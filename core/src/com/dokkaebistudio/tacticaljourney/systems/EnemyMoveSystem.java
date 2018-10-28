package com.dokkaebistudio.tacticaljourney.systems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.dokkaebistudio.tacticaljourney.ai.enemies.EnemyActionSelector;
import com.dokkaebistudio.tacticaljourney.ai.movements.TileSearchUtil;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.ParentRoomComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TransformComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class EnemyMoveSystem extends IteratingSystem implements RoomSystem {
	
    /** The movement handler. */
    private final MovementHandler movementHandler;

    /** The room. */
    private Room room;
   
    /** The enemies of the current room that need updating. */
    private List<Entity> allEnemiesOfCurrentRoom;
    
    /** For each enemy, store whether it's turn is over or not. */
    private Map<Entity, Boolean> turnFinished = new HashMap<>();

    public EnemyMoveSystem(Room r) {
        super(Family.all(EnemyComponent.class, MoveComponent.class, GridPositionComponent.class).get());
        room = r;
        movementHandler = new MovementHandler(r.engine);
        allEnemiesOfCurrentRoom = new ArrayList<>();
    }

    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }
    
    @Override
    public void update(float deltaTime) {
    	super.update(deltaTime);
    	
    	if (!room.state.isEnemyTurn()) {
    		return;
    	}
    	
    	//Get all enemies of the current room
    	allEnemiesOfCurrentRoom.clear();
    	ImmutableArray<Entity> allEnemies = getEntities();
    	for (Entity enemyEntity : allEnemies) {
			ParentRoomComponent parentRoomComponent = Mappers.parentRoomComponent.get(enemyEntity);
			if (parentRoomComponent != null && parentRoomComponent.getParentRoom() == this.room) {
				allEnemiesOfCurrentRoom.add(enemyEntity);
			}
    	}
    	
    	
    	int enemyFinishedCount = 0;
    	for (Entity enemyEntity : allEnemiesOfCurrentRoom) {
    		HealthComponent healthComponent = Mappers.healthComponent.get(enemyEntity);
    		if (healthComponent == null || healthComponent.isDead()) {
    			continue;
    		}
    		
    		if (turnFinished.get(enemyEntity) != null && turnFinished.get(enemyEntity).booleanValue() == true) {
    			enemyFinishedCount ++;
    			continue;
    		}
    		
        	MoveComponent moveCompo = Mappers.moveComponent.get(enemyEntity);
        	AttackComponent attackCompo = Mappers.attackComponent.get(enemyEntity);
        	GridPositionComponent moverCurrentPos = Mappers.gridPositionComponent.get(enemyEntity);
    		
    		switch(room.state) {
        	case ENEMY_TURN_INIT :
            	
            	moveCompo.moveRemaining = moveCompo.moveSpeed;
            	room.state = RoomState.ENEMY_COMPUTE_MOVABLE_TILES;
        		
        	case ENEMY_COMPUTE_MOVABLE_TILES :
        		
        		//clear the movable tile
        		moveCompo.clearMovableTiles();
        		if (attackCompo != null) attackCompo.clearAttackableTiles();
            		
            	//Build the movable tiles list
        		TileSearchUtil.buildMoveTilesSet(enemyEntity, moveCompo, room);
        		if (attackCompo != null) TileSearchUtil.buildAttackTilesSet(enemyEntity, moveCompo, attackCompo, room);
        		moveCompo.hideMovableTiles();
        		attackCompo.hideAttackableTiles();
        		room.state = RoomState.ENEMY_MOVE_TILES_DISPLAYED;
        		
        		break;
        		
        	case ENEMY_MOVE_TILES_DISPLAYED :
        		
            	Entity selectedTile = EnemyActionSelector.selectTileToMove(enemyEntity, room.engine);
            		
            	if (selectedTile != null) {
            		GridPositionComponent destinationPos = Mappers.gridPositionComponent.get(selectedTile);
    		    	//Clicked on this tile !!
    				//Create an entity to show that this tile is selected as the destination
    				Entity destinationTileEntity = room.entityFactory.createDestinationTile(destinationPos.coord);
    				moveCompo.setSelectedTile(destinationTileEntity);
    					
    				//Display the way to go to this point
    				List<Entity> waypoints = TileSearchUtil.buildWaypointList(moveCompo, moverCurrentPos, destinationPos, room);
    		       	moveCompo.setWayPoints(waypoints);
    		       	moveCompo.hideMovementEntities();
            		room.state = RoomState.ENEMY_MOVE_DESTINATION_SELECTED;
            	} else {
            		room.state = RoomState.ENEMY_ATTACK;
            	}
        		
        		break;
        		
        	case ENEMY_MOVE_DESTINATION_SELECTED :

        		movementHandler.initiateMovement(enemyEntity);
            	room.state = RoomState.ENEMY_MOVING;

        		break;
        		
        	case ENEMY_MOVING:
        		
    	    	TransformComponent transfoCompo = Mappers.transfoComponent.get(enemyEntity);
    	    	moveCompo.selectCurrentMoveDestinationTile();
    	    		
    	    	//Do the movement on screen
    	    	boolean movementFinished = movementHandler.performRealMovement(enemyEntity, room);
        		if (movementFinished) room.state = RoomState.ENEMY_END_MOVEMENT;
        		
        		break;
        		
        	case ENEMY_END_MOVEMENT:
        		
        		movementHandler.finishRealMovement(enemyEntity);
    	    	moveCompo.clearMovableTiles();
    	    	room.state = RoomState.ENEMY_ATTACK;

        		break;
        		
        	case ENEMY_ATTACK:
        		
        		//Check if attack possible
    	    	if (attackCompo.attackableTiles != null && !attackCompo.attackableTiles.isEmpty()) {
    	    		for (Entity attTile : attackCompo.attackableTiles) {
    	    			GridPositionComponent attTilePos = Mappers.gridPositionComponent.get(attTile);
    	    			int range = TileUtil.getDistanceBetweenTiles(moverCurrentPos.coord, attTilePos.coord);
						if (range <= attackCompo.getRangeMax() && range >= attackCompo.getRangeMin()) {
    	    				//Attack possible
							Entity target = TileUtil.getAttackableEntityOnTile(attTilePos.coord, room);
            				attackCompo.setTarget(target);
							room.attackManager.performAttack(enemyEntity, attackCompo.getTarget());
    	    			}
    	    		}
    	    	}
    	    	attackCompo.clearAttackableTiles();
    	    	
    	    	enemyFinishedCount ++;
    	    	turnFinished.put(enemyEntity, new Boolean(true));
    	    	room.state = RoomState.ENEMY_TURN_INIT;
    	    	
        		break;
        		
        	default:
        	}
    		
    		break;
    	}
    	
    	
		//If all enemies have finished moving, end the turn
		if (allEnemiesOfCurrentRoom.size() == 0 || enemyFinishedCount == allEnemiesOfCurrentRoom.size()) {
			enemyFinishedCount = 0;
			turnFinished.clear();
			room.turnManager.endEnemyTurn();
		}
    	
    }
    
    @Override
    protected void processEntity(Entity moverEntity, float deltaTime) {}
}
