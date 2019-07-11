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
import com.dokkaebistudio.tacticaljourney.ai.pathfinding.RoomGraph;
import com.dokkaebistudio.tacticaljourney.ai.pathfinding.RoomHeuristic;
import com.dokkaebistudio.tacticaljourney.ces.components.BlockExplosionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.BlockVisibilityComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class TileSearchService {
	
	/** The speed at which entities move on screen. */
	public static final int MOVE_SPEED = 7;
	
	/** The entity for which we are searching tiles. */
	protected Entity currentEntity;
	
	/** The list of tiles already visited, and the remaining move when they were visited. */
	protected Map<Tile, Integer> visitedTilesWithRemainingMove;

	/** Attackable tiles per distance from the attacker.	 */
	protected Map<Integer, List<Tile>> attackableTilesPerDistance;
	/** The list of positions where an obstacle prevents attacks. */
	protected Set<Vector2> obstacles; 


	public TileSearchService() {
		visitedTilesWithRemainingMove = new HashMap<>();
		attackableTilesPerDistance = new HashMap<>();
		obstacles = new HashSet<>();
	}
    
	
	
	public void buildMoveTilesSet(Entity moverEntity, Room room) {
		long time = System.currentTimeMillis();
		
		currentEntity = moverEntity;
		MoveComponent moveCompo = Mappers.moveComponent.get(moverEntity);

		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(moverEntity);
		Tile moverTile = room.grid[(int)gridPositionComponent.coord().x][(int)gridPositionComponent.coord().y];
		
		//Find all walkable tiles
		
		if (!moveCompo.isFreeMove()) {
			moveCompo.allWalkableTiles = findAllWalkableTiles(moverEntity, moverTile, 1, moveCompo.getMoveRemaining(),room);
			moveCompo.allWalkableTiles.add(moverTile);
		} else {
			moveCompo.allWalkableTiles = new HashSet<>();
			moveCompo.allWalkableTiles.add(moverTile);
			// For rooms with no enemies, just say that all tiles are walkable
			for (Tile[] column : room.grid) {
				for (Tile tile : column) {
					if (tile.isWalkable(moverEntity)) {
						moveCompo.allWalkableTiles.add(tile);
					}
				}
			}
		}
		
		//Create entities for each movable tiles to display them
		for (Tile tileCoord : moveCompo.allWalkableTiles) {
			Entity movableTileEntity = room.entityFactory.createMovableTile(tileCoord.getGridPos(), room);
			moveCompo.movableTiles.add(movableTileEntity);
		}
//		System.out.println("Search movable tiles : " + (System.currentTimeMillis() - time));
	}

	
	
	
	/**
	 * Build the waypoint list to get the path to follow from start tile to end tile.
	 * @param moveCompo the moveComponent
	 * @param moverCurrentPos the position of the start tile
	 * @param destinationPos the position of the destination
	 * @param room the room
	 * @param onlyUseMovableTiles only use the movable tiles to check the path
	 * @return the list of waypoint entities. An empty list if the destination is already beside the startpoint. Null if
	 * no path can be found.
	 */
	public static List<Entity> buildWaypointList(Entity mover, MoveComponent moveCompo, Vector2 moverCurrentPos,
			Vector2 destinationPos, Room room, boolean onlyUseMovableTiles) {
		Tile startTile = room.getTileAtGridPosition(moverCurrentPos);
		List<Tile> movableTilesList = null;
		
		if (onlyUseMovableTiles) {
			movableTilesList = new ArrayList<>(moveCompo.allWalkableTiles);
		} else {
			movableTilesList = new ArrayList<>();
			for (Tile[] column : room.grid) {
				for (Tile tile : column) {
					if (tile.isWalkable(mover)) {
						movableTilesList.add(tile);
					}
				}
			}
			movableTilesList.add(startTile);
		}
		RoomGraph roomGraph = new RoomGraph(mover, movableTilesList);
		IndexedAStarPathFinder<Tile> indexedAStarPathFinder = new IndexedAStarPathFinder<Tile>(roomGraph);
		GraphPath<Tile> path = new DefaultGraphPath<Tile>();
		indexedAStarPathFinder.searchNodePath(startTile, room.getTileAtGridPosition(destinationPos), new RoomHeuristic(), path);
		
		int pathNb = -1;
		List<Entity> waypoints = new ArrayList<>();
		if (path.getCount() == 0) return null;
		
		Iterator<Tile> iterator = path.iterator();
		while(iterator.hasNext()) {
			pathNb ++;
			Tile next = iterator.next();
			if (pathNb == 0 || !iterator.hasNext()) continue;
			Entity waypoint = room.entityFactory.createWaypoint(next.getGridPos(), room);
			waypoints.add(waypoint);
			
		}
		return waypoints;
	}
	
	
	/**
	 * Build the waypoint list to get the path to follow from start tile to end tile.
	 * @param moveCompo the moveComponent
	 * @param moverCurrentPos the position of the start tile
	 * @param destinationPos the position of the destination
	 * @param room the room
	 * @param onlyUseMovableTiles only use the movable tiles to check the path
	 * @return the list of waypoint entities. An empty list if the destination is already beside the startpoint. Null if
	 * no path can be found.
	 */
	public static boolean findPath(Entity mover, MoveComponent moveCompo, Vector2 moverCurrentPos,
			Vector2 destinationPos, Room room, boolean onlyUseMovableTiles) {
		Tile startTile = room.getTileAtGridPosition(moverCurrentPos);
		List<Tile> movableTilesList = null;
		
		if (onlyUseMovableTiles) {
			movableTilesList = new ArrayList<>(moveCompo.allWalkableTiles);
		} else {
			movableTilesList = new ArrayList<>();
			movableTilesList.add(TileUtil.getTileAtGridPos(Mappers.gridPositionComponent.get(mover).coord(), room));
			for (Tile[] column : room.grid) {
				for (Tile tile : column) {
					if (tile.isWalkable(mover)) {
						movableTilesList.add(tile);
					}
				}
			}
		}
		RoomGraph roomGraph = new RoomGraph(mover, movableTilesList);
		IndexedAStarPathFinder<Tile> indexedAStarPathFinder = new IndexedAStarPathFinder<Tile>(roomGraph);
		GraphPath<Tile> path = new DefaultGraphPath<Tile>();
		indexedAStarPathFinder.searchNodePath(startTile, room.getTileAtGridPosition(destinationPos), new RoomHeuristic(), path);
		
		return path.getCount() != 0;
	}
	
	
	//**********************************
	// Search algorithm
	
    /**
     * Find all tiles where the entity can move.
     * Recursive method that stops when currentDepth becomes higher than maxDepth
     * @param mover the moving entity
     * @param currentTile the starting tile
     * @param currentDepth the current depth of the search
     * @param maxDepth the max depth of the search
     * @return the set of tiles where the entity can move
     */
	public Set<Tile> findAllWalkableTiles(Entity mover, Tile currentTile, int currentDepth, int maxDepth, Room room) {
    	Map<Integer, Set<Tile>> allTilesByDepth = new HashMap<>();
    	return findAllWalkableTiles(mover, currentTile, currentDepth, maxDepth, allTilesByDepth, room);
	}
	
    /**
     * Find all tiles where the entity can move.
     * Recursive method that stops when currentDepth becomes higher than maxDepth
     * @param the moving entity
     * @param currentTile the starting tile
     * @param currentDepth the current depth of the search
     * @param maxDepth the max depth of the search
     * @param allTilesByDepth the map containing for each depth all the tiles the entity can move onto
     * @return the set of tiles where the entity can move
     */
	private Set<Tile> findAllWalkableTiles(Entity mover, Tile currentTile, int currentDepth, int maxDepth, 
			Map<Integer, Set<Tile>> allTilesByDepth, Room room) {		
		Set<Tile> walkableTiles = new LinkedHashSet<>();
		
		//Check whether we reached the maxDepth or not
		if (currentDepth <= maxDepth) {
	        Vector2 currentPosition = currentTile.getGridPos();
	        int currentX = (int)currentPosition.x;
	        int currentY = (int)currentPosition.y;
			
	        //Check the 4 contiguous tiles and retrieve the ones we can move onto
	        Set<Tile> tilesToIgnore = null;
	        if (allTilesByDepth.containsKey(currentDepth)) {
	        	tilesToIgnore = allTilesByDepth.get(currentDepth);
	        }
			Set<Tile> previouslyReturnedTiles = check4ContiguousTiles(mover, CheckTypeEnum.MOVEMENT, currentX, currentY, tilesToIgnore, room);
			walkableTiles.addAll(previouslyReturnedTiles);
			
			//Fill the map
			Set<Tile> set = allTilesByDepth.get(currentDepth);
			if (set == null) set = new LinkedHashSet<>();
			set.addAll(previouslyReturnedTiles);
			allTilesByDepth.put(currentDepth, set);
			
			//For each retrieved tile, redo a search until we reach max depth
			for (Tile tile : previouslyReturnedTiles) {
				int moveConsumed = TileUtil.getCostOfMovementForTilePos(tile.getGridPos(), mover, room);
	        	Set<Tile> returnedTiles = findAllWalkableTiles(mover, tile, currentDepth + moveConsumed, 
	        			maxDepth, allTilesByDepth, room);
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
	 * @return the set of tiles where it's possible to move
	 */
	private Set<Tile> check4ContiguousTiles(Entity attacker, CheckTypeEnum type, int currentX, int currentY, Set<Tile> tilesToIgnore, Room room) {
		return check4ContiguousTiles(attacker, null,type, currentX, currentY, tilesToIgnore, room, 1, 1);
	}
		
	
	/**
	 * Check the 4 contiguous tiles.
	 * @param type the type of search
	 * @param currentX the current tile X
	 * @param currentY the current tile Y
	 * @return the set of tiles where it's possible to move
	 */
	public Set<Tile> check4ContiguousTiles(Entity attacker, AttackTypeEnum attackType, CheckTypeEnum type, int currentX, int currentY, Set<Tile> tilesToIgnore, Room room, int maxDepth, int currentDepth) {
		Set<Tile> foundTiles = new LinkedHashSet<>();
		boolean continueSearching = false;
		//Left
		if (currentX > 0) {
			int newX = currentX - 1;
			int newY = currentY;
			PoolableVector2 tempPos = PoolableVector2.create(newX, newY);
			
			if (type == CheckTypeEnum.MOVEMENT) {
				continueSearching = checkOneTileForMovement(tempPos, room, currentDepth, foundTiles, tilesToIgnore);
			} else if (type == CheckTypeEnum.ATTACK) {
				continueSearching = checkOneTileForAttack(attacker, attackType, tempPos, room, currentDepth, foundTiles, tilesToIgnore, true);
			} else if (type == CheckTypeEnum.ATTACK_FOR_DISPLAY) {
				continueSearching = checkOneTileForAttack(attacker, attackType, tempPos, room, currentDepth, foundTiles, tilesToIgnore, false);
			}
			tempPos.free();
			
			if (continueSearching && maxDepth > currentDepth) {
				Set<Tile> subDepthTiles = check4ContiguousTiles(attacker, attackType,type, newX, newY, tilesToIgnore, room, maxDepth, currentDepth + 1);
				foundTiles.addAll(subDepthTiles);
			}
			
		}
		//Up
		if (currentY < GameScreen.GRID_H - 1) {
			int newX = currentX;
			int newY = currentY + 1;
			PoolableVector2 tempPos = PoolableVector2.create(newX, newY);

			if (type == CheckTypeEnum.MOVEMENT) {
				continueSearching = checkOneTileForMovement(tempPos, room, currentDepth, foundTiles, tilesToIgnore);
			} else if (type == CheckTypeEnum.ATTACK) {
				continueSearching = checkOneTileForAttack(attacker, attackType, tempPos, room, currentDepth, foundTiles, tilesToIgnore, true);
			} else if (type == CheckTypeEnum.ATTACK_FOR_DISPLAY) {
				continueSearching = checkOneTileForAttack(attacker, attackType, tempPos, room, currentDepth, foundTiles, tilesToIgnore, false);
			}
			tempPos.free();
			
			if (continueSearching && maxDepth > currentDepth) {
				Set<Tile> subDepthTiles = check4ContiguousTiles(attacker, attackType,type, newX, newY, tilesToIgnore, room, maxDepth, currentDepth + 1);
				foundTiles.addAll(subDepthTiles);
			}
		}
		//Right
		if (currentX < GameScreen.GRID_W - 1) {
			int newX = currentX + 1;
			int newY = currentY;
			PoolableVector2 tempPos = PoolableVector2.create(newX, newY);

			if (type == CheckTypeEnum.MOVEMENT) {
				continueSearching = checkOneTileForMovement(tempPos, room, currentDepth, foundTiles, tilesToIgnore);
			} else if (type == CheckTypeEnum.ATTACK) {
				continueSearching = checkOneTileForAttack(attacker, attackType, tempPos, room, currentDepth, foundTiles, tilesToIgnore, true);
			} else if (type == CheckTypeEnum.ATTACK_FOR_DISPLAY) {
				continueSearching = checkOneTileForAttack(attacker, attackType, tempPos, room, currentDepth, foundTiles, tilesToIgnore, false);
			}
			tempPos.free();
			
			if (continueSearching && maxDepth > currentDepth) {
				Set<Tile> subDepthTiles = check4ContiguousTiles(attacker, attackType,type, newX, newY, tilesToIgnore, room, maxDepth, currentDepth + 1);
				foundTiles.addAll(subDepthTiles);
			}
		}
		//Down
		if (currentY > 0) {
			int newX = currentX;
			int newY = currentY - 1;
			PoolableVector2 tempPos = PoolableVector2.create(newX, newY);

			if (type == CheckTypeEnum.MOVEMENT) {
				continueSearching = checkOneTileForMovement(tempPos, room,currentDepth,foundTiles, tilesToIgnore);
			} else if (type == CheckTypeEnum.ATTACK) {
				continueSearching = checkOneTileForAttack(attacker, attackType, tempPos, room, currentDepth, foundTiles, tilesToIgnore, true);
			} else if (type == CheckTypeEnum.ATTACK_FOR_DISPLAY) {
				continueSearching = checkOneTileForAttack(attacker, attackType, tempPos, room, currentDepth, foundTiles, tilesToIgnore, false);
			}
			tempPos.free();
			
			if (continueSearching && maxDepth > currentDepth) {
				Set<Tile> subDepthTiles = check4ContiguousTiles(attacker, attackType,type, newX, newY, tilesToIgnore, room, maxDepth, currentDepth + 1);
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
	private boolean checkOneTileForMovement(Vector2 pos, Room room, int currentDepth, Set<Tile> walkableTiles, Set<Tile> tilesToIgnore) {
		Tile tile = room.getTileAtGridPosition(pos);

		if (tilesToIgnore != null && tilesToIgnore.contains(tile)) {
			return true;
		}
				
		Entity entityOnTile = TileUtil.getSolidEntityOnTile(pos, room);
		if (entityOnTile != null) {
			//There's already something on this tile.
			return true;
		}
		
		if (tile.isWalkable(currentEntity)) {
			walkableTiles.add(tile);
		}
		return true;
	}
	
	/**
	 * Check whether the tileEntity can be attacked.
	 * @param tileEntity the tile to check
	 * @param attackableTiles the set of attackable tile entities
	 */
	private boolean checkOneTileForAttack(Entity attacker, AttackTypeEnum attackType, Vector2 pos, Room room, int currentDepth, Set<Tile> attackableTiles, Set<Tile> tilesToIgnore, boolean checkEntityToAttack) {
		Tile tile = room.getTileAtGridPosition(pos);
				
		//First, check whether this tiles hasn't already been visited.
		if (visitedTilesWithRemainingMove.containsKey(tile)) {
			if (visitedTilesWithRemainingMove.get(tile) <= currentDepth) {
				//Already visited with more move remaining, so skip this tile
				return false;
			}
		}
		visitedTilesWithRemainingMove.put(tile, currentDepth);

		
		if (tilesToIgnore != null && tilesToIgnore.contains(tile)) {
			return true;
		}
		
		boolean result = true;

		// Obstacles		
		Set<Entity> blockingEntity = null; 
		if (attackType == AttackTypeEnum.EXPLOSION) {
			blockingEntity = TileUtil.getEntitiesWithComponentOnTile(pos, BlockExplosionComponent.class, room);
		} else {
			blockingEntity = TileUtil.getEntitiesWithComponentOnTile(pos, BlockVisibilityComponent.class, room);
		}
		if (!blockingEntity.isEmpty()) {
			result = false;
			obstacles.add(tile.getGridPos());
		}
		
		
		// Check entities to attack
		if (checkEntityToAttack) {
			Entity entityOnTile = TileUtil.getAttackableEntityOnTile(attacker, pos, room);
			if (entityOnTile == null) {
				//Nothing to attack on this tile
				return result;
			} else {
				EnemyComponent currentEnemyCompo = Mappers.enemyComponent.get(currentEntity);
				if (currentEnemyCompo != null) {
					EnemyComponent targetEnemyComponent = Mappers.enemyComponent.get(entityOnTile);
					if (targetEnemyComponent != null && targetEnemyComponent.getFaction() == currentEnemyCompo.getFaction()) {
						//Same faction, do not add the attackable tiles
						return result;
					}
				}
			}
		}
		

		
		//TODO: this condition will probably have to change, when fighting a flying enemy over a pit
		//for example.
		if (attackType.canAttack(tile, currentEntity,room)) {
			
			List<Tile> list = attackableTilesPerDistance.get(currentDepth);
			if (list == null) list = new ArrayList<>();
			list.add(tile);
			
			attackableTiles.add(tile);
		}
		return result;
	}
	
	
}
