package com.dokkaebistudio.tacticaljourney.ces.systems.iteratingsystems;

import java.util.List;
import java.util.Optional;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTileSearchService;
import com.dokkaebistudio.tacticaljourney.ai.movements.TileSearchService;
import com.dokkaebistudio.tacticaljourney.ces.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.loot.LootableComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.loot.LootableComponent.LootableStateEnum;
import com.dokkaebistudio.tacticaljourney.ces.components.player.AllyComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.ces.systems.NamedIteratingSystem;
import com.dokkaebistudio.tacticaljourney.enums.HealthChangeEnum;
import com.dokkaebistudio.tacticaljourney.enums.InventoryDisplayModeEnum;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.rendering.HUDRenderer;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.singletons.InputSingleton;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffStunned;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler.MovementProgressEnum;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.dokkaebistudio.tacticaljourney.vfx.VFXUtil;

public class PlayerMoveSystem extends NamedIteratingSystem {

	/** The movement handler. */
	private final MovementHandler movementHandler;

	/** The highlighted enemy. */
	public static Entity enemyHighlighted;
	
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
	protected void performProcessEntity(Entity player, float deltaTime) {
		
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
			moveCompo.setMoveRemaining(moveCompo.getMoveSpeed());
			room.setNextState(RoomState.PLAYER_COMPUTE_MOVABLE_TILES);

			AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(player);
			alterationReceiverComponent.onPlayerTurnStarts(player, room);
			
			break;

		case PLAYER_COMPUTE_MOVABLE_TILES:
			if (room.hasEnemies()) {
				moveCompo.setFreeMove(false);
			} else {
				moveCompo.setFreeMove(true);
			}
			
			if (moveCompo.getSelectedTileFromPreviousTurn() != null) {
				// Continue movement
				boolean canStillMove = true;
				
				int totalMoves = Mappers.moveComponent.get(player).getMoveRemaining() - 1;
				int currentMove = 0;
				
				for (Entity waypoint : moveCompo.getWayPoints()) {
					Tile tileAtGridPos = TileUtil.getTileAtGridPos(Mappers.gridPositionComponent.get(waypoint).coord(), room);
					canStillMove &= tileAtGridPos.isWalkable(player);
					
					Mappers.spriteComponent.get(waypoint).setSprite(currentMove <= totalMoves ? Assets.tile_movable_waypoint : Assets.tile_movable_waypoint_gray);
					
					currentMove += TileUtil.getCostOfMovementForTilePos(tileAtGridPos.getGridPos(), player, room);
				}
				canStillMove &= moveCompo.getSelectedTileFromPreviousTurn().isWalkable(player);
				Mappers.spriteComponent.get(moveCompo.getSelectedTile()).setSprite(currentMove <= totalMoves ? Assets.tile_movable_selected : Assets.tile_movable_selected_gray);

				if (canStillMove && !room.hasEnemies()) {
					room.setNextState(RoomState.PLAYER_MOVE_DESTINATION_SELECTED);
					break;
				}
			}
			
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

			room.setNextState(RoomState.ALLY_COMPUTE_TILES_TO_DISPLAY_TO_PLAYER);
			break;

		case PLAYER_MOVE_TILES_DISPLAYED:
						
			boolean stillLooting = handleLoot(player);
			if (stillLooting) return;
			
			// When right clicking on an ennemy, display it's possible movement
			handleRightClickOnEnemies(player);
			handleClickOnPlayer(player);
			handleClickOnAlly();
			
			if (moveCompo.getSelectedTileFromPreviousTurn() != null) {
				// Continue movement
				room.setNextState(RoomState.PLAYER_MOVE_DESTINATION_SELECTED);
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


			
			break;

		case PLAYER_MOVE_DESTINATION_SELECTED:
			// Either click on confirm to move or click on another tile to change the
			// destination
			if (InputSingleton.getInstance().leftClickJustReleased || (moveCompo.getSelectedTileFromPreviousTurn() != null && !room.hasEnemies())) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
				int y = (int) touchPoint.y;

				if (TileUtil.isPixelPosOnEntity(x, y, moveCompo.getSelectedTile()) || (moveCompo.getSelectedTileFromPreviousTurn() != null && !room.hasEnemies())) {
					// Confirm movement is we click on the selected tile again
					
					// Initiate movement
					movementHandler.initiateMovement(player);
					room.setNextState(RoomState.PLAYER_MOVING);
				} else if (moveCompo.getSelectedAttackTile() != null && TileUtil.isPixelPosOnEntity(x, y, moveCompo.getSelectedAttackTile())) {
					// Confirm movement by clicking on an attack tile
//					moveCompo.setFastAttack(true);
					GridPositionComponent attackTilePos = Mappers.gridPositionComponent.get(moveCompo.getSelectedAttackTile());
					moveCompo.setFastAttackTarget(TileUtil.getAttackableEntityOnTile(GameScreen.player, attackTilePos.coord(), room));
					
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
				}

			}

			break;

		case PLAYER_MOVING:
			if (moveCompo.moving) {
				moveCompo.selectCurrentMoveDestinationTile(player);
				
				// handle the case where the player has no move remaining, but is in a cleared room
				if (moveCompo.getMoveRemaining() <= 0 && !moveCompo.isFrozen()) {
					GridPositionComponent gridPosCompo = Mappers.gridPositionComponent.get(player);
					moveCompo.setEndTurnTile(TileUtil.getTileAtGridPos(gridPosCompo.coord(), room));
					Vector2 selectedTile = Mappers.gridPositionComponent.get(moveCompo.getSelectedTile()).coord();
					moveCompo.setSelectedTileFromPreviousTurn(TileUtil.getTileAtGridPos(selectedTile, room));
					room.setNextState(RoomState.PLAYER_PAUSE_MOVEMENT);
				}
	
				
				// Do the movement on screen
				MovementProgressEnum movementProgress = movementHandler.performRealMovement(player, room);
				if (movementProgress == null)
					return;
				else if (movementProgress == MovementProgressEnum.MOVEMENT_OVER) {
					moveCompo.clearSelectedTileFromPreviousTurn();
					room.setNextState(RoomState.PLAYER_END_MOVEMENT);
				} else if (movementProgress == MovementProgressEnum.TURN_OVER) {
					room.setNextState(RoomState.PLAYER_PAUSE_MOVEMENT);
				}
				
			} else {
				room.setNextState(RoomState.PLAYER_END_MOVEMENT);
			}

			break;
			
		case PLAYER_PAUSE_MOVEMENT:
			if (moveCompo.moving) {
				MovementHandler.finishRealMovement(player, room);
			}
			if (moveCompo.isFrozen()) {
				moveCompo.clearSelectedTileFromPreviousTurn();
			}
			
			
			room.turnManager.endPlayerTurn();
			break;

		case PLAYER_END_MOVEMENT:
			if (moveCompo.moving) {
				MovementHandler.finishRealMovement(player, room);
			}
			moveCompo.clearMovableTiles();
			room.setNextState(RoomState.PLAYER_COMPUTE_MOVABLE_TILES);

			break;
			
			
		case PLAYER_STUNNED:
			moveCompo.clearSelectedTileFromPreviousTurn();
			moveCompo.clearMovableTiles();
			attackCompo = Mappers.attackComponent.get(player);
			if (attackCompo != null) {
				attackCompo.clearAttackableTiles();
			}
			
			handleRightClickOnEnemies(player);
			handleClickOnPlayer(player);
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
					VFXUtil.createDamageDisplayer("INTERRUPTED", gridPos.coord(), HealthChangeEnum.HIT, 15, room);
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
				VFXUtil.createDamageDisplayer("INTERRUPTED", gridPos.coord(), HealthChangeEnum.HIT, 15, room);
			} else {
				inventoryComponent.setTurnsToWaitBeforeLooting(inventoryComponent.getTurnsToWaitBeforeLooting() - 1);
				room.turnManager.endPlayerTurn();
			}
		}
	}
	
	
	/**
	 * Click on the player to end the turn if no move remaining
	 */
	private void handleClickOnPlayer(Entity player) {		
//		if (moveCompo.getMoveRemaining() <= 0) {
			if (InputSingleton.getInstance().leftClickJustReleased) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
				int y = (int) touchPoint.y;
	
				if (TileUtil.isPixelPosOnEntity(x, y, player)) {
					
					// Do not end the turn if there is any entity that launches a contextual action on this tile
					if (TileUtil.hasEntityWithContextualActionOnClick(x, y, room)) return;
					
					moveCompo.clearMovableTiles();
					attackCompo.clearAttackableTiles();
					room.turnManager.endPlayerTurn();
				}
			}
//		}
	}
	
	/**
	 * Click on the player to end the turn if no move remaining
	 */
	private void handleClickOnAlly() {		
		if (InputSingleton.getInstance().leftClickJustReleased) {
			Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
			int x = (int) touchPoint.x;
			int y = (int) touchPoint.y;

			PoolableVector2 gridPos = TileUtil.convertPixelPosIntoGridPos(x, y);
			
			GridPositionComponent playerPos = Mappers.gridPositionComponent.get(GameScreen.player);
			int distance = TileUtil.getDistanceBetweenTiles(playerPos.coord(), gridPos);
			if (distance > 1) return;
			
			Optional<Entity> ally = TileUtil.getEntityWithComponentOnTile(gridPos, AllyComponent.class, room);
			if (!ally.isPresent() || ally.get() == GameScreen.player) return;
			
			// Check movement cost
			MoveComponent moveComponent = Mappers.moveComponent.get(GameScreen.player);
			int moveCost = TileUtil.getCostOfMovementForTilePos(gridPos, GameScreen.player, room);
			if (moveComponent.getMoveRemaining() < moveCost) return;
			
			// Swap possible
			VFXUtil.createSmokeEffect(gridPos);
			VFXUtil.createSmokeEffect(playerPos.coord());
			MovementHandler.placeEntity(ally.get(), playerPos.coord(), room);
			MovementHandler.placeEntity(GameScreen.player, gridPos, room);
			
			moveComponent.setMoveRemaining(moveComponent.getMoveRemaining() - moveCost);
			
			room.setNextState(RoomState.PLAYER_COMPUTE_MOVABLE_TILES);
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
				Optional<Entity> attackableEntity = TileUtil.getEntityWithComponentOnTile(tempPos, AttackComponent.class, room);
				tempPos.free();
				
				if (attackableEntity.isPresent()) {
					timer += Gdx.graphics.getDeltaTime();
					
					if (enemyHighlighted != null && attackableEntity.get() != enemyHighlighted) {
						hideEnemyTiles(player);
					} else if (timer >= 0.5f) {
						displayEnemyTiles(player, attackableEntity.get());
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
					InputSingleton.getInstance().leftClickJustReleased = false;
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
			Optional<Entity> attackableEntity = TileUtil.getEntityWithComponentOnTile(tempPos, AttackComponent.class, room);
			tempPos.free();
			
			if (attackableEntity.isPresent()) {
				displayEnemyTiles(player, attackableEntity.get());
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
		
		HUDRenderer.hideTargetMaker();
	}

	private void displayEnemyTiles(Entity player, Entity attackableEntity) {
		// Hide the player's tiles
		MoveComponent playerMoveComponent = Mappers.moveComponent.get(player);
		if (playerMoveComponent != null) {
			playerMoveComponent.hideMovableTiles();
		}
		AttackComponent playerAttackComponent = Mappers.attackComponent.get(player);
		if (playerAttackComponent != null) {
			playerAttackComponent.hideAttackableTiles();
		}
		
		enemyHighlighted = attackableEntity;

		
		StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(attackableEntity);
		if (statusReceiverComponent != null && statusReceiverComponent.hasStatus(StatusDebuffStunned.class)) return;
		
		// There is an enemy at this location, display it's tiles
		MoveComponent enemyMoveCompo = Mappers.moveComponent.get(attackableEntity);
		if (enemyMoveCompo != null) {
			enemyMoveCompo.showMovableTiles();
		}
		AttackComponent enemyAttackCompo = Mappers.attackComponent.get(attackableEntity);
		if (enemyAttackCompo != null) {
			enemyAttackCompo.showAttackableTiles();
		}
		
		AIComponent aiComponent = Mappers.aiComponent.get(attackableEntity);
		if (aiComponent != null && aiComponent.getTarget() != null) {
			HUDRenderer.displayTargetMaker(Mappers.gridPositionComponent.get(aiComponent.getTarget()).coord());
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
		
		
//		for (Entity tile : moveCompo.movableTiles) {
//			GridPositionComponent destinationPos = Mappers.gridPositionComponent.get(tile);
//			if (destinationPos.coord().equals(moverCurrentPos.coord())) {
//				continue;
//			}
//			
//			if (TileUtil.isPixelPosOnEntity(x, y, tile)) {
//				moveCompo.setSelectedAttackTile(null);
//				return selectTileAndBuildWaypoints(moverEntity, destinationPos);
//			}
//		}
		
		
		for (Entity tile : attackCompo.attackableTiles) {
			SpriteComponent spriteComponent = Mappers.spriteComponent.get(tile);
			GridPositionComponent destinationPos = Mappers.gridPositionComponent.get(tile);

			if (spriteComponent.containsPoint(x, y)) {
				int distance = TileUtil.getDistanceBetweenTiles(moverCurrentPos.coord(), destinationPos.coord());
				if (distance > attackCompo.getMainSkill().getRangeMax()) {
					
					//Select a tile close enough to attack
					for (Entity movableTile : moveCompo.movableTiles) {
						
						// TODO improve the tile selection later
						GridPositionComponent movableTilePos = Mappers.gridPositionComponent.get(movableTile);
						int dist = TileUtil.getDistanceBetweenTiles(movableTilePos.coord(), destinationPos.coord());
						if (dist >= attackCompo.getMainSkill().getRangeMin() && dist <= attackCompo.getMainSkill().getRangeMax()) {
							// Select this tile
							moveCompo.setSelectedAttackTile(tile);
							return selectTileAndBuildWaypoints(moverEntity, movableTilePos.coord());
						}
					}
					
				}
			}
		}
		
		PoolableVector2 gridPos = TileUtil.convertPixelPosIntoGridPos(x, y);
		if (gridPos.equals(moverCurrentPos.coord()) || TileUtil.gridPosOutOfRoom(gridPos)) {
			return false;
		}

		moveCompo.setSelectedAttackTile(null);
		return selectTileAndBuildWaypoints(moverEntity, gridPos);
		
		
		
//		return false;
	}

	private boolean selectTileAndBuildWaypoints(Entity moverEntity, Vector2 destinationPos) {
		// Check whether we can find a path to this tile
		List<Entity> waypoints = TileSearchService.buildWaypointList(moverEntity, moveCompo, moverCurrentPos.coord(), 
				destinationPos, room, false);

		if (waypoints == null) {
			// No path found
			return false;
		}
		Entity destination = waypoints.remove(waypoints.size() - 1);
		moveCompo.setWayPoints(waypoints);
		moveCompo.setSelectedTile(destination, room);

		room.setNextState(RoomState.PLAYER_MOVE_DESTINATION_SELECTED);
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
