package com.dokkaebistudio.tacticaljourney.systems.creatures;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.dokkaebistudio.tacticaljourney.ai.enemies.EnemyActionSelector;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTileSearchService;
import com.dokkaebistudio.tacticaljourney.ai.movements.TileSearchService;
import com.dokkaebistudio.tacticaljourney.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomCreatureState;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler.MovementProgressEnum;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public abstract class CreatureSystem extends EntitySystem implements RoomSystem {
	
    /** The movement handler. */
	protected final MovementHandler movementHandler;

    /** The room. */
    protected Room room;
    
    /** The fx stage for attack animations. */
    protected Stage fxStage;
   
    /** The creatures of the current room that need updating. */
    protected List<Entity> allCreaturesOfCurrentRoom;
        
	/** The tile search service. */
    protected TileSearchService tileSearchService;
	/** The attack tile search service. */
    protected AttackTileSearchService attackTileSearchService;
	
	public static Entity creatureCurrentyPlaying;
	
	
	protected int creatureFinishedCount = 0;


    public CreatureSystem(Room r, Stage stage) {
		this.priority = 9;
		
		CreatureSystem.creatureCurrentyPlaying = null;

		this.room = r;
        this.fxStage = stage;
        this.movementHandler = new MovementHandler(r.engine);
        this.allCreaturesOfCurrentRoom = new ArrayList<>();
		this.tileSearchService = new TileSearchService();
		this.attackTileSearchService = new AttackTileSearchService();
    }

    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }
    
    
    public abstract boolean isStateRelevant();
    
    public abstract boolean computeTilesToDisplayState();
    public abstract void finishComputeTilesToDisplay();
    
    @Override
    public void update(float deltaTime) {
    	super.update(deltaTime);
    	
    	if (!isStateRelevant()) {
    		return;
    	}
    	
    	//Get all enemies of the current room
    	fillEntitiesOfCurrentRoom();
    	
    	if (computeTilesToDisplayState() ) {
    		//Computing movable tiles of all enemies to display them to the player
    		computeMovableTilesToDisplayToPlayer();
    		finishComputeTilesToDisplay();
    		return;
    	}
    	
    	
    	creatureFinishedCount = 0;
    	for (final Entity creatureEntity : allCreaturesOfCurrentRoom) {
    		HealthComponent healthComponent = Mappers.healthComponent.get(creatureEntity);
    		if (healthComponent != null && healthComponent.isDead()) {
    			continue;
    		}
    		
    		final AIComponent aiComponent = Mappers.aiComponent.get(creatureEntity);
    		if (aiComponent.isTurnOver()) {
    			creatureFinishedCount ++;
    			continue;
    		}
    		
    		creatureCurrentyPlaying = creatureEntity;
    		
        	if ((room.getState() == RoomState.ENEMY_TURN || room.getState() == RoomState.ALLY_TURN) 
        			&& room.getCreatureState() == RoomCreatureState.NONE) {
            	room.setCreatureState(RoomCreatureState.TURN_INIT);
        	}
        	
    		
    		// Check if this enemy uses a sub system
    		if (aiComponent.getSubSystem() != null) {
    			boolean creatureHandled = aiComponent.getSubSystem().update(this, creatureEntity, room);
    			if (creatureHandled) {
    				checkAllCreaturesFinished();
    				return;
    			}
    		}
    		
    		
        	MoveComponent moveCompo = Mappers.moveComponent.get(creatureEntity);
        	final AttackComponent attackCompo = Mappers.attackComponent.get(creatureEntity);
        	GridPositionComponent enemyCurrentPos = Mappers.gridPositionComponent.get(creatureEntity);
        	
    		switch(room.getCreatureState()) {
        		
    		case TURN_INIT:
            	moveCompo.setMoveRemaining(moveCompo.getMoveSpeed());
            	aiComponent.onStartTurn(creatureEntity, room);
            	room.setCreatureState(RoomCreatureState.COMPUTE_MOVABLE_TILES);

        	case COMPUTE_MOVABLE_TILES :
        		
        		//clear the movable tile
        		moveCompo.clearMovableTiles();
//        		if (attackCompo != null) attackCompo.clearAttackableTiles();
            		
            	//Build the movable tiles list
        		tileSearchService.buildMoveTilesSet(creatureEntity, room);
        		if (attackCompo != null && attackCompo.allAttackableTiles != null && attackCompo.allAttackableTiles.isEmpty()) {
        			attackTileSearchService.buildAttackTilesSet(creatureEntity, room, true, false);
        		}
        		moveCompo.hideMovableTiles();
        		if (attackCompo != null) attackCompo.hideAttackableTiles();
        		room.setCreatureState(RoomCreatureState.MOVE_TILES_DISPLAYED);
        		
        		break;
        		
        	case MOVE_TILES_DISPLAYED :
        		
            	Entity selectedTile = EnemyActionSelector.selectTileToMove(creatureEntity, room, attackTileSearchService);
            		
            	if (selectedTile != null) {
            		GridPositionComponent destinationPos = Mappers.gridPositionComponent.get(selectedTile);
    		    	//Clicked on this tile !!
    				//Create an entity to show that this tile is selected as the destination
    				Entity destinationTileEntity = room.entityFactory.createDestinationTile(destinationPos.coord(), room);
    				moveCompo.setSelectedTile(destinationTileEntity);
    					
    				//Display the way to go to this point
    				List<Entity> waypoints = tileSearchService.buildWaypointList(creatureEntity,moveCompo, enemyCurrentPos, 
    						destinationPos, room);
    		       	moveCompo.setWayPoints(waypoints);
    		       	moveCompo.hideMovementEntities();
            		room.setCreatureState(RoomCreatureState.MOVE_DESTINATION_SELECTED);
            	} else {
            		room.setCreatureState(RoomCreatureState.END_MOVEMENT);
            	}
        		
        		break;
        		
        	case MOVE_DESTINATION_SELECTED :

        		movementHandler.initiateMovement(creatureEntity);
            	room.setCreatureState(RoomCreatureState.MOVING);

        		break;
        		
        	case MOVING:
        		if (moveCompo.moving) {
	    	    	moveCompo.selectCurrentMoveDestinationTile(creatureEntity);
	    	    		
	    	    	//Do the movement on screen
	    	    	MovementProgressEnum movementProgress = movementHandler.performRealMovement(creatureEntity, room);
	        		if (movementProgress == MovementProgressEnum.MOVEMENT_OVER) room.setCreatureState(RoomCreatureState.END_MOVEMENT);
        		} else {
        			room.setCreatureState(RoomCreatureState.END_MOVEMENT);
        		}
        		
        		break;
        		
        	case END_MOVEMENT:
        		if (moveCompo.moving) {
	        		MovementHandler.finishRealMovement(creatureEntity, room);
	    	    	moveCompo.clearMovableTiles();
        		}
        		if (attackCompo != null) {
        			attackCompo.clearAttackableTiles();
        			attackTileSearchService.buildAttackTilesSet(creatureEntity, room, true, false);
        		}
        		moveCompo.clearMovableTiles();

    	    	room.setCreatureState(RoomCreatureState.ATTACK);

        		break;
        		
        	case ATTACK:
        		
        		//Check if attack possible
        		boolean attacked = false;
    	    	if (attackCompo.isActive() && attackCompo.attackableTiles != null && !attackCompo.attackableTiles.isEmpty()) {
    	    		for (Entity attTile : attackCompo.attackableTiles) {
    	    			GridPositionComponent attTilePos = Mappers.gridPositionComponent.get(attTile);
    	    			int range = TileUtil.getDistanceBetweenTiles(enemyCurrentPos.coord(), attTilePos.coord());
						if (range <= attackCompo.getRangeMax() && range >= attackCompo.getRangeMin()) {
    	    				//Attack possible
							Entity target = TileUtil.getAttackableEntityOnTile(creatureEntity, attTilePos.coord(), room);
							if (target != null) {
								
					    		EnemyComponent enemyComponent = Mappers.enemyComponent.get(creatureEntity);
								EnemyComponent targetEnemyCompo = Mappers.enemyComponent.get(target);
								if (enemyComponent != null && targetEnemyCompo != null && targetEnemyCompo.getFaction() == enemyComponent.getFaction()) {
									// Never attack member of the same faction
									continue;
								}
								
	            				attackCompo.setTarget(target);
	            				attackCompo.setTargetedTile(room.getTileAtGridPosition(attTilePos.coord()));
	            				attacked = true;
	            				room.setCreatureState(RoomCreatureState.ATTACK_ANIMATION);
	            				
	            				// Orient the sprite so that it looks towards its target
	            				Mappers.spriteComponent.get(creatureEntity).orientSprite(creatureEntity, attTilePos.coord());
	            				
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
    	    		room.setCreatureState(RoomCreatureState.ATTACK_FINISH);
    	    	}
    	    	
        		break;
        		
        	case ATTACK_ANIMATION:
        		
				Action finishAttackAction = new Action(){
				  @Override
				  public boolean act(float delta){
					room.attackManager.performAttack(creatureEntity, attackCompo);
    	    		room.setCreatureState(RoomCreatureState.ATTACK_FINISH);
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
						room.attackManager.performAttack(creatureEntity, attackCompo);
	    	    		room.setCreatureState(RoomCreatureState.ATTACK_FINISH);
					}
				}

        		break;
        		
        	case ATTACK_FINISH:
	    		finishOneCreatureTurn(creatureEntity, attackCompo, aiComponent);
	    		break;
	    		
        	default:
        	}
    		
    		break;
    	}
    	
		//If all enemies have finished moving, end the turn
		checkAllCreaturesFinished();
    	
    }
    
    public abstract void endTurn();

	private void checkAllCreaturesFinished() {
		if (allCreaturesOfCurrentRoom.size() == 0 || creatureFinishedCount == allCreaturesOfCurrentRoom.size()) {
			for (Entity e : allCreaturesOfCurrentRoom) {
				Mappers.aiComponent.get(e).setTurnOver(false);
			}
			creatureFinishedCount = 0;
			creatureCurrentyPlaying = null;
			endTurn();
		}
	}

	
	public void finishOneCreatureTurn(Entity enemyEntity, AttackComponent attackCompo, AIComponent aiComponent) {
    	attackCompo.clearAttackableTiles();
		attackCompo.getActiveSkill().clearAttackImage();
		attackCompo.setActiveSkill(null);
		
    	aiComponent.onEndTurn(enemyEntity, room);

    	
		creatureFinishedCount ++;
		Mappers.aiComponent.get(enemyEntity).setTurnOver(true);
		room.setCreatureState(RoomCreatureState.NONE);
	}

	public abstract void fillEntitiesOfCurrentRoom();

    
	
	
	
    /**
     * For each enemy, compute the list of tiles where they can move and attack.
     */
    private void computeMovableTilesToDisplayToPlayer() {
    	for (Entity enemyEntity : allCreaturesOfCurrentRoom) {
    		AIComponent aiComponent = Mappers.aiComponent.get(enemyEntity);
    		if (aiComponent.getSubSystem() != null) {
    			boolean handledInSubSystem = aiComponent.getSubSystem().computeMovableTilesToDisplayToPlayer(this, enemyEntity, room);
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
