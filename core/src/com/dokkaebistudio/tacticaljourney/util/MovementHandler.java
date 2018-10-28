/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.util;

import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.components.DoorComponent;
import com.dokkaebistudio.tacticaljourney.components.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TransformComponent;
import com.dokkaebistudio.tacticaljourney.components.interfaces.MovableInterface;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.display.RenderingSystem;

/**
 * Handles movements of entities.
 * @author Callil
 *
 */
public class MovementHandler {
	
	/** The speed at which entities move on screen. */
	public static final int MOVE_SPEED = 7;
	
	/** The engine that managed entities.*/
	public PooledEngine engine;
	
	/**
	 * Constructor.
	 * @param engine the game engine
	 */
    public MovementHandler(PooledEngine engine) {
        this.engine = engine;
    }
	
	
	public void initiateMovement(Entity e) {
		GridPositionComponent moverCurrentPos = Mappers.gridPositionComponent.get(e);
		MoveComponent moveComponent = Mappers.moveComponent.get(e);
		
		//Add the tranfo component to the entity to perform real movement on screen
		TransformComponent transfoCompo = engine.createComponent(TransformComponent.class);
		Vector2 startPos = RenderingSystem.convertGridPosIntoPixelPos(moverCurrentPos.coord);
		transfoCompo.pos.x = startPos.x;
		transfoCompo.pos.y = startPos.y;
		transfoCompo.pos.z = 1;
		e.add(transfoCompo);
		
		moveComponent.currentMoveDestinationIndex = 0;
		
		for (Component c : e.getComponents()) {
			if (c instanceof MovableInterface) {
				((MovableInterface) c).initiateMovement(moverCurrentPos.coord);
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
	public Boolean performRealMovement(Entity mover, Room room) {
		Boolean result = false;
		float xOffset = 0;
		float yOffset = 0;
		MoveComponent moveCompo = Mappers.moveComponent.get(mover);
		TransformComponent transfoCompo = Mappers.transfoComponent.get(mover);
		
		if (moveCompo.currentMoveDestinationPos.x > transfoCompo.pos.x) { 
			transfoCompo.pos.x = transfoCompo.pos.x + MOVE_SPEED;
			
			if (transfoCompo.pos.x >= moveCompo.currentMoveDestinationPos.x) {
				xOffset = MOVE_SPEED - (transfoCompo.pos.x - moveCompo.currentMoveDestinationPos.x);
				transfoCompo.pos.x = moveCompo.currentMoveDestinationPos.x;
				result = performEndOfMovement(mover, moveCompo, room);
			} else {
				xOffset = MOVE_SPEED;
			}
		} else if (moveCompo.currentMoveDestinationPos.x < transfoCompo.pos.x) {
			transfoCompo.pos.x = transfoCompo.pos.x - MOVE_SPEED;

			if (transfoCompo.pos.x <= moveCompo.currentMoveDestinationPos.x) {
				xOffset = -MOVE_SPEED - (transfoCompo.pos.x - moveCompo.currentMoveDestinationPos.x);
				transfoCompo.pos.x = moveCompo.currentMoveDestinationPos.x;
				result = performEndOfMovement(mover, moveCompo, room);    			
			} else {
				xOffset = -MOVE_SPEED;
			}
		} else if (moveCompo.currentMoveDestinationPos.y > transfoCompo.pos.y) { 
			transfoCompo.pos.y = transfoCompo.pos.y + MOVE_SPEED;
			
			if (transfoCompo.pos.y >= moveCompo.currentMoveDestinationPos.y) {
				yOffset = MOVE_SPEED - (transfoCompo.pos.y - moveCompo.currentMoveDestinationPos.y);
				transfoCompo.pos.y = moveCompo.currentMoveDestinationPos.y;
				result = performEndOfMovement(mover, moveCompo, room);    			
			} else {
				yOffset = MOVE_SPEED;
			}
		} else if (moveCompo.currentMoveDestinationPos.y < transfoCompo.pos.y) {
			transfoCompo.pos.y = transfoCompo.pos.y - MOVE_SPEED;
			
			if (transfoCompo.pos.y <= moveCompo.currentMoveDestinationPos.y) {
				yOffset = -MOVE_SPEED - (transfoCompo.pos.y - moveCompo.currentMoveDestinationPos.y);
				transfoCompo.pos.y = moveCompo.currentMoveDestinationPos.y;
				result = performEndOfMovement(mover, moveCompo, room);    			
			} else {
				yOffset = -MOVE_SPEED;
			}
		} else {
			//No move to perform, target already reached
			result = performEndOfMovement(mover, moveCompo, room);
		}
		
		if (result != null) {
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
	private Boolean performEndOfMovement(Entity mover, MoveComponent moveCompo, Room room) {
		
		
		//Perform any action related to the current tile such as picking up consumables,
		//receiving damages from spikes or fire...
		//TODO move this
		Entity tileAtGridPosition = room.getTileAtGridPosition(moveCompo.currentMoveDestinationTilePos);
		
		// Items pickup
		List<Entity> items = TileUtil.getItemEntityOnTile(moveCompo.currentMoveDestinationTilePos, room);
		for (Entity item : items) {
			ItemComponent itemComponent = ComponentMapper.getFor(ItemComponent.class).get(item);
			if (itemComponent != null && itemComponent.getItemType().isInstantPickUp()) {
				//Pick up this consumable
				itemComponent.pickUp(mover, item, room);
			}
		}
		
		
		
		// Things that only the player can do, such are go through a door
		PlayerComponent playerCompo = Mappers.playerComponent.get(mover);
		if (playerCompo != null) {
			// Doors
			Entity doorEntity = TileUtil.getDoorEntityOnTile(moveCompo.currentMoveDestinationTilePos, room);
			if (doorEntity != null) {
				DoorComponent doorCompo = Mappers.doorComponent.get(doorEntity);
				if (doorCompo != null && doorCompo.isOpened() && doorCompo.getTargetedRoom() != null) {
					//Change room !!!
					finishRealMovement(mover);
					moveCompo.clearMovableTiles();
					room.leaveRoom(doorCompo.getTargetedRoom());
					return null;
				}
			}
		}
		
		
		
		
		if (moveCompo.currentMoveDestinationIndex >= moveCompo.getWayPoints().size()) {
			moveCompo.currentMoveDestinationIndex ++;
			return true;
		}
		moveCompo.currentMoveDestinationIndex ++;
		return false;
	}
	
	
	public void finishRealMovement(Entity e) {
		MoveComponent moveCompo = Mappers.moveComponent.get(e);
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(e);
		
		e.remove(TransformComponent.class);
		GridPositionComponent selectedTilePos = Mappers.gridPositionComponent.get(moveCompo.getSelectedTile());
		gridPositionComponent.coord.set(selectedTilePos.coord);
		
		for (Component c : e.getComponents()) {
			if (c instanceof MovableInterface) {
				((MovableInterface) c).endMovement(selectedTilePos.coord);
			}
		}
	}
	
}
