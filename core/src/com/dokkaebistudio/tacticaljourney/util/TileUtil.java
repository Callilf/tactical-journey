package com.dokkaebistudio.tacticaljourney.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;

/**
 * Helpers for everything related to tiles, like finding something on a give tile.
 * @author Callil
 *
 */
public final class TileUtil {

	private TileUtil() {}
	
	
	/**
	 * Convert a grid position for ex (5,4) into pixel position (450,664).
	 * @param gridPos the grid position
	 * @return the real position
	 */
	public static PoolableVector2 convertGridPosIntoPixelPos(Vector2 gridPos) {
		float x = gridPos.x * GameScreen.GRID_SIZE + GameScreen.LEFT_RIGHT_PADDING;
		float y = gridPos.y * GameScreen.GRID_SIZE + GameScreen.BOTTOM_MENU_HEIGHT;
		return PoolableVector2.create(x,y);
	}
	
	/**
	 * Convert a grid position for ex (5,4) into pixel position (450,664).
	 * @param gridPos the grid position
	 * @param pixelPos the pixel position updated
	 */
	public static void convertGridPosIntoPixelPos(Vector2 gridPos, Vector2 pixelPos) {
		pixelPos.x = gridPos.x * GameScreen.GRID_SIZE + GameScreen.LEFT_RIGHT_PADDING;
		pixelPos.y = gridPos.y * GameScreen.GRID_SIZE + GameScreen.BOTTOM_MENU_HEIGHT;
	}
	
	/**
	 * Convert a grid position for ex (5,4) into pixel position (450,664).
	 * @param gridPos the grid position
	 * @return the real position
	 */
	public static PoolableVector2 convertPixelPosIntoGridPos(Vector2 pixelPos) {
		float x = (float)Math.floor((pixelPos.x - GameScreen.LEFT_RIGHT_PADDING) / GameScreen.GRID_SIZE);
		float y = (float)Math.floor((pixelPos.y - GameScreen.BOTTOM_MENU_HEIGHT) / GameScreen.GRID_SIZE);
		return PoolableVector2.create(x,y);
	}
	
	/**
	 * Convert a grid position for ex (5,4) into pixel position (450,664).
	 * @param gridPos the grid position
	 * @return the real position
	 */
	public static PoolableVector2 convertPixelPosIntoGridPos(int x, int y) {
		float newX = (float)Math.floor((x - GameScreen.LEFT_RIGHT_PADDING) / GameScreen.GRID_SIZE);
		float newY = (float)Math.floor((y - GameScreen.BOTTOM_MENU_HEIGHT) / GameScreen.GRID_SIZE);
		return PoolableVector2.create(newX, newY);
	}
	
	
	/**
	 * Get the tile on which the given entity is standing.
	 * @param e the entity
	 * @param r the room
	 * @return the tile on which the entity is standing.
	 */
	public static Tile getTileFromEntity(Entity e, Room r) {
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(e);
		return getTileAtGridPos(gridPositionComponent.coord(), r);
	}
	
	/**
	 * Return the cost of movement for the given tile position in the given room.
	 * @param pos the position
	 * @param room the room
	 * @return the cost of movement
	 */
	public static int getCostOfMovementForTilePos(Vector2 pos, Entity mover, Room room) {
		int cost = 1;
		Set<Entity> creepEntities = TileUtil.getEntitiesWithComponentOnTile(pos, CreepComponent.class, room);
		for (Entity e : creepEntities) {
			CreepComponent creepComponent = Mappers.creepComponent.get(e);
			cost += creepComponent.getMovementConsumed(mover);
		}
		return cost;
	}
	
	/**
	 * Return the cost of movement for the given tile position in the given room.
	 * @param pos the position
	 * @param room the room
	 * @return the cost of movement
	 */
	public static int getHeuristicCostForTilePos(Vector2 pos, Entity mover, Room room) {
		int cost = 1;
		Set<Entity> creepEntities = TileUtil.getEntitiesWithComponentOnTile(pos, CreepComponent.class, room);
		for (Entity e : creepEntities) {
			CreepComponent creepComponent = Mappers.creepComponent.get(e);
			cost += creepComponent.getMovementConsumed(mover);
			cost += creepComponent.getHeuristic(mover);
		}
		return cost;
	}
	
	/**
	 * Return the tile at the given grid position.
	 * @param gridPos the position
	 * @param room the room
	 * @return the tile at the given position
	 */
	public static Tile getTileAtGridPos(Vector2 gridPos, Room room) {
		
//		//TODO test
//		Set<Entity> entitiesAtPosition = room.getEntitiesAtPosition(gridPos);
//		if (entitiesAtPosition != null) {
//			for (Entity e : entitiesAtPosition) {
//				if (Mappers.tileComponent.get(e) != null) {
//					return e;
//				}
//			}
//		}
//		
//		return null;
		
		return room.grid[(int) gridPos.x][(int) gridPos.y];
	}
	
	/**
	 * Return the tile at the given pixel position.
	 * @param gridPos the position
	 * @param room the room
	 * @return the tile at the given position
	 */
	public static Tile getTileAtPixelPos(Vector2 pixelPos, Room room) {
		PoolableVector2 gridPos = convertPixelPosIntoGridPos(pixelPos);
		Tile e = getTileAtGridPos(gridPos, room);
		gridPos.free();
		
		return e;
	}
	
	
	
	/**
	 * Return the entity with the given component standing on the tile at the given position.
	 * @param position the position
	 * @param engine the engine
	 * @return The entity with the given component standing on the tile at the given position, null if there is none.
	 */
	public static Entity getEntityWithComponentOnTile(Vector2 position, Class componentClass, Room room) {
		Set<Entity> entities = room.getEntitiesAtPositionWithComponent(position, componentClass);
		for (Entity e : entities) {
			return e;
		}
		return null;
	}
	
	/**
	 * Return the solid entity standing on the tile at the given position.
	 * @param position the position
	 * @param engine the engine
	 * @return The entity standing at this position, null if no entity there.
	 */
	public static Set<Entity> getEntitiesWithComponentOnTile(Vector2 position, Class componentClass, Room room) {
		return room.getEntitiesAtPositionWithComponent(position, componentClass);
	}
	
	
	/**
	 * Return the solid entity standing on the tile at the given position.
	 * @param position the position
	 * @param engine the engine
	 * @return The entity standing at this position, null if no entity there.
	 */
	public static Entity getSolidEntityOnTile(Vector2 position, Room room) {
		Set<Entity> entitiesAtPosition = room.getEntitiesAtPosition(position);
		if (entitiesAtPosition != null) {
			for (Entity e : entitiesAtPosition) {
				if (Mappers.solidComponent.get(e) != null) {
					return e;
				}
			}
		}
		
		return null;
	}
	
	
	/**
	 * Return the enemy entity standing on the tile at the given position.
	 * @param position the position
	 * @param engine the engine
	 * @return The entity standing at this position, null if no entity there.
	 */
	public static Entity getEnemyEntityOnTile(Vector2 position, Room room) {
		Set<Entity> entitiesAtPosition = room.getEntitiesAtPosition(position);
		if (entitiesAtPosition != null) {
			for (Entity e : entitiesAtPosition) {
				if (Mappers.enemyComponent.get(e) != null) {
					return e;
				}
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
		Set<Entity> entitiesAtPosition = room.getEntitiesAtPosition(position);
		if (entitiesAtPosition != null) {
			for (Entity e : entitiesAtPosition) {
				if (Mappers.healthComponent.get(e) != null) {
					return e;
				}
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
		List<Entity> result = new ArrayList<>();
		Set<Entity> entitiesAtPosition = room.getEntitiesAtPosition(position);
		if (entitiesAtPosition != null) {
			for (Entity e : entitiesAtPosition) {
				if (Mappers.itemComponent.get(e) != null) {
					result.add(e);
				}
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
		Set<Entity> entitiesAtPosition = room.getEntitiesAtPosition(position);
		if (entitiesAtPosition != null) {
			for (Entity e : entitiesAtPosition) {
				if (Mappers.doorComponent.get(e) != null) {
					return e;
				}
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
	
	
	/**
	 * Get adjacent tiles.
	 * @param pos the position
	 * @param room the room
	 * @return a list with 4 tiles max
	 */
	public static List<Tile> getAdjacentTiles(Vector2 pos, Room room) {
		List<Tile> tiles = new ArrayList<>();
		
		PoolableVector2 temp = PoolableVector2.create(pos);
		if (temp.x > 0) {
			temp.x -= 1;
			tiles.add(room.getTileAtGridPosition(temp));
			temp.x += 1;
		}
		if (temp.x < GameScreen.GRID_W - 1) {
			temp.x += 1;
			tiles.add(room.getTileAtGridPosition(temp));
			temp.x -= 1;
		}
		if (temp.y > 0) {
			temp.y -= 1;
			tiles.add(room.getTileAtGridPosition(temp));
			temp.y += 1;
		}
		if (temp.y < GameScreen.GRID_H - 1) {
			temp.y += 1;
			tiles.add(room.getTileAtGridPosition(temp));
			temp.y -= 1;
		}
		
		return tiles;
	}
	
	/**
	 * Get adjacent entities with the given component. Adjacent means on adjacent tiles.
	 * @param pos the position
	 * @param componentClass the component class
	 * @param room the room
	 * @return a list with 4 entities max
	 */
	public static List<Entity> getAdjacentEntitiesWithComponent(Vector2 pos, Class componentClass, Room room) {
		List<Entity> tiles = new ArrayList<>();
		
		PoolableVector2 temp = PoolableVector2.create(pos);
		if (temp.x > 0) {
			temp.x -= 1;
			Entity e = TileUtil.getEntityWithComponentOnTile(temp, componentClass, room);
			if (e != null) {
				tiles.add(e);
			}
			temp.x += 1;
		}
		if (temp.x < GameScreen.GRID_W - 1) {
			temp.x += 1;
			Entity e = TileUtil.getEntityWithComponentOnTile(temp, componentClass, room);
			if (e != null) {
				tiles.add(e);
			}
			temp.x -= 1;
		}
		if (temp.y > 0) {
			temp.y -= 1;
			Entity e = TileUtil.getEntityWithComponentOnTile(temp, componentClass, room);
			if (e != null) {
				tiles.add(e);
			}
			temp.y += 1;
		}
		if (temp.y < GameScreen.GRID_H - 1) {
			temp.y += 1;
			Entity e = TileUtil.getEntityWithComponentOnTile(temp, componentClass, room);
			if (e != null) {
				tiles.add(e);
			}
			temp.y -= 1;
		}
		
		return tiles;
	}
}
