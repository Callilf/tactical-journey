package com.dokkaebistudio.tacticaljourney.systems;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.dokkaebistudio.tacticaljourney.ai.enemies.EnemyActionSelector;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTileSearchService;
import com.dokkaebistudio.tacticaljourney.ai.movements.TileSearchService;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler.MovementProgressEnum;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class EnemySystem extends EntitySystem implements RoomSystem {
	
    /** The movement handler. */
    private final MovementHandler movementHandler;

    /** The room. */
    private Room room;
    
    /** The fx stage for attack animations. */
    private Stage fxStage;
   
    /** The enemies of the current room that need updating. */
    private List<Entity> allEnemiesOfCurrentRoom;
        
	/** The tile search service. */
	private TileSearchService tileSearchService;
	/** The attack tile search service. */
	private AttackTileSearchService attackTileSearchService;
	
	public static Entity enemyCurrentyPlaying;
	
	
	private int enemyFinishedCount = 0;


    public EnemySystem(Room r, Stage stage) {
		this.priority = 9;
		
		EnemySystem.enemyCurrentyPlaying = null;

		this.room = r;
        this.fxStage = stage;
        this.movementHandler = new MovementHandler(r.engine);
        this.allEnemiesOfCurrentRoom = new ArrayList<>();
		this.tileSearchService = new TileSearchService();
		this.attackTileSearchService = new AttackTileSearchService();
    }

    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }
    
    @Override
    public void update(float deltaTime) {
    	super.update(deltaTime);
    	
    	if (!room.getState().isEnemyTurn()) {
    		return;
    	}
    	
    	//Get all enemies of the current room
    	fillEntitiesOfCurrentRoom();
    	
    	if (room.getState() == RoomState.ENEMY_COMPUTE_TILES_TO_DISPLAY_TO_PLAYER) {
    		//Computing movable tiles of all enemies to display them to the player
    		computeMovableTilesToDisplayToPlayer();
    		return;
    	}
    	
    	
    	enemyFinishedCount = 0;
    	for (final Entity enemyEntity : allEnemiesOfCurrentRoom) {
    		HealthComponent healthComponent = Mappers.healthComponent.get(enemyEntity);
    		if (healthComponent != null && healthComponent.isDead()) {
    			continue;
    		}
    		
    		final EnemyComponent enemyComponent = Mappers.enemyComponent.get(enemyEntity);
    		if (enemyComponent.isTurnOver()) {
    			enemyFinishedCount ++;
    			continue;
    		}
    		
    		enemyCurrentyPlaying = enemyEntity;
    		
    		// Check if this enemy uses a sub system
    		if (enemyComponent.getSubSystem() != null) {
    			boolean enemyHandled = enemyComponent.getSubSystem().update(this, enemyEntity, room);
    			if (enemyHandled) {
    				checkAllEnemiesFinished();
    				return;
    			}
    		}
    		
    		
        	MoveComponent moveCompo = Mappers.moveComponent.get(enemyEntity);
        	final AttackComponent attackCompo = Mappers.attackComponent.get(enemyEntity);
        	GridPositionComponent enemyCurrentPos = Mappers.gridPositionComponent.get(enemyEntity);
    		
    		switch(room.getState()) {
        	case ENEMY_TURN_INIT :
            	
            	moveCompo.setMoveRemaining(moveCompo.getMoveSpeed());
            	enemyComponent.onStartTurn(enemyEntity, room);
            	room.setNextState(RoomState.ENEMY_COMPUTE_MOVABLE_TILES);
        		
        	case ENEMY_COMPUTE_MOVABLE_TILES :
        		
        		//clear the movable tile
        		moveCompo.clearMovableTiles();
//        		if (attackCompo != null) attackCompo.clearAttackableTiles();
            		
            	//Build the movable tiles list
        		tileSearchService.buildMoveTilesSet(enemyEntity, room);
//        		if (attackCompo != null) attackTileSearchService.buildAttackTilesSet(enemyEntity, room, true, false);
        		moveCompo.hideMovableTiles();
        		attackCompo.hideAttackableTiles();
        		room.setNextState(RoomState.ENEMY_MOVE_TILES_DISPLAYED);
        		
        		break;
        		
        	case ENEMY_MOVE_TILES_DISPLAYED :
        		
            	Entity selectedTile = EnemyActionSelector.selectTileToMove(enemyEntity, room, attackTileSearchService);
            		
            	if (selectedTile != null) {
            		GridPositionComponent destinationPos = Mappers.gridPositionComponent.get(selectedTile);
    		    	//Clicked on this tile !!
    				//Create an entity to show that this tile is selected as the destination
    				Entity destinationTileEntity = room.entityFactory.createDestinationTile(destinationPos.coord(), room);
    				moveCompo.setSelectedTile(destinationTileEntity);
    					
    				//Display the way to go to this point
    				List<Entity> waypoints = tileSearchService.buildWaypointList(enemyEntity,moveCompo, enemyCurrentPos, 
    						destinationPos, room);
    		       	moveCompo.setWayPoints(waypoints);
    		       	moveCompo.hideMovementEntities();
            		room.setNextState(RoomState.ENEMY_MOVE_DESTINATION_SELECTED);
            	} else {
            		room.setNextState(RoomState.ENEMY_END_MOVEMENT);
            	}
        		
        		break;
        		
        	case ENEMY_MOVE_DESTINATION_SELECTED :

        		movementHandler.initiateMovement(enemyEntity);
            	room.setNextState(RoomState.ENEMY_MOVING);

        		break;
        		
        	case ENEMY_MOVING:
        		if (moveCompo.moving) {
	    	    	moveCompo.selectCurrentMoveDestinationTile(enemyEntity);
	    	    		
	    	    	//Do the movement on screen
	    	    	MovementProgressEnum movementProgress = movementHandler.performRealMovement(enemyEntity, room);
	        		if (movementProgress == MovementProgressEnum.MOVEMENT_OVER) room.setNextState(RoomState.ENEMY_END_MOVEMENT);
        		} else {
        			room.setNextState(RoomState.ENEMY_END_MOVEMENT);
        		}
        		
        		break;
        		
        	case ENEMY_END_MOVEMENT:
        		if (moveCompo.moving) {
	        		MovementHandler.finishRealMovement(enemyEntity, room);
	    	    	moveCompo.clearMovableTiles();
	
	        		if (attackCompo != null) attackCompo.clearAttackableTiles();
	        		if (attackCompo != null) attackTileSearchService.buildAttackTilesSet(enemyEntity, room, true, false);
        		}
        		moveCompo.clearMovableTiles();

    	    	room.setNextState(RoomState.ENEMY_ATTACK);

        		break;
        		
        	case ENEMY_ATTACK:
        		
        		//Check if attack possible
        		boolean attacked = false;
    	    	if (attackCompo.isActive() && attackCompo.attackableTiles != null && !attackCompo.attackableTiles.isEmpty()) {
    	    		for (Entity attTile : attackCompo.attackableTiles) {
    	    			GridPositionComponent attTilePos = Mappers.gridPositionComponent.get(attTile);
    	    			int range = TileUtil.getDistanceBetweenTiles(enemyCurrentPos.coord(), attTilePos.coord());
						if (range <= attackCompo.getRangeMax() && range >= attackCompo.getRangeMin()) {
    	    				//Attack possible
							Entity target = TileUtil.getAttackableEntityOnTile(attTilePos.coord(), room);
							if (target != null) {
								EnemyComponent targetEnemyCompo = Mappers.enemyComponent.get(target);
								if (targetEnemyCompo != null && targetEnemyCompo.getFaction() == enemyComponent.getFaction()) {
									// Never attack member of the same faction
									continue;
								}
								
	            				attackCompo.setTarget(target);
	            				attackCompo.setTargetedTile(room.getTileAtGridPosition(attTilePos.coord()));
	            				attacked = true;
	            				room.setNextState(RoomState.ENEMY_ATTACK_ANIMATION);
	            				
	            				if (Mappers.playerComponent.has(target)) {
	            					// Prioritize attacks on the player
	            					break;
	            				}
							}
    	    			}
    	    		}
    	    	}
    	    	attackCompo.hideAttackableTiles();
    	    	
    	    	if (!attacked) {
    	    		room.setNextState(RoomState.ENEMY_ATTACK_FINISH);
    	    	}
    	    	
        		break;
        		
        	case ENEMY_ATTACK_ANIMATION:
        		
				Action finishAttackAction = new Action(){
				  @Override
				  public boolean act(float delta){
					room.attackManager.performAttack(enemyEntity, attackCompo);
    	    		room.setNextState(RoomState.ENEMY_ATTACK_FINISH);
				    return true;
				  }
				};
        		
				
				if (!attackCompo.getAttackAnimation().isPlaying()) {
					boolean hasAnim = attackCompo.setAttackImage(enemyCurrentPos.coord(), 
							attackCompo.getTargetedTile(), 
							null,
							fxStage,
							finishAttackAction);
					
					if (!hasAnim) {
						room.attackManager.performAttack(enemyEntity, attackCompo);
	    	    		room.setNextState(RoomState.ENEMY_ATTACK_FINISH);
					}
				}

        		break;
        		
        	case ENEMY_ATTACK_FINISH:
	    		finishOneEnemyTurn(enemyEntity, attackCompo, enemyComponent);
	    		break;
	    		
        	default:
        	}
    		
    		break;
    	}
    	
		//If all enemies have finished moving, end the turn
		checkAllEnemiesFinished();
    	
    }

	private void checkAllEnemiesFinished() {
		if (allEnemiesOfCurrentRoom.size() == 0 || enemyFinishedCount == allEnemiesOfCurrentRoom.size()) {
			for (Entity e : allEnemiesOfCurrentRoom) {
				Mappers.enemyComponent.get(e).setTurnOver(false);
			}
			enemyFinishedCount = 0;
			enemyCurrentyPlaying = null;
			room.turnManager.endEnemyTurn();
		}
	}

	public void finishOneEnemyTurn(Entity enemyEntity, AttackComponent attackCompo, EnemyComponent enemyComponent) {
    	attackCompo.clearAttackableTiles();
		attackCompo.clearAttackImage();
		
    	enemyComponent.onEndTurn(enemyEntity, room);

    	
		enemyFinishedCount ++;
		enemyComponent.setTurnOver(true);
		room.setNextState(RoomState.ENEMY_TURN_INIT);
	}

	private void fillEntitiesOfCurrentRoom() {
		allEnemiesOfCurrentRoom.clear();
		for (Entity e : room.getEnemies()) {
			allEnemiesOfCurrentRoom.add(e);
		}
	}

    
    /**
     * For each enemy, compute the list of tiles where they can move and attack.
     */
    private void computeMovableTilesToDisplayToPlayer() {
    	for (Entity enemyEntity : allEnemiesOfCurrentRoom) {
    		EnemyComponent enemyComponent = Mappers.enemyComponent.get(enemyEntity);
    		if (enemyComponent.getSubSystem() != null) {
    			boolean handledInSubSystem = enemyComponent.getSubSystem().computeMovableTilesToDisplayToPlayer(this, enemyEntity, room);
    			if (handledInSubSystem) continue;
    		}
    		
        	MoveComponent moveCompo = Mappers.moveComponent.get(enemyEntity);
        	AttackComponent attackCompo = Mappers.attackComponent.get(enemyEntity);
        	
    		//clear the movable tile
    		moveCompo.clearMovableTiles();
    		if (attackCompo != null) attackCompo.clearAttackableTiles();
    		
    		moveCompo.setMoveRemaining(moveCompo.getMoveSpeed());
        		
        	//Build the movable tiles list
    		tileSearchService.buildMoveTilesSet(enemyEntity, room);
    		if (attackCompo != null) attackTileSearchService.buildAttackTilesSet(enemyEntity, room, false, true);
    		moveCompo.hideMovableTiles();
    		if (attackCompo != null) attackCompo.hideAttackableTiles();
    	}
    	
    	room.setNextState(RoomState.PLAYER_MOVE_TILES_DISPLAYED);
    }

    
    
    
    
    
    
    
    
    //********************
    // Getters and setters
    
	public Stage getFxStage() {
		return fxStage;
	}

	public void setFxStage(Stage fxStage) {
		this.fxStage = fxStage;
	}

	public TileSearchService getTileSearchService() {
		return tileSearchService;
	}

	public void setTileSearchService(TileSearchService tileSearchService) {
		this.tileSearchService = tileSearchService;
	}

	public AttackTileSearchService getAttackTileSearchService() {
		return attackTileSearchService;
	}

	public void setAttackTileSearchService(AttackTileSearchService attackTileSearchService) {
		this.attackTileSearchService = attackTileSearchService;
	}

	public MovementHandler getMovementHandler() {
		return movementHandler;
	}
    
    
    
}
