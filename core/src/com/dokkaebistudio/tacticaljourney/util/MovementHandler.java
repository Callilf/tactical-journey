/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.util;

import java.util.Optional;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.ces.components.ChasmComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.interfaces.MovableInterface;
import com.dokkaebistudio.tacticaljourney.ces.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.transition.DoorComponent;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * Handles movements of entities.
 * @author Callil
 *
 */
public class MovementHandler {
	
	/** The speed at which entities move on screen. */
	public static final int MOVE_SPEED = 7;
	/** The speed at which entities move on screen. */
	public static final int MOVE_SPEED_IN_CLEARED_ROOMS = 14;
	
	/** The engine that managed entities.*/
	public PooledEngine engine;
	
	/**
	 * Constructor.
	 * @param engine the game engine
	 */
    public MovementHandler(PooledEngine engine) {
        this.engine = engine;
    }
    
    public enum MovementProgressEnum {
    	NONE,
    	IN_PROGRESS,
    	MOVEMENT_OVER,
    	TURN_OVER;
    }
	
	
    
    //*********************************************************************
    // Fluent movement BEGIN 
    
	public void initiateMovement(Entity e) {
		GridPositionComponent moverGridPosCompo = Mappers.gridPositionComponent.get(e);
		MoveComponent moveComponent = Mappers.moveComponent.get(e);
		
		//Add the tranfo component to the entity to perform real movement on screen
		Vector2 startPos = TileUtil.convertGridPosIntoPixelPos(moverGridPosCompo.coord());
		moverGridPosCompo.absolutePos((int)startPos.x, (int) startPos.y);
		
		moveComponent.currentMoveDestinationIndex = 0;
		moveComponent.moving = true;
		
		for (Component c : e.getComponents()) {
			if (c instanceof MovableInterface) {
				((MovableInterface) c).initiateMovement(moverGridPosCompo.coord());
			}
		}
		

		StateComponent stateComponent = Mappers.stateComponent.get(e);
		if (stateComponent != null && !stateComponent.isKeepCurrentAnimation()) {	
			if (Mappers.flyComponent.has(e)) {
				stateComponent.set(StatesEnum.FLY_MOVING);			
			} else {
				stateComponent.set(StatesEnum.MOVING);
			}
		}
		
	}
	
	
	
	/**
	 * Do the real movement from a tile to another.
	 * @param moveCompo the moveComponent
	 * @param transfoCompo the transformComponent
	 * @param room the Room
	 * @return true if the movement has ended, false if still in progress.
	 */
	public MovementProgressEnum performRealMovement(Entity mover, Room room) {
		return performRealMovement(mover, room, null);
	}
	
	/**
	 * Do the real movement from a tile to another.
	 * @param moveCompo the moveComponent
	 * @param transfoCompo the transformComponent
	 * @param room the Room
	 * @return true if the movement has ended, false if still in progress.
	 */
	public MovementProgressEnum performRealMovement(Entity mover, Room room, Integer moveSpeed) {
		MovementProgressEnum result = MovementProgressEnum.IN_PROGRESS;
		float xOffset = 0;
		float yOffset = 0;
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(mover);
		Vector2 absolutePos = gridPositionComponent.getAbsolutePos();
		
		MoveComponent moveCompo = Mappers.moveComponent.get(mover);
		
		if (moveSpeed == null) moveSpeed = room.hasEnemies() ? MOVE_SPEED : MOVE_SPEED_IN_CLEARED_ROOMS;
		
		if (moveCompo.currentMoveDestinationPos.x > absolutePos.x) {
			Mappers.spriteComponent.get(mover).flipX = false;
			absolutePos.x = absolutePos.x + moveSpeed.intValue();
			
			if (absolutePos.x >= moveCompo.currentMoveDestinationPos.x) {
				xOffset = moveSpeed.intValue() - (absolutePos.x - moveCompo.currentMoveDestinationPos.x);
				absolutePos.x = moveCompo.currentMoveDestinationPos.x;
				result = performEndOfMovement(mover, moveCompo, room);
			} else {
				moveCompo.arrivedOnTile = false;
				xOffset = moveSpeed.intValue();
			}
		} else if (moveCompo.currentMoveDestinationPos.x < absolutePos.x) {
			Mappers.spriteComponent.get(mover).flipX = true;
			absolutePos.x = absolutePos.x - moveSpeed.intValue();

			if (absolutePos.x <= moveCompo.currentMoveDestinationPos.x) {
				xOffset = -moveSpeed.intValue() - (absolutePos.x - moveCompo.currentMoveDestinationPos.x);
				absolutePos.x = moveCompo.currentMoveDestinationPos.x;
				result = performEndOfMovement(mover, moveCompo, room);    			
			} else {
				moveCompo.arrivedOnTile = false;
				xOffset = -moveSpeed.intValue();
			}
		} else if (moveCompo.currentMoveDestinationPos.y > absolutePos.y) { 
			absolutePos.y = absolutePos.y + moveSpeed.intValue();
			
			if (absolutePos.y >= moveCompo.currentMoveDestinationPos.y) {
				yOffset = moveSpeed.intValue() - (absolutePos.y - moveCompo.currentMoveDestinationPos.y);
				absolutePos.y = moveCompo.currentMoveDestinationPos.y;
				result = performEndOfMovement(mover, moveCompo, room);    			
			} else {
				moveCompo.arrivedOnTile = false;
				yOffset = moveSpeed.intValue();
			}
		} else if (moveCompo.currentMoveDestinationPos.y < absolutePos.y) {
			absolutePos.y = absolutePos.y - moveSpeed.intValue();
			
			if (absolutePos.y <= moveCompo.currentMoveDestinationPos.y) {
				yOffset = -moveSpeed.intValue() - (absolutePos.y - moveCompo.currentMoveDestinationPos.y);
				absolutePos.y = moveCompo.currentMoveDestinationPos.y;
				result = performEndOfMovement(mover, moveCompo, room);    			
			} else {
				moveCompo.arrivedOnTile = false;
				yOffset = -moveSpeed.intValue();
			}
		} else {
			//No move to perform, target already reached
			result = performEndOfMovement(mover, moveCompo, room);
		}
		
		
		if (result != null) {
			gridPositionComponent.absolutePos(absolutePos.x, absolutePos.y);

			for (Component c : mover.getComponents()) {
				if (c instanceof MovableInterface) {
					((MovableInterface) c).performMovement(xOffset, yOffset);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Target has been reached. If it was the last one, stop the movement, otherwise
	 * switch to the next target.
	 * @param moveCompo the moveComponent
	 * @param room the room.
	 * @return true if the movement has ended, false if still in progress.
	 */
	private MovementProgressEnum performEndOfMovement(Entity mover, MoveComponent moveCompo, Room room) {
		moveCompo.arrivedOnTile = true;
		
		int costOfMovementForTilePos = TileUtil.getCostOfMovementForTilePos(moveCompo.currentMoveDestinationTilePos, mover, room);
		moveCompo.setMoveRemaining(moveCompo.getMoveRemaining() - costOfMovementForTilePos);
		
		Optional<Entity> chasm = TileUtil.getEntityWithComponentOnTile(moveCompo.currentMoveDestinationTilePos, ChasmComponent.class, room);
		if (!chasm.isPresent()) {
			moveCompo.setLastWalkableTile(moveCompo.currentMoveDestinationTilePos);
		}
		
		Mappers.gridPositionComponent.get(mover).coord(mover, moveCompo.currentMoveDestinationTilePos, room);
		for (Component c : mover.getComponents()) {
			if (c instanceof MovableInterface) {
				((MovableInterface) c).endMovement(moveCompo.currentMoveDestinationTilePos);
			}
		}
		
		AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(mover);
		if (alterationReceiverComponent != null) {
			alterationReceiverComponent.onArriveOnTile(moveCompo.currentMoveDestinationTilePos, mover, room);
		}
		
		
		// Creep
		Optional<Entity> creep = TileUtil.getEntityWithComponentOnTile(moveCompo.currentMoveDestinationTilePos, CreepComponent.class,room);
		if (creep.isPresent()) {
			DestructibleComponent destructibleComponent = Mappers.destructibleComponent.get(creep.get());
			if (destructibleComponent == null || !destructibleComponent.isDestroyed()) {
				// There is creep on this tile, play its effect
				Mappers.creepComponent.get(creep.get()).onWalk(mover, creep.get(), room);
			}
		}
		
		//Leave creep
		if (Mappers.creepEmitterComponent.has(mover)) {
			Mappers.creepEmitterComponent.get(mover).emit(mover, moveCompo.currentMoveDestinationTilePos, room);
		}
		
		// Things that only the player can do, such as go through a door
		PlayerComponent playerCompo = Mappers.playerComponent.get(mover);
		if (playerCompo != null) {
			// Doors
			Entity doorEntity = TileUtil.getDoorEntityOnTile(moveCompo.currentMoveDestinationTilePos, room);
			if (doorEntity != null) {
				DoorComponent doorCompo = Mappers.doorComponent.get(doorEntity);
				if (doorCompo != null && doorCompo.isOpened() && doorCompo.getTargetedRoom() != null) {
					moveCompo.clearSelectedTileFromPreviousTurn();
					//Change room !!!
					finishRealMovement(mover, room);
					moveCompo.clearMovableTiles();
					
					room.setNextRoom(doorCompo.getTargetedRoom());
//					return null;
				}
			}
		}
		

		boolean movementOver = moveCompo.currentMoveDestinationIndex >= moveCompo.getWayPoints().size();
		moveCompo.incrementCurrentMoveDestinationIndex();
		if (movementOver) {
			return MovementProgressEnum.MOVEMENT_OVER;
		}

		if (moveCompo.getMoveRemaining() <= 0 && !room.hasEnemies() && !moveCompo.isFrozen()) {
			moveCompo.setEndTurnTile(TileUtil.getTileAtGridPos(moveCompo.currentMoveDestinationTilePos, room));
			Vector2 selectedTile = Mappers.gridPositionComponent.get(moveCompo.getSelectedTile()).coord();
			moveCompo.setSelectedTileFromPreviousTurn(TileUtil.getTileAtGridPos(selectedTile, room));
			
			moveCompo.removeFirstWaypoints(moveCompo.currentMoveDestinationIndex);
			
			return MovementProgressEnum.TURN_OVER;
		}
		
		return MovementProgressEnum.IN_PROGRESS;
	}
	
	
	public static void finishRealMovement(Entity e, Room room) {
		MoveComponent moveCompo = Mappers.moveComponent.get(e);
		if (moveCompo == null) return;
		
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(e);
		
		if (moveCompo.getEndTurnTile() != null) {
			gridPositionComponent.coord(e, moveCompo.getEndTurnTile().getGridPos(), room);
			
			for (Component c : e.getComponents()) {
				if (c instanceof MovableInterface) {
					((MovableInterface) c).endMovement(moveCompo.getEndTurnTile().getGridPos());
				}
			}
			moveCompo.setEndTurnTile(null);

		} else if (moveCompo.getSelectedTile() != null) {
			GridPositionComponent selectedTilePos = Mappers.gridPositionComponent.get(moveCompo.getSelectedTile());
			gridPositionComponent.coord(e, selectedTilePos.coord(), room);
			
			for (Component c : e.getComponents()) {
				if (c instanceof MovableInterface) {
					((MovableInterface) c).endMovement(selectedTilePos.coord());
				}
			}
		
			
			StateComponent stateComponent = Mappers.stateComponent.get(e);
			if (stateComponent != null && !stateComponent.isKeepCurrentAnimation()) {
				if (Mappers.flyComponent.has(e)) {
					stateComponent.set(StatesEnum.FLY_STANDING);			
				} else {
					stateComponent.set(StatesEnum.STANDING);
				}
			}
			
		}
		
		moveCompo.moving = false;
		

	}
	
    // Fluent movement END 
    //*********************************************************************
	
	
	public static void placeEntity(Entity e, Vector2 tilePos, Room room) {
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(e);
		gridPositionComponent.coord(e,tilePos, room);
		
		MoveComponent moveComponent = Mappers.moveComponent.get(e);
		if(moveComponent != null) {
			Optional<Entity> chasm = TileUtil.getEntityWithComponentOnTile(tilePos, ChasmComponent.class, room);
			if (!chasm.isPresent()) {
				moveComponent.setLastWalkableTile(tilePos);
			}
		}
		
		for (Component c : e.getComponents()) {
			if (c instanceof MovableInterface) {
				((MovableInterface) c).place(tilePos);
			}
		}
	}
}
