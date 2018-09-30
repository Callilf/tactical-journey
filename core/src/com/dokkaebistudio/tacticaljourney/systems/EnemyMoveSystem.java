package com.dokkaebistudio.tacticaljourney.systems;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.dokkaebistudio.tacticaljourney.ai.movements.MovableTileSearchUtil;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent;
import com.dokkaebistudio.tacticaljourney.components.TransformComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;

public class EnemyMoveSystem extends IteratingSystem {

	private final ComponentMapper<TileComponent> tileCM;
	private final ComponentMapper<EnemyComponent> enemyCM;
	private final ComponentMapper<MoveComponent> moveCM;
    private final ComponentMapper<GridPositionComponent> gridPositionM;
    private final ComponentMapper<SpriteComponent> textureCompoM;
    private final ComponentMapper<TransformComponent> transfoCompoM;

    /** The room. */
    private Room room;
    
    /** For each enemy, store whether it's turn is over or not. */
    private Map<Entity, Boolean> turnFinished = new HashMap<>();

    public EnemyMoveSystem(Room r) {
        super(Family.all(EnemyComponent.class, MoveComponent.class, GridPositionComponent.class).get());
        this.tileCM = ComponentMapper.getFor(TileComponent.class);
        this.gridPositionM = ComponentMapper.getFor(GridPositionComponent.class);
        this.enemyCM = ComponentMapper.getFor(EnemyComponent.class);
        this.moveCM = ComponentMapper.getFor(MoveComponent.class);
        this.textureCompoM = ComponentMapper.getFor(SpriteComponent.class);
        this.transfoCompoM = ComponentMapper.getFor(TransformComponent.class);
        room = r;
    }

    @Override
    public void update(float deltaTime) {
    	super.update(deltaTime);
    	
    	if (!room.state.isEnemyTurn()) {
    		return;
    	}
    	
    	
    	ImmutableArray<Entity> allEnemies = getEntities();
    	
    	int enemyFinishedCount = 0;
    	for (Entity enemyEntity : allEnemies) {
    		if (turnFinished.get(enemyEntity) != null && turnFinished.get(enemyEntity).booleanValue() == true) {
    			enemyFinishedCount ++;
    			continue;
    		}
    		
        	MoveComponent moveCompo = moveCM.get(enemyEntity);
        	GridPositionComponent moverCurrentPos = gridPositionM.get(enemyEntity);
    		
    		switch(room.state) {
        	case ENEMY_TURN_INIT :
            	
            	moveCompo.moveRemaining = moveCompo.moveSpeed;
            	room.state = RoomState.ENEMY_COMPUTE_MOVABLE_TILES;
        		
        	case ENEMY_COMPUTE_MOVABLE_TILES :
        		
        		//clear the movable tile
        		moveCompo.clearMovableTiles();
            		
            	//Build the movable tiles list
        		MovableTileSearchUtil.buildMoveTilesSet(enemyEntity, moveCompo, room, gridPositionM, tileCM);
        		moveCompo.hideMovableTiles();
        		room.state = RoomState.ENEMY_MOVE_TILES_DISPLAYED;
        		
        		break;
        		
        	case ENEMY_MOVE_TILES_DISPLAYED :
        		
            	Entity selectedTile = null;
            	for (Entity t : moveCompo.movableTiles) {
            		selectedTile = t;
            		break;
            	}
            		
            	if (selectedTile != null) {
            		GridPositionComponent destinationPos = gridPositionM.get(selectedTile);
    		    	//Clicked on this tile !!
    				//Create an entity to show that this tile is selected as the destination
    				Entity destinationTileEntity = room.entityFactory.createDestinationTile(destinationPos.coord);
    				moveCompo.setSelectedTile(destinationTileEntity);
    					
    				//Display the confirmation button
    				Entity moveConfirmationButton = room.entityFactory.createMoveConfirmationButton(moverCurrentPos.coord);
    				moveCompo.setMovementConfirmationButton(moveConfirmationButton);
    					
    				//Display the way to go to this point
    				List<Entity> waypoints = MovableTileSearchUtil.buildWaypointList(moveCompo, moverCurrentPos, destinationPos, room, gridPositionM);
    		       	moveCompo.setWayPoints(waypoints);
    		       	moveCompo.hideMovementEntities();
            	}
        		room.state = RoomState.ENEMY_MOVE_DESTINATION_SELECTED;
        		
        		break;
        		
        	case ENEMY_MOVE_DESTINATION_SELECTED :

            	moveCompo.initiateMovement(enemyEntity, moverCurrentPos);
            	room.state = RoomState.ENEMY_MOVING;

        		break;
        		
        	case ENEMY_MOVING:
        		
    	    	TransformComponent transfoCompo = transfoCompoM.get(enemyEntity);
    	    	moveCompo.selectCurrentMoveDestinationTile(gridPositionM);
    	    		
    	    	//Do the movement on screen
    	    	boolean movementFinished = MovableTileSearchUtil.performRealMovement(moveCompo, transfoCompo, room);
        		if (movementFinished) room.state = RoomState.ENEMY_END_MOVEMENT;
        		
        		break;
        		
        	case ENEMY_END_MOVEMENT:
        		
            	//Remove the transform component
            	enemyEntity.remove(TransformComponent.class);
            		
            	//Set the new position in the GridPositionComponent
    	    	GridPositionComponent selectedTilePos = gridPositionM.get(moveCompo.getSelectedTile());
    	    	moverCurrentPos.coord.set(selectedTilePos.coord);
    	    		
    	    	moveCompo.clearMovableTiles();
    	    	
    	    	enemyFinishedCount ++;
    	    	turnFinished.put(enemyEntity, new Boolean(true));
    	    	room.state = RoomState.ENEMY_TURN_INIT;

        		break;
        		
        	default:
        	}
    		
    		break;
    	}
    	
    	
		//If all enemies have finished moving, end the turn
		if (allEnemies.size() == 0 || enemyFinishedCount == allEnemies.size()) {
			enemyFinishedCount = 0;
			turnFinished.clear();
			room.turnManager.endEnemyTurn();
		}
    	
//    	switch(room.state) {
//    	case ENEMY_TURN_INIT :
//        	
//        	for (Entity enemyEntity : allEnemies) {
//        		MoveComponent moveCompo = moveCM.get(enemyEntity);
//        		moveCompo.moveRemaining = moveCompo.moveSpeed;
//        	}
//        	room.state = RoomState.ENEMY_MOVE_START;
//    		
//    	case ENEMY_MOVE_START :
//    		
//    		for (Entity enemyEntity : allEnemies) {
//        		MoveComponent moveCompo = moveCM.get(enemyEntity);
//    			//clear the movable tile
//    			moveCompo.clearMovableTiles();
//        		
//        		//Build the movable tiles list
//    			MovableTileSearchUtil.buildMoveTilesSet(enemyEntity, moveCompo, room, gridPositionM, tileCM);
//    			moveCompo.hideMovableTiles();
//        	}
//    		room.state = RoomState.ENEMY_MOVE_TILES_DISPLAYED;
//    		
//    		break;
//    		
//    	case ENEMY_MOVE_TILES_DISPLAYED :
//    		
//    		for (Entity enemyEntity : allEnemies) {
//        		MoveComponent moveCompo = moveCM.get(enemyEntity);
//        		GridPositionComponent moverCurrentPos = gridPositionM.get(enemyEntity);
//        		Entity selectedTile = null;
//        		for (Entity t : moveCompo.movableTiles) {
//        			selectedTile = t;
//        			break;
//        		}
//        		
//        		if (selectedTile != null) {
//        			GridPositionComponent destinationPos = gridPositionM.get(selectedTile);
//		    		//Clicked on this tile !!
//					//Create an entity to show that this tile is selected as the destination
//					Entity destinationTileEntity = room.entityFactory.createDestinationTile(destinationPos.coord);
//					moveCompo.setSelectedTile(destinationTileEntity);
//					
//					//Display the confirmation button
//					Entity moveConfirmationButton = room.entityFactory.createMoveConfirmationButton(moverCurrentPos.coord);
//					moveCompo.setMovementConfirmationButton(moveConfirmationButton);
//					
//					//Display the way to go to this point
//					List<Entity> waypoints = MovableTileSearchUtil.buildWaypointList(moveCompo, moverCurrentPos, destinationPos, room, gridPositionM);
//		        	moveCompo.setWayPoints(waypoints);
//		        	moveCompo.hideMovementEntities();
//        		}
//    		}
//    		room.state = RoomState.ENEMY_MOVE_DESTINATION_SELECTED;
//    		
//    		break;
//    		
//    	case ENEMY_MOVE_DESTINATION_SELECTED :
//    		
//    		for (Entity enemyEntity : allEnemies) {
//        		MoveComponent moveCompo = moveCM.get(enemyEntity);
//        		GridPositionComponent moverCurrentPos = gridPositionM.get(enemyEntity);
//        		
//        		moveCompo.initiateMovement(enemyEntity, moverCurrentPos);
//	
//        		room.state = RoomState.ENEMY_MOVING;
//
//    		}    		
//    		
//    		break;
//    		
//    	case ENEMY_MOVING:
//    		
//    		boolean allEnemiesMovementsOver = true;
//    		for (Entity enemyEntity : allEnemies) {
//        		MoveComponent moveCompo = moveCM.get(enemyEntity);
//	    		TransformComponent transfoCompo = transfoCompoM.get(enemyEntity);
//	    		moveCompo.selectCurrentMoveDestinationTile(gridPositionM);
//	    		
//	    		//Do the movement on screen
//	    		allEnemiesMovementsOver &= MovableTileSearchUtil.performRealMovement(moveCompo, transfoCompo, room);
//    		}
//    		
//    		if (allEnemiesMovementsOver) {
//    			room.state = RoomState.ENEMY_END_MOVEMENT;
//    		}
//    		
//    		break;
//    		
//    	case ENEMY_END_MOVEMENT:
//    		
//    		for (Entity enemyEntity : allEnemies) {
//        		MoveComponent moveCompo = moveCM.get(enemyEntity);
//        		GridPositionComponent moverCurrentPos = gridPositionM.get(enemyEntity);
//        		
//        		//Remove the transform component
//        		enemyEntity.remove(TransformComponent.class);
//        		
//        		//Set the new position in the GridPositionComponent
//	    		GridPositionComponent selectedTilePos = gridPositionM.get(moveCompo.getSelectedTile());
//	    		moverCurrentPos.coord.set(selectedTilePos.coord);
//	    		
//	    		moveCompo.clearMovableTiles();
//    		}
//    		room.turnManager.endEnemyTurn();
//
//    		break;
//    		
//    	default:
//    	}
    }
    
    @Override
    protected void processEntity(Entity moverEntity, float deltaTime) {}
}
