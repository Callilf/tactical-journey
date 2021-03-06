package com.dokkaebistudio.tacticaljourney.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.ai.movements.TileSearchService;
import com.dokkaebistudio.tacticaljourney.ces.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.WormholeComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.loot.LootableComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.orbs.OrbComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.transition.DoorComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.transition.ExitComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.transition.SecretDoorComponent;
import com.dokkaebistudio.tacticaljourney.enums.DirectionEnum;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;

/**
 * Helpers for everything related to tiles, like finding something on a give tile.
 * @author Callil
 *
 */
public final class TileUtil {

	private TileUtil() {}
	
	public static List<Tile> getAllTiles(Room room) {
		List<Tile> allTiles = new ArrayList<>();
		for (Tile[] x : room.grid) {
			for (Tile y : x) {
				allTiles.add(y);
			}
		}
		return allTiles;
	}
	
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
	
	public static boolean gridPosOutOfRoom(Vector2 gridPos) {
		return gridPos.x < 0 || gridPos.x >= GameScreen.GRID_W || gridPos.y < 0 || gridPos.y >= GameScreen.GRID_H;
	}
	
	/**
	 * Check whether the given x,y coordinates in pixel are on the given entity.
	 * @param x the x pixel coordinate
	 * @param y the y pixel coordinate
	 * @param e the entity
	 * @return true if the x,y pixel coordinates are on the tile where the entity e is standing.
	 */
	public static boolean isPixelPosOnEntity(int x, int y, Entity e) {
		boolean result = false;
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(e);
		if (gridPositionComponent == null) return result;
		
		PoolableVector2 pixelPosIntoGridPos = TileUtil.convertPixelPosIntoGridPos(x, y);
		result = pixelPosIntoGridPos.equals(gridPositionComponent.coord());
		pixelPosIntoGridPos.free();
		return result;
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
		Set<Entity> creepEntities = TileUtil.getEntitiesWithComponentOnTile(pos, CreepComponent.class, room);
		return 1 + creepEntities.stream()
			.map(e -> Mappers.creepComponent.get(e))
			.mapToInt(creepCompo -> creepCompo.getMovementConsumed(mover))
			.sum();
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
		
		if (Mappers.humanoidComponent.has(mover)) {
			// Humanoid: avoid orbs
			Set<Entity> orbs = TileUtil.getEntitiesWithComponentOnTile(pos, OrbComponent.class,  room);
			for (Entity e : orbs) {
				OrbComponent orbComponent = Mappers.orbComponent.get(e);
				if (orbComponent.getParent() == mover) continue;
				cost += orbComponent.getType().getHeuristic(mover);
			}
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
	 * Return true if the "walker" entity can go on the tile at the given grid position.
	 * @param walker the walking entity
	 * @param gridPos the grid position of the tile to test
	 * @param room the room
	 * @return true if the entity can go on this tile, false if not
	 */
	public static boolean isTileWalkableForEntity(Entity walker, Vector2 gridPos, Room room) {
		Tile tile = getTileAtGridPos(gridPos, room);
		if (!tile.isWalkable(walker)) return false;
		
		Optional<Entity> solid = getEntityWithComponentOnTile(gridPos, SolidComponent.class, room);
		if (solid.isPresent()) return false;
		
		return true;
	}
	
	
	
	/**
	 * Return the entity with the given component standing on the tile at the given position.
	 * @param position the position
	 * @param engine the engine
	 * @return The entity with the given component standing on the tile at the given position, null if there is none.
	 */
	public static Optional<Entity> getEntityWithComponentOnTile(Vector2 position, Class<?> componentClass, Room room) {
		return room.getEntitiesAtPositionWithComponent(position, componentClass).stream()
				.findFirst();
	}
	
	/**
	 * Return the solid entity standing on the tile at the given position.
	 * @param position the position
	 * @param engine the engine
	 * @return The entity standing at this position, null if no entity there.
	 */
	public static Set<Entity> getEntitiesWithComponentOnTile(Vector2 position, Class<?> componentClass, Room room) {
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
	 * Return the ally entity standing on the tile at the given position.
	 * @param position the position
	 * @param engine the engine
	 * @return The entity standing at this position, null if no entity there.
	 */
	public static Entity getAllyEntityOnTile(Vector2 position, Room room) {
		Set<Entity> entitiesAtPosition = room.getEntitiesAtPosition(position);
		if (entitiesAtPosition != null) {
			for (Entity e : entitiesAtPosition) {
				if (Mappers.allyComponent.get(e) != null) {
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
	public static Entity getAttackableEntityOnTile(Entity attacker, Vector2 position, Room room) {
		ComponentMapper<?> cmToUse = null;
		if (Mappers.allyComponent.has(attacker)) {
			cmToUse = Mappers.enemyComponent;
		} else if (Mappers.enemyComponent.has(attacker)) {
			cmToUse = Mappers.allyComponent;
		} else {
			cmToUse = Mappers.healthComponent;
		}
		
		Set<Entity> entitiesAtPosition = room.getEntitiesAtPosition(position);
		if (entitiesAtPosition != null) {
			for (Entity e : entitiesAtPosition) {
				if (cmToUse.get(e) != null) {
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
		
		List<PoolableVector2> adjacentPositions = getAdjacentPositions(pos);

		adjacentPositions.forEach( vector -> {
			tiles.add(room.getTileAtGridPosition(vector));
			vector.free();
		});
		
		return tiles;
	}
	
	/**
	 * Get adjacent entities with the given component. Adjacent means on adjacent tiles.
	 * @param pos the position
	 * @param componentClass the component class
	 * @param room the room
	 * @return a list with 4 entities max
	 */
	public static List<Entity> getAdjacentEntitiesWithComponent(Vector2 pos, Class<?> componentClass, Room room) {
		List<Entity> tiles = new ArrayList<>();
		
		List<PoolableVector2> adjacentPositions = getAdjacentPositions(pos);
		
		adjacentPositions.forEach( vector -> {
			Optional<Entity> e = TileUtil.getEntityWithComponentOnTile(vector, componentClass, room);
			if (e.isPresent()) {
				tiles.add(e.get());
			}
			vector.free();
		});
		
		return tiles;
	}
	

	private static List<PoolableVector2> getAdjacentPositions(Vector2 pos) {
		List<PoolableVector2> adjacentPositions = new ArrayList<>();
		if (pos.x > 0) {
			adjacentPositions.add(PoolableVector2.create(pos.x - 1, pos.y));
		}
		if (pos.x < GameScreen.GRID_W - 1) {
			adjacentPositions.add(PoolableVector2.create(pos.x + 1, pos.y));
		}
		if (pos.y > 0) {
			adjacentPositions.add(PoolableVector2.create(pos.x, pos.y - 1));
		}
		if (pos.y < GameScreen.GRID_H - 1) {
			adjacentPositions.add(PoolableVector2.create(pos.x, pos.y + 1));
		}
		return adjacentPositions;
	}
	
	
	/**
	 * Return true if the given tile position has at least one entity with a contextual action on click.
	 * @param x the x pixel pos
	 * @param y the y pixel pos
	 * @param room the room
	 * @return true if the given tile position has at least one entity with a contextual action on click.
	 */
	public static boolean hasEntityWithContextualActionOnClick(int x, int y, Room room) {
		boolean empty = true;
		PoolableVector2 temp = TileUtil.convertPixelPosIntoGridPos(x, y);
		
		if (empty) {
			empty &= !TileUtil.getEntityWithComponentOnTile(temp, ItemComponent.class, room).isPresent();
		}
		if (empty) {
			empty &= !TileUtil.getEntityWithComponentOnTile(temp, LootableComponent.class, room).isPresent();
		}
		if (empty) {
			empty &= !TileUtil.getEntityWithComponentOnTile(temp, WormholeComponent.class, room).isPresent();
		}
		if (empty) {
			empty &= !TileUtil.getEntityWithComponentOnTile(temp, DoorComponent.class, room).isPresent();
		}
		if (empty) {
			empty &= !TileUtil.getEntityWithComponentOnTile(temp, SecretDoorComponent.class, room).isPresent();
		}
		if (empty) {
			empty &= !TileUtil.getEntityWithComponentOnTile(temp, ExitComponent.class, room).isPresent();
		}

		return !empty;
	}
	
	/**
	 * Checks whether the given entity can move the its current position to the given position.
	 * @param mover the mover entity
	 * @param destination the destination grid position
	 * @param room the room
	 * @return true if the entity can go there, false if no path can be found
	 */
	public static boolean canMoveToPosition(Entity mover, Vector2 destination, Room room) {
		MoveComponent moveComponent = Mappers.moveComponent.get(mover);
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(mover);
		return TileSearchService.findPath(mover, moveComponent, gridPositionComponent.coord(), 
				destination, room, false);
	}
	
	/**
	 * Checks whether the given entity can move the its current position to the given position.
	 * @param mover the mover entity
	 * @param destination the destination grid position
	 * @param room the room
	 * @return true if the entity can go there, false if no path can be found
	 */
	public static boolean canMoveToEnemy(Entity mover, Vector2 enemyPos, Room room) {
		MoveComponent moveComponent = Mappers.moveComponent.get(mover);
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(mover);
		
		List<Tile> adjacentTiles = getAdjacentTiles(enemyPos, room);
		for (Tile t : adjacentTiles) {
			if (!t.isWalkable(mover)) continue;
			boolean foundPath = TileSearchService.findPath(mover, moveComponent, gridPositionComponent.coord(), 
					t.getGridPos(), room, false);
			if (foundPath) return true;
		}
		
		return false;
	}
	
	
	public static List<Tile> getTilesAtProximity(Vector2 center, int maxDistance, Room room) {
		return TileUtil.getAllTiles(room).parallelStream()
			.filter(t -> TileUtil.getDistanceBetweenTiles(center, t.getGridPos()) <= maxDistance)
			.collect(Collectors.toList());
	}
	

	public static DirectionEnum getDirectionBetweenTiles(Vector2 origin, Vector2 target) {
		DirectionEnum direction = null;
		if (target.x < origin.x) {
			direction = DirectionEnum.LEFT;
		} else if (target.x > origin.x) {
			direction = DirectionEnum.RIGHT;
		} else if (target.y < origin.y) {
			direction = DirectionEnum.DOWN;
		} else if (target.y > origin.y) {
			direction = DirectionEnum.UP;
		}
		return direction;
	}

}
