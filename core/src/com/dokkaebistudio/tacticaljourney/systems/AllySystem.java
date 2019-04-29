package com.dokkaebistudio.tacticaljourney.systems;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.enemies.EnemyActionSelector;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTileSearchService;
import com.dokkaebistudio.tacticaljourney.ai.movements.TileSearchService;
import com.dokkaebistudio.tacticaljourney.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler.MovementProgressEnum;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class AllySystem extends EntitySystem implements RoomSystem {
	
    /** The movement handler. */
    private final MovementHandler movementHandler;

    /** The room. */
    private Room room;
    
    /** The fx stage for attack animations. */
    private Stage fxStage;
   
    /** The enemies of the current room that need updating. */
    private List<Entity> allAlliesOfCurrentRoom;
        
	/** The tile search service. */
	private TileSearchService tileSearchService;
	/** The attack tile search service. */
	private AttackTileSearchService attackTileSearchService;
	
	public static Entity allyCurrentyPlaying;
	
	
	private int allyFinishedCount = 0;


    public AllySystem(Room r, Stage stage) {
		this.priority = 9;
		
		AllySystem.allyCurrentyPlaying = null;

		this.room = r;
        this.fxStage = stage;
        this.movementHandler = new MovementHandler(r.engine);
        this.allAlliesOfCurrentRoom = new ArrayList<>();
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
    	
    	if (!room.getState().isAllyTurn()) {
    		return;
    	}
    	
    	//Get all enemies of the current room
    	fillEntitiesOfCurrentRoom();
    	
    	if (room.getState() == RoomState.ALLY_COMPUTE_TILES_TO_DISPLAY_TO_PLAYER) {
    		//Computing movable tiles of all enemies to display them to the player
    		computeMovableTilesToDisplayToPlayer();
    		return;
    	}
    	
    	
    	allyFinishedCount = 0;
    	for (final Entity allyEntity : allAlliesOfCurrentRoom) {
    		HealthComponent healthComponent = Mappers.healthComponent.get(allyEntity);
    		if (healthComponent != null && healthComponent.isDead()) {
    			continue;
    		}
    		
    		final AIComponent aiCompo = Mappers.aiComponent.get(allyEntity);
    		if (aiCompo.isTurnOver()) {
    			allyFinishedCount ++;
    			continue;
    		}
    		
    		allyCurrentyPlaying = allyEntity;
    		
//    		// Check if this enemy uses a sub system
//    		if (aiComponent.getSubSystem() != null) {
//    			boolean enemyHandled = aiComponent.getSubSystem().update(this, allyEntity, room);
//    			if (enemyHandled) {
//    				checkAllAlliesFinished();
//    				return;
//    			}
//    		}
    		
        	MoveComponent moveCompo = Mappers.moveComponent.get(allyEntity);
        	final AttackComponent attackCompo = Mappers.attackComponent.get(allyEntity);
        	GridPositionComponent enemyCurrentPos = Mappers.gridPositionComponent.get(allyEntity);
    		
    		switch(room.getState()) {
        	case ALLY_TURN_INIT :
            	
            	moveCompo.setMoveRemaining(moveCompo.getMoveSpeed());
            	aiCompo.onStartTurn(allyEntity, room);
            	room.setNextState(RoomState.ALLY_COMPUTE_MOVABLE_TILES);
        		
        	case ALLY_COMPUTE_MOVABLE_TILES :
        		
        		//clear the movable tile
        		moveCompo.clearMovableTiles();
//        		if (attackCompo != null) attackCompo.clearAttackableTiles();
            		
            	//Build the movable tiles list
        		tileSearchService.buildMoveTilesSet(allyEntity, room);
        		if (attackCompo != null && attackCompo.allAttackableTiles.isEmpty()) {
        			attackTileSearchService.buildAttackTilesSet(allyEntity, room, true, false);
        		}
        		moveCompo.hideMovableTiles();
        		if (attackCompo != null) attackCompo.hideAttackableTiles();
        		room.setNextState(RoomState.ALLY_MOVE_TILES_DISPLAYED);
        		
        		break;
        		
        	case ALLY_MOVE_TILES_DISPLAYED :
        		
            	Entity selectedTile = EnemyActionSelector.selectTileToMove(allyEntity, room, attackTileSearchService);
            		
            	if (selectedTile != null) {
            		GridPositionComponent destinationPos = Mappers.gridPositionComponent.get(selectedTile);
    		    	//Clicked on this tile !!
    				//Create an entity to show that this tile is selected as the destination
    				Entity destinationTileEntity = room.entityFactory.createDestinationTile(destinationPos.coord(), room);
    				moveCompo.setSelectedTile(destinationTileEntity);
    					
    				//Display the way to go to this point
    				List<Entity> waypoints = tileSearchService.buildWaypointList(allyEntity,moveCompo, enemyCurrentPos, 
    						destinationPos, room);
    		       	moveCompo.setWayPoints(waypoints);
    		       	moveCompo.hideMovementEntities();
            		room.setNextState(RoomState.ALLY_MOVE_DESTINATION_SELECTED);
            	} else {
            		room.setNextState(RoomState.ALLY_END_MOVEMENT);
            	}
        		
        		break;
        		
        	case ALLY_MOVE_DESTINATION_SELECTED :

        		movementHandler.initiateMovement(allyEntity);
            	room.setNextState(RoomState.ALLY_MOVING);

        		break;
        		
        	case ALLY_MOVING:
        		if (moveCompo.moving) {
	    	    	moveCompo.selectCurrentMoveDestinationTile(allyEntity);
	    	    		
	    	    	//Do the movement on screen
	    	    	MovementProgressEnum movementProgress = movementHandler.performRealMovement(allyEntity, room);
	        		if (movementProgress == MovementProgressEnum.MOVEMENT_OVER) room.setNextState(RoomState.ALLY_END_MOVEMENT);
        		} else {
        			room.setNextState(RoomState.ALLY_END_MOVEMENT);
        		}
        		
        		break;
        		
        	case ALLY_END_MOVEMENT:
        		if (moveCompo.moving) {
	        		MovementHandler.finishRealMovement(allyEntity, room);
	    	    	moveCompo.clearMovableTiles();
	
	        		if (attackCompo != null) attackCompo.clearAttackableTiles();
	        		if (attackCompo != null) attackTileSearchService.buildAttackTilesSet(allyEntity, room, true, false);
        		}
        		moveCompo.clearMovableTiles();

    	    	room.setNextState(RoomState.ALLY_ATTACK);

        		break;
        		
        	case ALLY_ATTACK:
        		
        		//Check if attack possible
        		boolean attacked = false;
    	    	if (attackCompo.isActive() && attackCompo.attackableTiles != null && !attackCompo.attackableTiles.isEmpty()) {
    	    		for (Entity attTile : attackCompo.attackableTiles) {
    	    			GridPositionComponent attTilePos = Mappers.gridPositionComponent.get(attTile);
    	    			int range = TileUtil.getDistanceBetweenTiles(enemyCurrentPos.coord(), attTilePos.coord());
						if (range <= attackCompo.getRangeMax() && range >= attackCompo.getRangeMin()) {
    	    				//Attack possible
							Entity target = TileUtil.getAttackableEntityOnTile(allyEntity, attTilePos.coord(), room);
							if (target != null) {								
	            				attackCompo.setTarget(target);
	            				attackCompo.setTargetedTile(room.getTileAtGridPosition(attTilePos.coord()));
	            				attacked = true;
	            				room.setNextState(RoomState.ALLY_ATTACK_ANIMATION);
	            				
	            				// Orient the sprite so that it looks towards its target
	            				Mappers.spriteComponent.get(allyEntity).orientSprite(allyEntity, attTilePos.coord());
	            				
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
    	    		room.setNextState(RoomState.ALLY_ATTACK_FINISH);
    	    	}
    	    	
        		break;
        		
        	case ALLY_ATTACK_ANIMATION:
        		
				Action finishAttackAction = new Action(){
				  @Override
				  public boolean act(float delta){
					room.attackManager.performAttack(allyEntity, attackCompo);
    	    		room.setNextState(RoomState.ALLY_ATTACK_FINISH);
				    return true;
				  }
				};
        		
				
				if (!attackCompo.getActiveSkill().getAttackAnimation().isPlaying()) {
					boolean hasAnim = attackCompo.getActiveSkill().setAttackImage(enemyCurrentPos.coord(), 
							attackCompo.getTargetedTile(), 
							null,
							fxStage,
							finishAttackAction);
					
					if (!hasAnim) {
						room.attackManager.performAttack(allyEntity, attackCompo);
	    	    		room.setNextState(RoomState.ALLY_ATTACK_FINISH);
					}
				}

        		break;
        		
        	case ALLY_ATTACK_FINISH:
	    		finishOneAllyTurn(allyEntity, attackCompo, aiCompo);
	    		break;
	    		
        	default:
        	}
    		
    		break;
    	}
    	
		//If all allies have finished moving, end the turn
		checkAllAlliesFinished();
    	
    }

	private void checkAllAlliesFinished() {
		if (allAlliesOfCurrentRoom.size() == 0 || allyFinishedCount == allAlliesOfCurrentRoom.size()) {
			for (Entity e : allAlliesOfCurrentRoom) {
				Mappers.aiComponent.get(e).setTurnOver(false);
			}
			allyFinishedCount = 0;
			allyCurrentyPlaying = null;
			room.turnManager.endAllyTurn();
		}
	}

	public void finishOneAllyTurn(Entity allyEntity, AttackComponent attackCompo, AIComponent aiCompo) {
    	attackCompo.clearAttackableTiles();
		attackCompo.getActiveSkill().clearAttackImage();
		attackCompo.setActiveSkill(null);

		aiCompo.onEndTurn(allyEntity, room);

		allyFinishedCount ++;
		Mappers.aiComponent.get(allyEntity).setTurnOver(true);
		room.setNextState(RoomState.ALLY_TURN_INIT);
	}

	private void fillEntitiesOfCurrentRoom() {
		allAlliesOfCurrentRoom.clear();
		for (Entity e : room.getAllies()) {
			if (e != GameScreen.player) {
				allAlliesOfCurrentRoom.add(e);
			}
		}
	}

    
    /**
     * For each enemy, compute the list of tiles where they can move and attack.
     */
    private void computeMovableTilesToDisplayToPlayer() {
    	for (Entity allyEntity : allAlliesOfCurrentRoom) {
//    		EnemyComponent enemyComponent = Mappers.enemyComponent.get(allyEntity);
//    		if (enemyComponent.getSubSystem() != null) {
//    			boolean handledInSubSystem = enemyComponent.getSubSystem().computeMovableTilesToDisplayToPlayer(this, allyEntity, room);
//    			if (handledInSubSystem) continue;
//    		}
    		
        	MoveComponent moveCompo = Mappers.moveComponent.get(allyEntity);
        	AttackComponent attackCompo = Mappers.attackComponent.get(allyEntity);
        	
    		//clear the movable tile
    		moveCompo.clearMovableTiles();
    		if (attackCompo != null) attackCompo.clearAttackableTiles();
    		
    		moveCompo.setMoveRemaining(moveCompo.getMoveSpeed());
        		
        	//Build the movable tiles list
    		tileSearchService.buildMoveTilesSet(allyEntity, room);
    		if (attackCompo != null) attackTileSearchService.buildAttackTilesSet(allyEntity, room, false, true);
    		moveCompo.hideMovableTiles();
    		if (attackCompo != null) attackCompo.hideAttackableTiles();
    	}
    	
    	room.setNextState(RoomState.ENEMY_COMPUTE_TILES_TO_DISPLAY_TO_PLAYER);
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
