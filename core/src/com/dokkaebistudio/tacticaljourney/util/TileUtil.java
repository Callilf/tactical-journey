package com.dokkaebistudio.tacticaljourney.util;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.components.DoorComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.ParentRoomComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * Helpers for everything related to tiles, like finding something on a give tile.
 * @author Callil
 *
 */
public final class TileUtil {

	private TileUtil() {}
	
	/**
	 * Return the solid entity standing on the tile at the given position.
	 * @param position the position
	 * @param engine the engine
	 * @return The entity standing at this position, null if no entity there.
	 */
	public static Entity getSolidEntityOnTile(Vector2 position, Room room) {
		Family family = Family.all(SolidComponent.class, GridPositionComponent.class).get();
		
		ImmutableArray<Entity> allSolids = room.engine.getEntitiesFor(family);
		for (Entity solid : allSolids) {
			ParentRoomComponent parentRoomComponent = Mappers.parentRoomComponent.get(solid);
			if (parentRoomComponent == null || parentRoomComponent.getParentRoom() != room) {
				continue;
			}
			
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(solid);
			if (gridPositionComponent.coord.x == position.x && gridPositionComponent.coord.y == position.y) {
				return solid;
			}
		}
		
		return null;
	}
	
	/**
	 * Return the attackable entity standing on the tile at the given position.
	 * @param position the position
	 * @param engine the engine
	 * @return The entity standing at this position, null if no entity there.
	 */
	public static Entity getAttackableEntityOnTile(Vector2 position, Room room) {
		Family family = Family.all(HealthComponent.class, GridPositionComponent.class).get();
		
		ImmutableArray<Entity> allAttackables = room.engine.getEntitiesFor(family);
		for (Entity attackableEntity : allAttackables) {
			ParentRoomComponent parentRoomComponent = Mappers.parentRoomComponent.get(attackableEntity);
			if (parentRoomComponent == null || parentRoomComponent.getParentRoom() != room) {
				continue;
			}
			
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(attackableEntity);
			if (gridPositionComponent.coord.x == position.x && gridPositionComponent.coord.y == position.y) {
				return attackableEntity;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Return all items standing on the tile at the given position.
	 * @param position the position
	 * @param engine the engine
	 * @return The item entity(ies) standing at this position, empty list if no items there.
	 */
	public static List<Entity> getItemEntityOnTile(Vector2 position, Room room) {
		Family family = Family.all(ItemComponent.class, GridPositionComponent.class).get();
		
		List<Entity> result = new ArrayList<>();
		ImmutableArray<Entity> allItems = room.engine.getEntitiesFor(family);
		for (Entity item : allItems) {
			ParentRoomComponent parentRoomComponent = Mappers.parentRoomComponent.get(item);
			if (parentRoomComponent == null || parentRoomComponent.getParentRoom() != room) {
				continue;
			}
			
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(item);
			if (gridPositionComponent.coord.x == position.x && gridPositionComponent.coord.y == position.y) {
				result.add(item);
			}
		}
		
		return result;
	}
	
	
	/**
	 * Return the door on the tile at the given position.
	 * @param position the position
	 * @param engine the engine
	 * @return The door entity at this position, empty list if no door there.
	 */
	public static Entity getDoorEntityOnTile(Vector2 position, Room room) {
		Family family = Family.all(DoorComponent.class, GridPositionComponent.class).get();
		
		ImmutableArray<Entity> allDoors = room.engine.getEntitiesFor(family);
		for (Entity door : allDoors) {
			ParentRoomComponent parentRoomComponent = Mappers.parentRoomComponent.get(door);
			if (parentRoomComponent == null || parentRoomComponent.getParentRoom() != room) {
				continue;
			}
			
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(door);
			if (gridPositionComponent.coord.x == position.x && gridPositionComponent.coord.y == position.y) {
				return door;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Return the distance in tiles between two tiles. The distance is always positive.
	 * @param startTilePos the start tile position
	 * @param endTilePos the end tile position
	 * @return the distance between startTile and endTile
	 */
	public static int getDistanceBetweenTiles(Vector2 startTilePos, Vector2 endTilePos) {
		float xDistance = Math.abs(startTilePos.x - endTilePos.x);
		float yDistance = Math.abs(startTilePos.y - endTilePos.y);
		return (int) xDistance + (int) yDistance;
	}
}
