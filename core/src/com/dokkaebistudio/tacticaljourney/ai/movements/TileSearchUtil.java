package com.dokkaebistudio.tacticaljourney.ai.movements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.pathfinding.RoomGraph;
import com.dokkaebistudio.tacticaljourney.ai.pathfinding.RoomHeuristic;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public final class TileSearchUtil {
	
	/** The speed at which entities move on screen. */
	public static final int MOVE_SPEED = 7;


	private TileSearchUtil() {}
    
	
	
	public static void buildMoveTilesSet(Entity moverEntity, MoveComponent moveCompo, Room room) {
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(moverEntity);
		Entity playerTileEntity = room.grid[(int)gridPositionComponent.coord.x][(int)gridPositionComponent.coord.y];
		
		//Find all walkable tiles
		moveCompo.allWalkableTiles = TileSearchUtil.findAllWalkableTiles(playerTileEntity, 1, moveCompo.moveRemaining,room);
		moveCompo.allWalkableTiles.add(playerTileEntity);
		
		//Create entities for each movable tiles to display them
		for (Entity tileCoord : moveCompo.allWalkableTiles) {
			Entity movableTileEntity = room.entityFactory.createMovableTile(Mappers.gridPositionComponent.get(tileCoord).coord);
			moveCompo.movableTiles.add(movableTileEntity);
		}
	}
	
	
	
	public static void buildAttackTilesSet(Entity moverEntity, MoveComponent moveCompo, AttackComponent attackCompo, Room room) {
		
		Set<Entity> attackableTiles = new HashSet<>();
		for (Entity t : moveCompo.allWalkableTiles) {
			GridPositionComponent tilePos = Mappers.gridPositionComponent.get(t);
			Set<Entity> foundAttTiles = check4ContiguousTiles(CheckTypeEnum.ATTACK, (int)tilePos.coord.x, (int)tilePos.coord.y, moveCompo.allWalkableTiles, room);
			attackableTiles.addAll(foundAttTiles);
		}

		attackCompo.allAttackableTiles = attackableTiles;
		
		//Create entities for each attackable tiles to display them
		for (Entity tileCoord : attackCompo.allAttackableTiles) {
			Entity attackableTileEntity = room.entityFactory.createAttackableTile(Mappers.gridPositionComponent.get(tileCoord).coord);
			attackCompo.attackableTiles.add(attackableTileEntity);
		}
	}
	
	
	
	
	
	/**
	 * Build the waypoint list to get the path to follow from start tile to end tile.
	 * @param moveCompo the moveComponent
	 * @param moverCurrentPos the position of the start tile
	 * @param destinationPos the position of the destination
	 * @param room the room
	 * @return the list of waypoints entities
	 */
	public static List<Entity> buildWaypointList(MoveComponent moveCompo, GridPositionComponent moverCurrentPos,
			GridPositionComponent destinationPos, Room room) {
		Entity startTileEntity = room.getTileAtGridPosition(moverCurrentPos.coord);
		List<Entity> movableTilesList = new ArrayList<>(moveCompo.allWalkableTiles);
		RoomGraph roomGraph = new RoomGraph(movableTilesList);
		IndexedAStarPathFinder<Entity> indexedAStarPathFinder = new IndexedAStarPathFinder<Entity>(roomGraph);
		GraphPath<Entity> path = new DefaultGraphPath<Entity>();
		indexedAStarPathFinder.searchNodePath(startTileEntity, room.getTileAtGridPosition(destinationPos.coord), new RoomHeuristic(), path);
		
		int pathNb = -1;
		List<Entity> waypoints = new ArrayList<>();
		Iterator<Entity> iterator = path.iterator();
		while(iterator.hasNext()) {
			pathNb ++;
			Entity next = iterator.next();
			if (pathNb == 0 || !iterator.hasNext()) continue;
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(next);
			Entity waypoint = room.entityFactory.createWaypoint(gridPositionComponent.coord);
			waypoints.add(waypoint);
			
		}
		return waypoints;
	}
	
	
	//**********************************
	// Search algorithm
	
    /**
     * Find all tiles where the entity can move.
     * Recursive method that stops when currentDepth becomes higher than maxDepth
     * @param currentTileEntity the starting tile
     * @param currentDepth the current depth of the search
     * @param maxDepth the max depth of the search
     * @return the set of tiles where the entity can move
     */
	public static Set<Entity> findAllWalkableTiles(Entity currentTileEntity, int currentDepth, int maxDepth, Room room) {
    	Map<Integer, Set<Entity>> allTilesByDepth = new HashMap<>();
    	return findAllWalkableTiles(currentTileEntity, currentDepth, maxDepth, allTilesByDepth, room);
	}
	
    /**
     * Find all tiles where the entity can move.
     * Recursive method that stops when currentDepth becomes higher than maxDepth
     * @param currentTileEntity the starting tile
     * @param currentDepth the current depth of the search
     * @param maxDepth the max depth of the search
     * @param allTilesByDepth the map containing for each depth all the tiles the entity can move onto
     * @return the set of tiles where the entity can move
     */
	private static Set<Entity> findAllWalkableTiles(Entity currentTileEntity, int currentDepth, int maxDepth, 
			Map<Integer, Set<Entity>> allTilesByDepth, Room room) {		
		Set<Entity> walkableTiles = new LinkedHashSet<>();
		
		//Check whether we reached the maxDepth or not
		if (currentDepth <= maxDepth) {
			GridPositionComponent gridPosCompo = Mappers.gridPositionComponent.get(currentTileEntity);
	        Vector2 currentPosition = gridPosCompo.coord;
	        int currentX = (int)currentPosition.x;
	        int currentY = (int)currentPosition.y;
			
	        //Check the 4 contiguous tiles and retrieve the ones we can move onto
	        Set<Entity> tilesToIgnore = null;
	        if (allTilesByDepth.containsKey(currentDepth)) {
	        	tilesToIgnore = allTilesByDepth.get(currentDepth);
	        }
			Set<Entity> previouslyReturnedTiles = check4ContiguousTiles(CheckTypeEnum.MOVEMENT, currentX, currentY, tilesToIgnore, room);
			walkableTiles.addAll(previouslyReturnedTiles);
			
			//Fill the map
			Set<Entity> set = allTilesByDepth.get(currentDepth);
			if (set == null) set = new LinkedHashSet<>();
			set.addAll(previouslyReturnedTiles);
			allTilesByDepth.put(currentDepth, set);
			
			//For each retrieved tile, redo a search until we reach max depth
			for (Entity tile : previouslyReturnedTiles) {
				TileComponent tileComponent = Mappers.tileComponent.get(tile);
				int moveConsumed = tileComponent.type.getMoveConsumed();
	        	Set<Entity> returnedTiles = findAllWalkableTiles(tile, currentDepth + moveConsumed, maxDepth, allTilesByDepth, room);
	        	walkableTiles.addAll(returnedTiles);
	        }
		}
		
		return walkableTiles;
	}
	
	private enum CheckTypeEnum {
		MOVEMENT,
		ATTACK;
	}

	/**
	 * Check the 4 contiguous tiles.
	 * @param type the type of search
	 * @param currentX the current tile X
	 * @param currentY the current tile Y
	 * @return the set of tile entities where it's possible to move
	 */
	private static Set<Entity> check4ContiguousTiles(CheckTypeEnum type, int currentX, int currentY, Set<Entity> tilesToIgnore, Room room) {
		Set<Entity> foundTiles = new LinkedHashSet<>();
		//Left
		if (currentX > 0) {
			if (type == CheckTypeEnum.MOVEMENT) {
				checkOneTileForMovement(new Vector2(currentX - 1, currentY), room, foundTiles, tilesToIgnore);
			} else if (type == CheckTypeEnum.ATTACK) {
				checkOneTileForAttack(new Vector2(currentX - 1, currentY), room, foundTiles, tilesToIgnore);
			}
		}
		//Up
		if (currentY < GameScreen.GRID_H - 1) {
			if (type == CheckTypeEnum.MOVEMENT) {
				checkOneTileForMovement(new Vector2(currentX, currentY + 1), room, foundTiles, tilesToIgnore);
			} else if (type == CheckTypeEnum.ATTACK) {
				checkOneTileForAttack(new Vector2(currentX, currentY + 1), room, foundTiles, tilesToIgnore);
			}
		}
		//Right
		if (currentX < GameScreen.GRID_W - 1) {
			if (type == CheckTypeEnum.MOVEMENT) {
				checkOneTileForMovement(new Vector2(currentX + 1, currentY), room, foundTiles, tilesToIgnore);
			} else if (type == CheckTypeEnum.ATTACK) {
				checkOneTileForAttack(new Vector2(currentX + 1, currentY), room, foundTiles, tilesToIgnore);
			}
		}
		//Down
		if (currentY > 0) {
			if (type == CheckTypeEnum.MOVEMENT) {
				checkOneTileForMovement(new Vector2(currentX, currentY - 1), room, foundTiles, tilesToIgnore);
			} else if (type == CheckTypeEnum.ATTACK) {
				checkOneTileForAttack(new Vector2(currentX, currentY - 1), room, foundTiles, tilesToIgnore);
			}
		}
		return foundTiles;
	}

	/**
	 * Check whether the tileEntity can be moved on.
	 * @param tileEntity the tile to check
	 * @param walkableTiles the set of movable entities
	 */
	private static void checkOneTileForMovement(Vector2 pos, Room room, Set<Entity> walkableTiles, Set<Entity> tilesToIgnore) {
		
		Entity tileEntity = room.getTileAtGridPosition(pos);
		if (tilesToIgnore != null && tilesToIgnore.contains(tileEntity)) {
			return;
		}
		
		TileComponent tileComponent = Mappers.tileComponent.get(tileEntity);
		
		Entity entityOnTile = TileUtil.getSolidEntityOnTile(pos, room);
		if (entityOnTile != null) {
			//There's already something on this tile.
			return;
		}
		
		//TODO: this condition will have to change when we will have to handle items that allow
		//moving past pits for example.
		if (tileComponent.type.isWalkable()) {
			walkableTiles.add(tileEntity);
		}
	}
	
	/**
	 * Check whether the tileEntity can be attacked.
	 * @param tileEntity the tile to check
	 * @param attackableTiles the set of attackable tile entities
	 */
	private static void checkOneTileForAttack(Vector2 pos, Room room, Set<Entity> attackableTiles, Set<Entity> tilesToIgnore) {
		
		Entity tileEntity = room.getTileAtGridPosition(pos);
		if (tilesToIgnore != null && tilesToIgnore.contains(tileEntity)) {
			return;
		}
		
		Entity entityOnTile = TileUtil.getAttackableEntityOnTile(pos, room);
		if (entityOnTile == null) {
			//Nothing to attack on this tile
			return;
		}
		
		TileComponent tileComponent = Mappers.tileComponent.get(tileEntity);
		
		//TODO: this condition will probably have to change, when fighting a flying enemy over a pit
		//for example.
		if (tileComponent.type.isWalkable()) {
			attackableTiles.add(tileEntity);
		}
	}
	
	
}
