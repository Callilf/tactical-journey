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
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTileSearchService;
import com.dokkaebistudio.tacticaljourney.ai.movements.TileSearchService;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.ParentRoomComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
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
    
	/** The tile search service. */
	private TileSearchService tileSearchService;
	/** The attack tile search service. */
	private AttackTileSearchService attackTileSearchService;


    public EnemyMoveSystem(Room r) {
        super(Family.all(EnemyComponent.class, MoveComponent.class, GridPositionComponent.class).get());
        room = r;
        movementHandler = new MovementHandler(r.engine);
        allEnemiesOfCurrentRoom = new ArrayList<>();
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
    		
    		switch(room.getState()) {
        	case ENEMY_TURN_INIT :
            	
            	moveCompo.moveRemaining = moveCompo.moveSpeed;
            	room.setNextState(RoomState.ENEMY_COMPUTE_MOVABLE_TILES);
        		
        	case ENEMY_COMPUTE_MOVABLE_TILES :
        		
        		//clear the movable tile
        		moveCompo.clearMovableTiles();
        		if (attackCompo != null) attackCompo.clearAttackableTiles();
            		
            	//Build the movable tiles list
        		tileSearchService.buildMoveTilesSet(enemyEntity, room);
        		if (attackCompo != null) attackTileSearchService.buildAttackTilesSet(enemyEntity, room, true);
        		moveCompo.hideMovableTiles();
        		attackCompo.hideAttackableTiles();
        		room.setNextState(RoomState.ENEMY_MOVE_TILES_DISPLAYED);
        		
        		break;
        		
        	case ENEMY_MOVE_TILES_DISPLAYED :
        		
            	Entity selectedTile = EnemyActionSelector.selectTileToMove(enemyEntity, room.engine);
            		
            	if (selectedTile != null) {
            		GridPositionComponent destinationPos = Mappers.gridPositionComponent.get(selectedTile);
    		    	//Clicked on this tile !!
    				//Create an entity to show that this tile is selected as the destination
    				Entity destinationTileEntity = room.entityFactory.createDestinationTile(destinationPos.coord(), room);
    				moveCompo.setSelectedTile(destinationTileEntity);
    					
    				//Display the way to go to this point
    				List<Entity> waypoints = tileSearchService.buildWaypointList(moveCompo, moverCurrentPos, destinationPos, room);
    		       	moveCompo.setWayPoints(waypoints);
    		       	moveCompo.hideMovementEntities();
            		room.setNextState(RoomState.ENEMY_MOVE_DESTINATION_SELECTED);
            	} else {
            		room.setNextState(RoomState.ENEMY_ATTACK);
            	}
        		
        		break;
        		
        	case ENEMY_MOVE_DESTINATION_SELECTED :

        		movementHandler.initiateMovement(enemyEntity);
            	room.setNextState(RoomState.ENEMY_MOVING);

        		break;
        		
        	case ENEMY_MOVING:
        		
    	    	moveCompo.selectCurrentMoveDestinationTile();
    	    		
    	    	//Do the movement on screen
    	    	boolean movementFinished = movementHandler.performRealMovement(enemyEntity, room);
        		if (movementFinished) room.setNextState(RoomState.ENEMY_END_MOVEMENT);
        		
        		break;
        		
        	case ENEMY_END_MOVEMENT:
        		
        		movementHandler.finishRealMovement(enemyEntity, room);
    	    	moveCompo.clearMovableTiles();
    	    	room.setNextState(RoomState.ENEMY_ATTACK);

        		break;
        		
        	case ENEMY_ATTACK:
        		
        		//Check if attack possible
    	    	if (attackCompo.attackableTiles != null && !attackCompo.attackableTiles.isEmpty()) {
    	    		for (Entity attTile : attackCompo.attackableTiles) {
    	    			GridPositionComponent attTilePos = Mappers.gridPositionComponent.get(attTile);
    	    			int range = TileUtil.getDistanceBetweenTiles(moverCurrentPos.coord(), attTilePos.coord());
						if (range <= attackCompo.getRangeMax() && range >= attackCompo.getRangeMin()) {
    	    				//Attack possible
							Entity target = TileUtil.getAttackableEntityOnTile(attTilePos.coord(), room);
            				attackCompo.setTarget(target);
							room.attackManager.performAttack(enemyEntity, attackCompo);
    	    			}
    	    		}
    	    	}
    	    	attackCompo.clearAttackableTiles();
    	    	
    	    	enemyFinishedCount ++;
    	    	turnFinished.put(enemyEntity, new Boolean(true));
    	    	room.setNextState(RoomState.ENEMY_TURN_INIT);
    	    	
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

	private void fillEntitiesOfCurrentRoom() {
		allEnemiesOfCurrentRoom.clear();
    	ImmutableArray<Entity> allEnemies = getEntities();
    	for (Entity enemyEntity : allEnemies) {
			ParentRoomComponent parentRoomComponent = Mappers.parentRoomComponent.get(enemyEntity);
			if (parentRoomComponent != null && parentRoomComponent.getParentRoom() == this.room) {
				allEnemiesOfCurrentRoom.add(enemyEntity);
			}
    	}
	}
    
    @Override
    protected void processEntity(Entity moverEntity, float deltaTime) {}
    
    
    /**
     * For each enemy, compute the list of tiles where they can move and attack.
     */
    private void computeMovableTilesToDisplayToPlayer() {
    	for (Entity enemyEntity : allEnemiesOfCurrentRoom) {
        	MoveComponent moveCompo = Mappers.moveComponent.get(enemyEntity);
        	AttackComponent attackCompo = Mappers.attackComponent.get(enemyEntity);
        	
    		//clear the movable tile
    		moveCompo.clearMovableTiles();
    		if (attackCompo != null) attackCompo.clearAttackableTiles();
    		
    		moveCompo.moveRemaining = moveCompo.moveSpeed;
        		
        	//Build the movable tiles list
    		tileSearchService.buildMoveTilesSet(enemyEntity, room);
    		if (attackCompo != null) attackTileSearchService.buildAttackTilesSet(enemyEntity, room, false);
    		moveCompo.hideMovableTiles();
    		attackCompo.hideAttackableTiles();
    	}
    	
    	room.setNextState(RoomState.PLAYER_MOVE_TILES_DISPLAYED);
    }
}
