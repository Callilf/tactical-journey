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
import com.dokkaebistudio.tacticaljourney.components.BlockExplosionComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.factory.EntityFlagEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class TileSearchService {
	
	/** The speed at which entities move on screen. */
	public static final int MOVE_SPEED = 7;
	
	/** The list of tiles already visited, and the remaining move when they were visited. */
	protected Map<Entity, Integer> visitedTilesWithRemainingMove;

	/** Attackable tiles per distance from the attacker.	 */
	protected Map<Integer, List<Entity>> attackableTilesPerDistance;
	/** The list of positions where an obstacle prevents attacks. */
	protected Set<Vector2> obstacles; 


	public TileSearchService() {
		visitedTilesWithRemainingMove = new HashMap<>();
		attackableTilesPerDistance = new HashMap<>();
		obstacles = new HashSet<>();
	}
    
	
	
	public void buildMoveTilesSet(Entity moverEntity, Room room) {
		long time = System.currentTimeMillis();

		MoveComponent moveCompo = Mappers.moveComponent.get(moverEntity);

		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(moverEntity);
		Entity moverTileEntity = room.grid[(int)gridPositionComponent.coord().x][(int)gridPositionComponent.coord().y];
		
		//Find all walkable tiles
		moveCompo.allWalkableTiles = findAllWalkableTiles(moverTileEntity, 1, moveCompo.moveRemaining,room);
		moveCompo.allWalkableTiles.add(moverTileEntity);
		
		//Create entities for each movable tiles to display them
		for (Entity tileCoord : moveCompo.allWalkableTiles) {
			Entity movableTileEntity = room.entityFactory.createMovableTile(Mappers.gridPositionComponent.get(tileCoord).coord(), room);
			moveCompo.movableTiles.add(movableTileEntity);
		}
		if (moverEntity.flags ==  EntityFlagEnum.PLAYER.getFlag()) {
			System.out.println("search for movement : " + String.valueOf(System.currentTimeMillis() - time));
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
	public List<Entity> buildWaypointList(MoveComponent moveCompo, GridPositionComponent moverCurrentPos,
			GridPositionComponent destinationPos, Room room) {
		Entity startTileEntity = room.getTileAtGridPosition(moverCurrentPos.coord());
		List<Entity> movableTilesList = new ArrayList<>(moveCompo.allWalkableTiles);
		RoomGraph roomGraph = new RoomGraph(movableTilesList);
		IndexedAStarPathFinder<Entity> indexedAStarPathFinder = new IndexedAStarPathFinder<Entity>(roomGraph);
		GraphPath<Entity> path = new DefaultGraphPath<Entity>();
		indexedAStarPathFinder.searchNodePath(startTileEntity, room.getTileAtGridPosition(destinationPos.coord()), new RoomHeuristic(), path);
		
		int pathNb = -1;
		List<Entity> waypoints = new ArrayList<>();
		Iterator<Entity> iterator = path.iterator();
		while(iterator.hasNext()) {
			pathNb ++;
			Entity next = iterator.next();
			if (pathNb == 0 || !iterator.hasNext()) continue;
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(next);
			Entity waypoint = room.entityFactory.createWaypoint(gridPositionComponent.coord(), room);
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
	public Set<Entity> findAllWalkableTiles(Entity currentTileEntity, int currentDepth, int maxDepth, Room room) {
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
	private Set<Entity> findAllWalkableTiles(Entity currentTileEntity, int currentDepth, int maxDepth, 
			Map<Integer, Set<Entity>> allTilesByDepth, Room room) {		
		Set<Entity> walkableTiles = new LinkedHashSet<>();
		
		//Check whether we reached the maxDepth or not
		if (currentDepth <= maxDepth) {
			GridPositionComponent gridPosCompo = Mappers.gridPositionComponent.get(currentTileEntity);
	        Vector2 currentPosition = gridPosCompo.coord();
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
				GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(tile);
				int moveConsumed = TileUtil.getCostOfMovementForTilePos(gridPositionComponent.coord(), room);
	        	Set<Entity> returnedTiles = findAllWalkableTiles(tile, currentDepth + moveConsumed, maxDepth, allTilesByDepth, room);
	        	walkableTiles.addAll(returnedTiles);
	        }
		}
		
		return walkableTiles;
	}
	
	enum CheckTypeEnum {
		MOVEMENT,
		ATTACK,
		ATTACK_FOR_DISPLAY;
	}

	
	
	/**
	 * Check the 4 contiguous tiles.
	 * @param type the type of search
	 * @param currentX the current tile X
	 * @param currentY the current tile Y
	 * @return the set of tile entities where it's possible to move
	 */
	private Set<Entity> check4ContiguousTiles(CheckTypeEnum type, int currentX, int currentY, Set<Entity> tilesToIgnore, Room room) {
		return check4ContiguousTiles(null,type, currentX, currentY, tilesToIgnore, room, 1, 1);
	}
		
	
	/**
	 * Check the 4 contiguous tiles.
	 * @param type the type of search
	 * @param currentX the current tile X
	 * @param currentY the current tile Y
	 * @return the set of tile entities where it's possible to move
	 */
	public Set<Entity> check4ContiguousTiles(AttackTypeEnum attackType, CheckTypeEnum type, int currentX, int currentY, Set<Entity> tilesToIgnore, Room room, int maxDepth, int currentDepth) {
		Set<Entity> foundTiles = new LinkedHashSet<>();
		boolean continueSearching = false;
		//Left
		if (currentX > 0) {
			int newX = currentX - 1;
			int newY = currentY;
			if (type == CheckTypeEnum.MOVEMENT) {
				continueSearching = checkOneTileForMovement(new Vector2(newX, newY), room, currentDepth, foundTiles, tilesToIgnore);
			} else if (type == CheckTypeEnum.ATTACK) {
				continueSearching = checkOneTileForAttack(attackType,new Vector2(newX, newY), room, currentDepth, foundTiles, tilesToIgnore, true);
			} else if (type == CheckTypeEnum.ATTACK_FOR_DISPLAY) {
				continueSearching = checkOneTileForAttack(attackType,new Vector2(newX, newY), room, currentDepth, foundTiles, tilesToIgnore, false);
			}
			
			
			if (continueSearching && maxDepth > currentDepth) {
				Set<Entity> subDepthTiles = check4ContiguousTiles(attackType,type, newX, newY, tilesToIgnore, room, maxDepth, currentDepth + 1);
				foundTiles.addAll(subDepthTiles);
			}
			
		}
		//Up
		if (currentY < GameScreen.GRID_H - 1) {
			int newX = currentX;
			int newY = currentY + 1;
			if (type == CheckTypeEnum.MOVEMENT) {
				continueSearching = checkOneTileForMovement(new Vector2(newX, newY), room, currentDepth, foundTiles, tilesToIgnore);
			} else if (type == CheckTypeEnum.ATTACK) {
				continueSearching = checkOneTileForAttack(attackType,new Vector2(newX, newY), room, currentDepth, foundTiles, tilesToIgnore, true);
			} else if (type == CheckTypeEnum.ATTACK_FOR_DISPLAY) {
				continueSearching = checkOneTileForAttack(attackType,new Vector2(newX, newY), room, currentDepth, foundTiles, tilesToIgnore, false);
			}
			
			if (continueSearching && maxDepth > currentDepth) {
				Set<Entity> subDepthTiles = check4ContiguousTiles(attackType,type, newX, newY, tilesToIgnore, room, maxDepth, currentDepth + 1);
				foundTiles.addAll(subDepthTiles);
			}
		}
		//Right
		if (currentX < GameScreen.GRID_W - 1) {
			int newX = currentX + 1;
			int newY = currentY;
			if (type == CheckTypeEnum.MOVEMENT) {
				continueSearching = checkOneTileForMovement(new Vector2(newX, newY), room, currentDepth, foundTiles, tilesToIgnore);
			} else if (type == CheckTypeEnum.ATTACK) {
				continueSearching = checkOneTileForAttack(attackType,new Vector2(newX, newY), room, currentDepth, foundTiles, tilesToIgnore, true);
			} else if (type == CheckTypeEnum.ATTACK_FOR_DISPLAY) {
				continueSearching = checkOneTileForAttack(attackType,new Vector2(newX, newY), room, currentDepth, foundTiles, tilesToIgnore, false);
			}
			
			if (continueSearching && maxDepth > currentDepth) {
				Set<Entity> subDepthTiles = check4ContiguousTiles(attackType,type, newX, newY, tilesToIgnore, room, maxDepth, currentDepth + 1);
				foundTiles.addAll(subDepthTiles);
			}
		}
		//Down
		if (currentY > 0) {
			int newX = currentX;
			int newY = currentY - 1;
			if (type == CheckTypeEnum.MOVEMENT) {
				continueSearching = checkOneTileForMovement(new Vector2(newX, newY), room,currentDepth,foundTiles, tilesToIgnore);
			} else if (type == CheckTypeEnum.ATTACK) {
				continueSearching = checkOneTileForAttack(attackType,new Vector2(newX, newY), room, currentDepth, foundTiles, tilesToIgnore, true);
			} else if (type == CheckTypeEnum.ATTACK_FOR_DISPLAY) {
				continueSearching = checkOneTileForAttack(attackType,new Vector2(newX, newY), room, currentDepth, foundTiles, tilesToIgnore, false);
			}
			
			if (continueSearching && maxDepth > currentDepth) {
				Set<Entity> subDepthTiles = check4ContiguousTiles(attackType,type, newX, newY, tilesToIgnore, room, maxDepth, currentDepth + 1);
				foundTiles.addAll(subDepthTiles);
			}
		}
		
		
		return foundTiles;
	}

	/**
	 * Check whether the tileEntity can be moved on.
	 * @param tileEntity the tile to check
	 * @param walkableTiles the set of movable entities
	 */
	private boolean checkOneTileForMovement(Vector2 pos, Room room, int currentDepth, Set<Entity> walkableTiles, Set<Entity> tilesToIgnore) {
		Entity tileEntity = room.getTileAtGridPosition(pos);

		if (tilesToIgnore != null && tilesToIgnore.contains(tileEntity)) {
			return true;
		}
		
		TileComponent tileComponent = Mappers.tileComponent.get(tileEntity);
		
		Entity entityOnTile = TileUtil.getSolidEntityOnTile(pos, room);
		if (entityOnTile != null) {
			//There's already something on this tile.
			return true;
		}
		
		//TODO: this condition will have to change when we will have to handle items that allow
		//moving past pits for example.
		if (tileComponent.type.isWalkable()) {
			walkableTiles.add(tileEntity);
		}
		return true;
	}
	
	/**
	 * Check whether the tileEntity can be attacked.
	 * @param tileEntity the tile to check
	 * @param attackableTiles the set of attackable tile entities
	 */
	private boolean checkOneTileForAttack(AttackTypeEnum attackType, Vector2 pos, Room room, int currentDepth, Set<Entity> attackableTiles, Set<Entity> tilesToIgnore, boolean checkEntityToAttack) {
		Entity tileEntity = room.getTileAtGridPosition(pos);
		
		//First, check whether this tiles hasn't already been visited.
		if (visitedTilesWithRemainingMove.containsKey(tileEntity)) {
			if (visitedTilesWithRemainingMove.get(tileEntity) <= currentDepth) {
				//Already visited with more move remaining, so skip this tile
				return false;
			}
		}
		visitedTilesWithRemainingMove.put(tileEntity, currentDepth);

		
		if (tilesToIgnore != null && tilesToIgnore.contains(tileEntity)) {
			return true;
		}
		
		if (checkEntityToAttack) {
			Entity entityOnTile = TileUtil.getAttackableEntityOnTile(pos, room);
			if (entityOnTile == null) {
				//Nothing to attack on this tile
				return true;
			}
		}
		
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(tileEntity);
		
		Set<Entity> blockingEntity = null; 
		if (attackType == AttackTypeEnum.EXPLOSION) {
			blockingEntity = TileUtil.getEntityWithComponentOnTile(pos, BlockExplosionComponent.class, room);
		} else {
			blockingEntity = TileUtil.getEntityWithComponentOnTile(pos, SolidComponent.class, room);
		}
		if (!blockingEntity.isEmpty()) {
			obstacles.add(gridPositionComponent.coord());
		}
		
		
		//TODO: this condition will probably have to change, when fighting a flying enemy over a pit
		//for example.
		if (attackType.canAttack(tileEntity, room)) {
			
			List<Entity> list = attackableTilesPerDistance.get(currentDepth);
			if (list == null) list = new ArrayList<>();
			list.add(tileEntity);
			
			attackableTiles.add(tileEntity);
		}
		return true;
	}
	
	
}
