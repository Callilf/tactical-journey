package com.dokkaebistudio.tacticaljourney.systems;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTileSearchService;
import com.dokkaebistudio.tacticaljourney.ai.movements.TileSearchService;
import com.dokkaebistudio.tacticaljourney.ashley.PublicPooledEngine;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootableComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootableComponent.LootableStateEnum;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.enums.HealthChangeEnum;
import com.dokkaebistudio.tacticaljourney.enums.InventoryDisplayModeEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.persistence.Persister;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class PlayerMoveSystem extends IteratingSystem implements RoomSystem {

	/** The movement handler. */
	private final MovementHandler movementHandler;

	/** The current room. */
	private Room room;

	/** The highlighted enemy. */
	private Entity enemyHighlighted;
	
	/** The tile search service. */
	private TileSearchService tileSearchService;
	/** The attack tile search service. */
	private AttackTileSearchService attackTileSearchService;
	
	
	//******************
	// component cache
	
	private PlayerComponent playerCompo;
	private MoveComponent moveCompo;
	private AttackComponent attackCompo;
	private GridPositionComponent moverCurrentPos;
	private InventoryComponent inventoryComponent;
	private HealthComponent healthComponent;

	//TEST
	float timer = 0;

	public PlayerMoveSystem(Room room) {
		super(Family.all(PlayerComponent.class, GridPositionComponent.class).get());
		this.priority = 11;

		this.room = room;
		this.movementHandler = new MovementHandler(room.engine);
		this.tileSearchService = new TileSearchService();
		this.attackTileSearchService = new AttackTileSearchService();
	}

	@Override
	public void enterRoom(Room newRoom) {
		this.room = newRoom;
	}

	@Override
	protected void processEntity(Entity player, float deltaTime) {
		
		if (playerCompo == null) {
			moveCompo = Mappers.moveComponent.get(player);
			moverCurrentPos = Mappers.gridPositionComponent.get(player);
			playerCompo = Mappers.playerComponent.get(player);
			inventoryComponent = Mappers.inventoryComponent.get(player);
			healthComponent = Mappers.healthComponent.get(player);
			attackCompo = Mappers.attackComponent.get(player);
		}

		if (!room.getState().isPlayerTurn()) {
			return;
		}
		
		boolean isLooting = inventoryComponent.getTurnsToWaitBeforeLooting() != null || inventoryComponent.isInventoryActionInProgress();
				
		switch (room.getState()) {

		case PLAYER_TURN_INIT:	
			if (room.hasEnemies()) {
				moveCompo.setMoveRemaining(moveCompo.getMoveSpeed());
				moveCompo.freeMove = false;
			} else {
				moveCompo.freeMove = true;
			}
			room.setNextState(RoomState.PLAYER_COMPUTE_MOVABLE_TILES);
			break;

		case PLAYER_COMPUTE_MOVABLE_TILES:
			
			// clear the movable tile
			moveCompo.clearMovableTiles();
			AttackComponent attackCompo = Mappers.attackComponent.get(player);
			if (attackCompo != null)
				attackCompo.clearAttackableTiles();

			// Build the movable tiles list
			tileSearchService.buildMoveTilesSet(player, room);
			if (attackCompo != null)
				attackTileSearchService.buildAttackTilesSet(player, room,true, true);

			if (!room.hasEnemies() || isLooting) {
				moveCompo.hideMovableTiles();
			}

			room.setNextState(RoomState.ENEMY_COMPUTE_TILES_TO_DISPLAY_TO_PLAYER);
			break;

		case PLAYER_MOVE_TILES_DISPLAYED:
						
			boolean stillLooting = handleLoot(player);
			if (stillLooting) return;
			
			if (!room.hasEnemies() && !moveCompo.freeMove) {
				room.turnManager.endPlayerTurn();
				break;
			}
			
			// When clicking on a moveTile, display it as the destination
			if (InputSingleton.getInstance().leftClickJustReleased) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
				int y = (int) touchPoint.y;

				boolean selected = selectDestinationTile(player,x, y);
				if (selected) {
					room.setNextState(RoomState.PLAYER_MOVE_DESTINATION_SELECTED);
				}
			}

			// When right clicking on an ennemy, display it's possible movement
			handleRightClickOnEnemies(player);

			break;

		case PLAYER_MOVE_DESTINATION_SELECTED:
			// Either click on confirm to move or click on another tile to change the
			// destination
			if (InputSingleton.getInstance().leftClickJustReleased) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
				int y = (int) touchPoint.y;

				if (TileUtil.isPixelPosOnEntity(x, y, moveCompo.getSelectedTile())) {
					// Confirm movement is we click on the selected tile again

					// Initiate movement
					movementHandler.initiateMovement(player);
					room.setNextState(RoomState.PLAYER_MOVING);
				} else if (moveCompo.getSelectedAttackTile() != null && TileUtil.isPixelPosOnEntity(x, y, moveCompo.getSelectedAttackTile())) {
					// Confirm movement by clicking on an attack tile
//					moveCompo.setFastAttack(true);
					GridPositionComponent attackTilePos = Mappers.gridPositionComponent.get(moveCompo.getSelectedAttackTile());
					moveCompo.setFastAttackTarget(TileUtil.getAttackableEntityOnTile(attackTilePos.coord(), room));
					
					// Initiate movement
					movementHandler.initiateMovement(player);
					room.setNextState(RoomState.PLAYER_MOVING);
				} else if (TileUtil.isPixelPosOnEntity(x, y, player)) {
					// Cancel movement is we click on the character
					moveCompo.clearSelectedTile();
					room.setNextState(RoomState.PLAYER_MOVE_TILES_DISPLAYED);
				} else {
					// No confirmation, check if another tile has been selected
					selectDestinationTile(player,x, y);
					room.setNextState(RoomState.PLAYER_MOVE_DESTINATION_SELECTED);
				}

			}

			break;

		case PLAYER_MOVING:
			if (moveCompo.moving) {
				moveCompo.selectCurrentMoveDestinationTile(player);
	
				// Do the movement on screen
				Boolean movementFinished = movementHandler.performRealMovement(player, room);
				if (movementFinished == null)
					return;
				else if (movementFinished)
					room.setNextState(RoomState.PLAYER_END_MOVEMENT);
				
			} else {
				room.setNextState(RoomState.PLAYER_END_MOVEMENT);
			}

			break;

		case PLAYER_END_MOVEMENT:
			if (moveCompo.moving) {
				MovementHandler.finishRealMovement(player, room);
	
				// Compute the cost of this move
				if (room.hasEnemies()) {
					int cost = computeCostOfMovement(player);
					moveCompo.setMoveRemaining(moveCompo.getMoveRemaining() - cost);
				}

			}
			moveCompo.clearMovableTiles();
			room.setNextState(RoomState.PLAYER_COMPUTE_MOVABLE_TILES);

			break;

		default:
			break;

		}
	}
	
	
	
	
	//**********************************
	// LOOT related methods

	/**
	 * Handle the loot.
	 * Check whether the player is currently looting or opening a lootable.
	 * @param moverEntity the player
	 * @return true if the player is looting or opening a lootable
	 */
	private boolean handleLoot(Entity moverEntity) {
		boolean isLooting = false;
		
		if (inventoryComponent.getTurnsToWaitBeforeLooting() != null) {
			handleWaitForLooting(moverEntity);
			isLooting = true;
		}
		
		if (!isLooting && inventoryComponent.isInventoryActionInProgress()) {
			inventoryComponent.setDisplayMode(InventoryDisplayModeEnum.LOOT);
			if (healthComponent.isReceivedDamageLastTurn()) {
				// INTERRUPTED
				inventoryComponent.interrupt();
				if (inventoryComponent != null && inventoryComponent.isInterrupted()) {
					Journal.addEntry("[SCARLET]Interrupted while looting");
					GridPositionComponent gridPos = Mappers.gridPositionComponent.get(moverEntity);
					room.entityFactory.createDamageDisplayer("INTERRUPTED", gridPos, HealthChangeEnum.HIT, 15, room);
				}
			} else {
				inventoryComponent.setInventoryActionInProgress(false);
				inventoryComponent.setNeedInventoryRefresh(true);
				room.setNextState(RoomState.LOOT_POPIN);
				isLooting = true;
			}
		}
	
		if (isLooting) {
			moveCompo.hideMovableTiles();
		} else {
			moveCompo.showMovableTiles();
		}
		return isLooting;
	}

	/**
	 * Update the number of turns to wait for opening a lootable.
	 */
	private void handleWaitForLooting(Entity player) {
		LootableComponent lootableComponent = Mappers.lootableComponent.get(inventoryComponent.getLootableEntity());
		if (inventoryComponent.getTurnsToWaitBeforeLooting().intValue() <= 0) {
			inventoryComponent.setDisplayMode(InventoryDisplayModeEnum.LOOT);
			inventoryComponent.setTurnsToWaitBeforeLooting(null);
			lootableComponent.setLootableState(LootableStateEnum.OPENED, inventoryComponent.getLootableEntity());
			Journal.addEntry("You opened " + lootableComponent.getType().getLabel());
		} else {
			if (healthComponent.isReceivedDamageLastTurn()) {
				//INTERRUPTED
				Journal.addEntry("[SCARLET]Interrupted while opening " + lootableComponent.getType().getLabel());
				inventoryComponent.setTurnsToWaitBeforeLooting(null);
				
				GridPositionComponent gridPos = Mappers.gridPositionComponent.get(player);
				room.entityFactory.createDamageDisplayer("INTERRUPTED", gridPos, HealthChangeEnum.HIT, 15, room);
			} else {
				inventoryComponent.setTurnsToWaitBeforeLooting(inventoryComponent.getTurnsToWaitBeforeLooting() - 1);
				room.turnManager.endPlayerTurn();
			}
		}
	}
	
	
	
	
	
	//****************************
	// Enemy related methods

	/**
	 * Holding right click on an enemy displays it's possible movements and attacks.
	 */
	private void handleRightClickOnEnemies(Entity player) {
		if (Gdx.app.getType() == ApplicationType.Android) {
			if (Gdx.input.isTouched()) {
			  //Finger touching the screen
			  // You can actually start calling onClick here, if those variables and logic you are using there are correct.
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
				int y = (int) touchPoint.y;
				
				PoolableVector2 tempPos = TileUtil.convertPixelPosIntoGridPos(x, y);
				Entity attackableEntity = TileUtil.getEntityWithComponentOnTile(tempPos, AttackComponent.class, room);
				tempPos.free();
				
				if (attackableEntity != null) {
					timer += Gdx.graphics.getDeltaTime();
					
					if (enemyHighlighted != null && attackableEntity != enemyHighlighted) {
						hideEnemyTiles(player);
					} else if (timer >= 0.5f) {
						displayEnemyTiles(player, attackableEntity);
					}
				} else {
					if (enemyHighlighted != null) {
						hideEnemyTiles(player);
					}
				}
			} else {
				timer = 0;
				if (enemyHighlighted != null) {
					hideEnemyTiles(player);
				}
			}
		}

		
		if (InputSingleton.getInstance().rightClickJustReleased && enemyHighlighted != null) {
			// Released right click
			hideEnemyTiles(player);
		} else if (InputSingleton.getInstance().rightClickJustPressed || enemyHighlighted != null) {
			Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
			int x = (int) touchPoint.x;
			int y = (int) touchPoint.y;

			PoolableVector2 tempPos = TileUtil.convertPixelPosIntoGridPos(x, y);
			Entity attackableEntity = TileUtil.getEntityWithComponentOnTile(tempPos, AttackComponent.class, room);
			tempPos.free();
			
			if (attackableEntity != null) {
				displayEnemyTiles(player, attackableEntity);
			} else if (enemyHighlighted != null) {
				hideEnemyTiles(player);
			}
		}
	}

	private void hideEnemyTiles(Entity player) {
		// hide the enemy tiles
		MoveComponent enemyMoveCompo = Mappers.moveComponent.get(enemyHighlighted);
		if (enemyMoveCompo != null) {
			enemyMoveCompo.hideMovableTiles();
		}
		AttackComponent enemyAttackCompo = Mappers.attackComponent.get(enemyHighlighted);
		if (enemyAttackCompo != null) {
			enemyAttackCompo.hideAttackableTiles();
		}
		enemyHighlighted = null;

		// Display the player's ones
		// Hide the player's tiles
		MoveComponent playerMoveComponent = Mappers.moveComponent.get(player);
		if (playerMoveComponent != null) {
			playerMoveComponent.showMovableTiles();
		}
		AttackComponent playerAttackComponent = Mappers.attackComponent.get(player);
		if (playerAttackComponent != null) {
			playerAttackComponent.showAttackableTiles();
		}
	}

	private void displayEnemyTiles(Entity player, Entity attackableEntity) {
		// There is an enemy at this location, display it's tiles
		MoveComponent enemyMoveCompo = Mappers.moveComponent.get(attackableEntity);
		if (enemyMoveCompo != null) {
			enemyHighlighted = attackableEntity;
			enemyMoveCompo.showMovableTiles();
		}
		AttackComponent enemyAttackCompo = Mappers.attackComponent.get(attackableEntity);
		if (enemyAttackCompo != null) {
			enemyHighlighted = attackableEntity;
			enemyAttackCompo.showAttackableTiles();
		}
		// Hide the player's tiles
		MoveComponent playerMoveComponent = Mappers.moveComponent.get(player);
		if (playerMoveComponent != null) {
			playerMoveComponent.hideMovableTiles();
		}
		AttackComponent playerAttackComponent = Mappers.attackComponent.get(player);
		if (playerAttackComponent != null) {
			playerAttackComponent.hideAttackableTiles();
		}
	}
	


	
	
	//************************************
	// Movement computation and selection

	/**
	 * Set the destination of the movement.
	 * 
	 * @param moveCompo       the moveComponent
	 * @param x               the abscissa of the destination
	 * @param y               the ordinate of the destination
	 * @param moverCurrentPos the current position of the mover
	 */
	private boolean selectDestinationTile(Entity moverEntity, int x, int y) {
		for (Entity tile : moveCompo.movableTiles) {
			GridPositionComponent destinationPos = Mappers.gridPositionComponent.get(tile);

			if (destinationPos.coord().equals(moverCurrentPos.coord())) {
				// Cannot move to the tile we already are
				continue;
			}

			
			if (TileUtil.isPixelPosOnEntity(x, y, tile)) {
				moveCompo.setSelectedAttackTile(null);
				return selectTileAndBuildWaypoints(moverEntity, destinationPos);
			}
		}
		
		
		for (Entity tile : attackCompo.attackableTiles) {
			SpriteComponent spriteComponent = Mappers.spriteComponent.get(tile);
			GridPositionComponent destinationPos = Mappers.gridPositionComponent.get(tile);

			if (spriteComponent.containsPoint(x, y)) {
				int distance = TileUtil.getDistanceBetweenTiles(moverCurrentPos.coord(), destinationPos.coord());
				if (distance > attackCompo.getRangeMax()) {
					
					//Select a tile close enough to attack
					for (Entity movableTile : moveCompo.movableTiles) {
						
						// TODO improve the tile selection later
						GridPositionComponent movableTilePos = Mappers.gridPositionComponent.get(movableTile);
						int dist = TileUtil.getDistanceBetweenTiles(movableTilePos.coord(), destinationPos.coord());
						if (dist >= attackCompo.getRangeMin() && dist <= attackCompo.getRangeMax()) {
							// Select this tile
							moveCompo.setSelectedAttackTile(tile);
							return selectTileAndBuildWaypoints(moverEntity, movableTilePos);
						}
					}
					
				}
			}
		}
		return false;
	}

	private boolean selectTileAndBuildWaypoints(Entity moverEntity, GridPositionComponent destinationPos) {
		// Check whether we can find a path to this tile
		List<Entity> waypoints = tileSearchService.buildWaypointList(moverEntity, moveCompo, moverCurrentPos, 
				destinationPos, room);

		if (waypoints == null) {
			// No path found
			return false;
		}
		moveCompo.setWayPoints(waypoints);
		
		// Create an entity to show that this tile is selected as the destination
		Entity destinationTileEntity = room.entityFactory.createDestinationTile(destinationPos.coord(), room);		
		moveCompo.setSelectedTile(destinationTileEntity);

		return true;
	}
	
	/**
	 * Return the cost of movement
	 * 
	 * @param moveCompo the moveComponent
	 * @return the cost of movement
	 */
	private int computeCostOfMovement(Entity mover) {
		int cost = 0;
		for (Entity wp : moveCompo.getWayPoints()) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(wp);
			cost = cost + TileUtil.getCostOfMovementForTilePos(gridPositionComponent.coord(), mover, room);
		}
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(moveCompo.getSelectedTile());
		cost = cost + TileUtil.getCostOfMovementForTilePos(gridPositionComponent.coord(), mover, room);
		return cost;
	}


}
