package com.dokkaebistudio.tacticaljourney.ai.movements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.pathfinding.RoomGraph;
import com.dokkaebistudio.tacticaljourney.ai.pathfinding.RoomHeuristic;
import com.dokkaebistudio.tacticaljourney.components.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;

public final class MovableTileSearchUtil {

	private MovableTileSearchUtil() {}
    
	
	
	public static void buildMoveTilesSet(Entity moverEntity, MoveComponent moveCompo, Room room, 
			ComponentMapper<GridPositionComponent> gridPositionM,
			ComponentMapper<TileComponent> tileCM) {
		GridPositionComponent gridPositionComponent = gridPositionM.get(moverEntity);
		Entity playerTileEntity = room.grid[(int)gridPositionComponent.coord.x][(int)gridPositionComponent.coord.y];
		
		//Find all walkable tiles
		moveCompo.allWalkableTiles = MovableTileSearchUtil.findAllWalkableTiles(playerTileEntity, 1, moveCompo.moveRemaining,room, gridPositionM, tileCM);
		moveCompo.allWalkableTiles.add(playerTileEntity);
		
		//Create entities for each movable tiles to display them
		for (Entity tileCoord : moveCompo.allWalkableTiles) {
			Entity movableTileEntity = room.entityFactory.createMovableTile(gridPositionM.get(tileCoord).coord);
			moveCompo.movableTiles.add(movableTileEntity);
		}
	}
	
	/**
	 * Build the waypoint list to get the path to follow from start tile to end tile.
	 * @param moveCompo the moveComponent
	 * @param moverCurrentPos the position of the start tile
	 * @param destinationPos the position of the destination
	 * @param room the room
	 * @param gridPositionM the gridPosition component mapper
	 * @return the list of waypoints entities
	 */
	public static List<Entity> buildWaypointList(MoveComponent moveCompo, GridPositionComponent moverCurrentPos,
			GridPositionComponent destinationPos, Room room, ComponentMapper<GridPositionComponent> gridPositionM) {
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
			GridPositionComponent gridPositionComponent = gridPositionM.get(next);
			Entity waypoint = room.entityFactory.createWaypoint(gridPositionComponent.coord);
			waypoints.add(waypoint);
			
		}
		return waypoints;
	}
	
	
	
    /**
     * Find all tiles where the entity can move.
     * Recursive method that stops when currentDepth becomes higher than maxDepth
     * @param currentTileEntity the starting tile
     * @param currentDepth the current depth of the search
     * @param maxDepth the max depth of the search
     * @return the set of tiles where the entity can move
     */
	public static Set<Entity> findAllWalkableTiles(Entity currentTileEntity, int currentDepth, int maxDepth, Room room,
			ComponentMapper<GridPositionComponent> gridCompoM, ComponentMapper<TileComponent> tileCM) {
    	Map<Integer, Set<Entity>> allTilesByDepth = new HashMap<>();
    	return findAllWalkableTiles(currentTileEntity, currentDepth, maxDepth, allTilesByDepth, room, gridCompoM, tileCM);
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
			Map<Integer, Set<Entity>> allTilesByDepth, Room room,
			ComponentMapper<GridPositionComponent> gridCompoM, ComponentMapper<TileComponent> tileCM) {		
		Set<Entity> walkableTiles = new LinkedHashSet<>();
		
		//Check whether we reached the maxDepth or not
		if (currentDepth <= maxDepth) {
			GridPositionComponent gridPosCompo = gridCompoM.get(currentTileEntity);
	        Vector2 currentPosition = gridPosCompo.coord;
	        int currentX = (int)currentPosition.x;
	        int currentY = (int)currentPosition.y;
			
	        //Check the 4 contiguous tiles and retrieve the ones we can move onto
	        Set<Entity> tilesToIgnore = null;
	        if (allTilesByDepth.containsKey(currentDepth)) {
	        	tilesToIgnore = allTilesByDepth.get(currentDepth);
	        }
			Set<Entity> previouslyReturnedTiles = check4ContiguousTiles(currentX, currentY, tilesToIgnore, room, tileCM);
			walkableTiles.addAll(previouslyReturnedTiles);
			
			//Fill the map
			Set<Entity> set = allTilesByDepth.get(currentDepth);
			if (set == null) set = new LinkedHashSet<>();
			set.addAll(previouslyReturnedTiles);
			allTilesByDepth.put(currentDepth, set);
			
			//For each retrieved tile, redo a search until we reach max depth
			for (Entity tile : previouslyReturnedTiles) {
				TileComponent tileComponent = tileCM.get(tile);
				int moveConsumed = tileComponent.type.getMoveConsumed();
	        	Set<Entity> returnedTiles = findAllWalkableTiles(tile, currentDepth + moveConsumed, maxDepth, allTilesByDepth, room, gridCompoM, tileCM);
	        	walkableTiles.addAll(returnedTiles);
	        }
		}
		
		return walkableTiles;
	}

	/**
	 * Check the 4 contiguous tiles.
	 * @param currentX the current tile X
	 * @param currentY the current tile Y
	 * @return the set of tile entities where it's possible to move
	 */
	private static Set<Entity> check4ContiguousTiles(int currentX, int currentY, Set<Entity> tilesToIgnore, Room room,
			ComponentMapper<TileComponent> tileCM) {
		Set<Entity> walkableTiles = new LinkedHashSet<>();
		//Left
		if (currentX > 0) {
			Entity tileEntity = room.grid[currentX - 1][currentY];
			checkOneTile(tileEntity, walkableTiles, tilesToIgnore, tileCM);
		}
		//Up
		if (currentY < GameScreen.GRID_H - 1) {
			Entity tileEntity = room.grid[currentX][currentY + 1];
			checkOneTile(tileEntity, walkableTiles, tilesToIgnore, tileCM);
		}
		//Right
		if (currentX < GameScreen.GRID_W - 1) {
			Entity tileEntity = room.grid[currentX + 1][currentY];
			checkOneTile(tileEntity, walkableTiles, tilesToIgnore, tileCM);
		}
		//Down
		if (currentY > 0) {
			Entity tileEntity = room.grid[currentX][currentY - 1];
			checkOneTile(tileEntity, walkableTiles, tilesToIgnore, tileCM);
		}
		return walkableTiles;
	}

	/**
	 * Check whether the tileEntity can be moved on.
	 * @param tileEntity the tile to check
	 * @param walkableTiles the set of movable entities
	 */
	private static void checkOneTile(Entity tileEntity, Set<Entity> walkableTiles, Set<Entity> tilesToIgnore,
			ComponentMapper<TileComponent> tileCM) {
		if (tilesToIgnore != null && tilesToIgnore.contains(tileEntity)) {
			return;
		}
		
		TileComponent tileComponent = tileCM.get(tileEntity);
		//TODO: this condition will have to change when we will have to handle items that allow
		//moving past pits for example.
		if (tileComponent.type.isWalkable()) {
			walkableTiles.add(tileEntity);
		}
	}
	
}
