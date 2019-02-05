package com.dokkaebistudio.tacticaljourney.systems;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTileSearchService;
import com.dokkaebistudio.tacticaljourney.ai.movements.TileSearchService;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.LootableComponent;
import com.dokkaebistudio.tacticaljourney.components.LootableComponent.LootableStateEnum;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.enums.InventoryDisplayModeEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;
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
	private GridPositionComponent moverCurrentPos;
	private InventoryComponent inventoryComponent;
	private HealthComponent healthComponent;

	//TEST
	float timer = 0;

	public PlayerMoveSystem(Room room) {
		super(Family.all(PlayerComponent.class, GridPositionComponent.class).get());
		this.priority = 10;

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
	protected void processEntity(Entity moverEntity, float deltaTime) {
		
		if (playerCompo == null) {
			moveCompo = Mappers.moveComponent.get(moverEntity);
			moverCurrentPos = Mappers.gridPositionComponent.get(moverEntity);
			playerCompo = Mappers.playerComponent.get(moverEntity);
			inventoryComponent = Mappers.inventoryComponent.get(moverEntity);
			healthComponent = Mappers.healthComponent.get(moverEntity);
		}

		if (!room.getState().isPlayerTurn()) {
			return;
		}
		
		boolean waitingForLooting = inventoryComponent.getTurnsToWaitBeforeLooting() != null;
		
		switch (room.getState()) {

		case PLAYER_TURN_INIT:	
			if (room.hasEnemies()) {
				moveCompo.moveRemaining = moveCompo.moveSpeed;
				moveCompo.freeMove = false;
			} else {
				moveCompo.moveRemaining = 30;
				moveCompo.freeMove = true;
			}
			room.setNextState(RoomState.PLAYER_COMPUTE_MOVABLE_TILES);

		case PLAYER_COMPUTE_MOVABLE_TILES:
			
			// clear the movable tile
			moveCompo.clearMovableTiles();
			AttackComponent attackCompo = Mappers.attackComponent.get(moverEntity);
			if (attackCompo != null)
				attackCompo.clearAttackableTiles();

			// Build the movable tiles list
			tileSearchService.buildMoveTilesSet(moverEntity, room);
			if (attackCompo != null)
				attackTileSearchService.buildAttackTilesSet(moverEntity, room,true, false);

			if (!room.hasEnemies()) {
				moveCompo.hideMovableTiles();
			}

			room.setNextState(RoomState.ENEMY_COMPUTE_TILES_TO_DISPLAY_TO_PLAYER);
			break;

		case PLAYER_MOVE_TILES_DISPLAYED:
			
			boolean looting = handleLoot(moverEntity,  waitingForLooting);
			if (looting) return;
			
			// When clicking on a moveTile, display it as the destination
			if (InputSingleton.getInstance().leftClickJustReleased) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
				int y = (int) touchPoint.y;

				boolean selected = selectDestinationTile(x, y);
				if (selected) {
					room.setNextState(RoomState.PLAYER_MOVE_DESTINATION_SELECTED);
				}
			}

			// When right clicking on an ennemy, display it's possible movement
			handleRightClickOnEnemies(moverEntity);

			break;

		case PLAYER_MOVE_DESTINATION_SELECTED:
			// Either click on confirm to move or click on another tile to change the
			// destination
			if (InputSingleton.getInstance().leftClickJustReleased) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
				int y = (int) touchPoint.y;

				SpriteComponent selectedTileSprite = Mappers.spriteComponent.get(moveCompo.getSelectedTile());
				SpriteComponent playerSprite = Mappers.spriteComponent.get(moverEntity);

				if (selectedTileSprite.containsPoint(x, y)) {
					// Confirm movement is we click on the selected tile again

					// Initiate movement
					movementHandler.initiateMovement(moverEntity);

					room.setNextState(RoomState.PLAYER_MOVING);
				} else if (playerSprite.containsPoint(x, y)) {
					// Cancel movement is we click on the character
					moveCompo.clearSelectedTile();
					room.setNextState(RoomState.PLAYER_MOVE_TILES_DISPLAYED);
				} else {
					// No confirmation, check if another tile has been selected
					selectDestinationTile(x, y);
					room.setNextState(RoomState.PLAYER_MOVE_DESTINATION_SELECTED);
				}

			}

			break;

		case PLAYER_MOVING:
			moveCompo.selectCurrentMoveDestinationTile();

			// Do the movement on screen
			Boolean movementFinished = movementHandler.performRealMovement(moverEntity, room);
			if (movementFinished == null)
				return;
			else if (movementFinished)
				room.setNextState(RoomState.PLAYER_END_MOVEMENT);

			break;

		case PLAYER_END_MOVEMENT:
			movementHandler.finishRealMovement(moverEntity, room);

			// Compute the cost of this move
			if (room.hasEnemies()) {
				int cost = computeCostOfMovement();
				moveCompo.moveRemaining = moveCompo.moveRemaining - cost;
			}

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
	 * @param waitingForLooting 
	 * @return true if the player is looting or opening a lootable
	 */
	private boolean handleLoot(Entity moverEntity, boolean waitingForLooting) {
		
		if (waitingForLooting) {
			handleWaitForLooting(moverEntity);
			return true;
		}
		
		if (inventoryComponent.isInventoryActionInProgress()) {
			inventoryComponent.setDisplayMode(InventoryDisplayModeEnum.LOOT);
			if (healthComponent.isReceivedDamageLastTurn()) {
				// INTERRUPTED
				inventoryComponent.interrupt();
				if (inventoryComponent != null && inventoryComponent.isInterrupted()) {
					GridPositionComponent gridPos = Mappers.gridPositionComponent.get(moverEntity);
					room.entityFactory.createDamageDisplayer("INTERRUPTED", gridPos.coord(), false, 15, room);
				}
			} else {
				inventoryComponent.setInventoryActionInProgress(false);
				inventoryComponent.setNeedInventoryRefresh(true);
				return true;
			}
		}
	
		return false;
	}

	/**
	 * Update the number of turns to wait for opening a lootable.
	 */
	private void handleWaitForLooting(Entity player) {
		if (inventoryComponent.getTurnsToWaitBeforeLooting().intValue() <= 0) {
			inventoryComponent.setDisplayMode(InventoryDisplayModeEnum.LOOT);
			inventoryComponent.setTurnsToWaitBeforeLooting(null);
			LootableComponent lootableComponent = Mappers.lootableComponent.get(inventoryComponent.getLootableEntity());
			lootableComponent.setLootableState(LootableStateEnum.OPENED);
		} else {
			
			//TODO
			if (healthComponent.isReceivedDamageLastTurn()) {
				//INTERRUPTED
				inventoryComponent.setTurnsToWaitBeforeLooting(null);
				
				GridPositionComponent gridPos = Mappers.gridPositionComponent.get(player);
				room.entityFactory.createDamageDisplayer("INTERRUPTED", gridPos.coord(), false, 15, room);
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
		if (Gdx.input.isTouched()) {
		  //Finger touching the screen
		  // You can actually start calling onClick here, if those variables and logic you are using there are correct.
			Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
			int x = (int) touchPoint.x;
			int y = (int) touchPoint.y;
			
			Vector2 gridPos = TileUtil.convertPixelPosIntoGridPos(new Vector2(x, y));
			Entity attackableEntity = TileUtil.getAttackableEntityOnTile(gridPos, room);
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

		
		if (InputSingleton.getInstance().rightClickJustPressed) {
			Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
			int x = (int) touchPoint.x;
			int y = (int) touchPoint.y;

			Vector2 gridPos = TileUtil.convertPixelPosIntoGridPos(new Vector2(x, y));
			Entity attackableEntity = TileUtil.getAttackableEntityOnTile(gridPos, room);
			if (attackableEntity != null) {
				displayEnemyTiles(player, attackableEntity);
			}
		} else if (InputSingleton.getInstance().rightClickJustReleased && enemyHighlighted != null) {
			// Released right click
			hideEnemyTiles(player);
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
	private boolean selectDestinationTile(int x, int y) {
		for (Entity tile : moveCompo.movableTiles) {
			SpriteComponent spriteComponent = Mappers.spriteComponent.get(tile);
			GridPositionComponent destinationPos = Mappers.gridPositionComponent.get(tile);

			if (destinationPos.coord().equals(moverCurrentPos.coord())) {
				// Cannot move to the tile we already are
				continue;
			}

			if (spriteComponent.containsPoint(x, y)) {
				// Clicked on this tile !!
				// Create an entity to show that this tile is selected as the destination
				Entity destinationTileEntity = room.entityFactory.createDestinationTile(destinationPos.coord(), room);
				moveCompo.setSelectedTile(destinationTileEntity);

				// Display the way to go to this point
				List<Entity> waypoints = tileSearchService.buildWaypointList(moveCompo, moverCurrentPos, destinationPos,
						room);
				moveCompo.setWayPoints(waypoints);

				return true;
			}

		}
		return false;
	}
	
	/**
	 * Return the cost of movement
	 * 
	 * @param moveCompo the moveComponent
	 * @return the cost of movement
	 */
	private int computeCostOfMovement() {
		int cost = 0;
		for (Entity wp : moveCompo.getWayPoints()) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(wp);
			cost = cost + TileUtil.getCostOfMovementForTilePos(gridPositionComponent.coord(), room);
		}
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(moveCompo.getSelectedTile());
		cost = cost + TileUtil.getCostOfMovementForTilePos(gridPositionComponent.coord(), room);
		return cost;
	}


}
