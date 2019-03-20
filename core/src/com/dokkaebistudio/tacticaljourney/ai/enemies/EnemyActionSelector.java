package com.dokkaebistudio.tacticaljourney.ai.enemies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTileSearchService;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.orbs.OrbComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.enemies.enums.EnemyMoveStrategy;
import com.dokkaebistudio.tacticaljourney.enemies.tribesmen.EnemyTribesmanScout;
import com.dokkaebistudio.tacticaljourney.enemies.tribesmen.EnemyTribesmanShaman;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class EnemyActionSelector {
	
	/**
	 * Select the tile to move on.
	 * @param enemyEntity the enemy entity
	 * @param engine the pooled engine
	 * @return the tile to move on, null if no move is needed
	 */
	public static Entity selectTileToMove(Entity enemyEntity, Room room, AttackTileSearchService atss) {
    	Entity selectedTile = null;
    	MoveComponent moveComponent = Mappers.moveComponent.get(enemyEntity);
    	GridPositionComponent enemyPos = Mappers.gridPositionComponent.get(enemyEntity);
    	EnemyComponent enemyComponent = Mappers.enemyComponent.get(enemyEntity);
    	
    	EnemyMoveStrategy moveStrategy = enemyComponent.isAlerted() ? enemyComponent.getAlertedMoveStrategy() : enemyComponent.getBasicMoveStrategy();
    	    	
    	switch (moveStrategy) {
    	case MOVE_TOWARD_PLAYER :
	    	//Strategy 1 : move toward the player(s)
	    	selectedTile = moveTowardPlayerStrategy(enemyEntity, room, moveComponent, enemyPos);
	    	break;
	    	
    	case TRIBESMAN_SCOUT_STRATEGY:
	    	selectedTile = tribesmanScoutStrategy(enemyEntity, room, moveComponent, enemyPos);
    		break;
    		
    	case MOVE_RANDOMLY :
    		selectedTile = moveRandomly(enemyEntity, room, moveComponent);
    		break;
    		
    	case MOVE_RANDOMLY_BUT_ATTACK_IF_POSSIBLE:
    		selectedTile = moveRandomlyButAttackIfPossible(enemyEntity, room, moveComponent, enemyPos);
    		break;
    		
    	case MOVE_RANDOMLY_BUT_ATTACK_FROM_RANGE_IF_POSSIBLE:
    		AttackComponent attackCompo = Mappers.attackComponent.get(enemyEntity);
    		selectedTile = moveRandomlyButAttackFromRangeIfPossible(enemyEntity, room, moveComponent, attackCompo,enemyPos, atss);
    		break;

    		
    	case STANDING_STILL:
    	default:
    		break;
	    	
    	}
    	
    	return selectedTile;
	}

	private static Entity moveRandomly(Entity enemyEntity, Room room, MoveComponent moveComponent) {
		Entity selectedTile = null;
		RandomXS128 random = RandomSingleton.getInstance().getUnseededRandom();

		List<Entity> movableTilesList = new ArrayList<>(moveComponent.movableTiles);
		Collections.shuffle(movableTilesList, random);

		//TODO improve this
		for (Entity tile : movableTilesList) {
			GridPositionComponent tilePos = Mappers.gridPositionComponent.get(tile);
			if (getOrbHeuristic(tilePos.coord(), enemyEntity, room) > 0) continue;
			selectedTile = tile;
			break;
		}
		
		return selectedTile;
	}

	/**
	 * Select the best tile to move toward the player.
	 * @param engine the engine
	 * @param moveComponent the move component of the enemy
	 * @param enemyPos the enemy position
	 * @return the selected tile. Null if no move needed
	 */
	private static Entity moveTowardPlayerStrategy(Entity enemyEntity, Room room,
			MoveComponent moveComponent, GridPositionComponent enemyPos) {
		Entity selectedTile = null;
		Family family = Family.all(PlayerComponent.class, GridPositionComponent.class).get();
		ImmutableArray<Entity> allPlayers = room.engine.getEntitiesFor(family);
		
		//First find the closest target
		int shortestDistance = -1;
		Entity target = null;
		for (Entity p : allPlayers) {
			GridPositionComponent playerPos = Mappers.gridPositionComponent.get(p);
			int distance = TileUtil.getDistanceBetweenTiles(enemyPos.coord(), playerPos.coord());
			if (target == null || distance < shortestDistance) {
				shortestDistance = distance;
				target = p;
			}
		}
		
		AttackComponent attackComponent = Mappers.attackComponent.get(enemyEntity);

		if (shortestDistance == attackComponent.getRangeMax()) {
			//Already facing the player, don't need to move.
		} else {
			if (target != null) {
				GridPositionComponent targetPos = Mappers.gridPositionComponent.get(target);
				shortestDistance = -1;
				
		    	for (Entity t : moveComponent.movableTiles) {
		    		GridPositionComponent tilePos = Mappers.gridPositionComponent.get(t);
		    		int distance = TileUtil.getDistanceBetweenTiles(targetPos.coord(), tilePos.coord());
		    		if (selectedTile == null 
		    				|| (shortestDistance > attackComponent.getRangeMax() && distance < shortestDistance)
		    				|| (shortestDistance <= attackComponent.getRangeMax() && distance > shortestDistance)) {
		    			if (getOrbHeuristic(tilePos.coord(), enemyEntity, room) > 0) {
		    				continue;
		    			}
		    			
		    			selectedTile = t;
		    			shortestDistance = distance;
		    		}
		    		
		    		if (shortestDistance == attackComponent.getRangeMax()) {
		    			break;
		    		}
		    	}
			} else {
				//No target, move randomly
				selectedTile = moveRandomly(enemyEntity, room, moveComponent);				
			}
		}
		return selectedTile;
	}
	
	
	/**
	 * Find and stay close to another member of the same faction. If no other member, go attack the playe.r
	 * @param engine the engine
	 * @param moveComponent the move component of the enemy
	 * @param enemyPos the enemy position
	 * @return the selected tile. Null if no move needed
	 */
	private static Entity tribesmanScoutStrategy(Entity enemyEntity, Room room,
			MoveComponent moveComponent, GridPositionComponent enemyPos) {
		Entity selectedTile = null;
		EnemyComponent currentEnemyCompo = Mappers.enemyComponent.get(enemyEntity);

		EnemyTribesmanScout type = (EnemyTribesmanScout) currentEnemyCompo.getType();
		boolean unalertedFriends = type.hasFriendsNotAlerted();
		
		List<Entity> friends = new ArrayList<>();
		for(Entity e : room.getEnemies()) {
			if (e == enemyEntity) continue;
			EnemyComponent ec = Mappers.enemyComponent.get(e);
			if (ec != null 
					&& ec.getFaction() == currentEnemyCompo.getFaction() 
					&& !(ec.getType() instanceof EnemyTribesmanShaman)
					&& (!unalertedFriends || (unalertedFriends && !ec.isAlerted()) )) {
				friends.add(e);
			}
		}
		if (friends.size() == 0) {
			// No other members of this faction
			return moveTowardPlayerStrategy(enemyEntity, room, moveComponent, enemyPos);
		} 
		
		//First find the closest target
		int shortestDistance = -1;
		Entity target = null;
		for (Entity p : friends) {
			GridPositionComponent playerPos = Mappers.gridPositionComponent.get(p);
			int distance = TileUtil.getDistanceBetweenTiles(enemyPos.coord(), playerPos.coord());
			if (target == null || distance < shortestDistance) {
				shortestDistance = distance;
				target = p;
			}
		}
		
		if (shortestDistance == 1) {
			//Already close to a friend, don't need to move.
		} else {
			if (target != null) {
				GridPositionComponent targetPos = Mappers.gridPositionComponent.get(target);
				shortestDistance = -1;
				
		    	for (Entity t : moveComponent.movableTiles) {
		    		GridPositionComponent tilePos = Mappers.gridPositionComponent.get(t);
		    		int distance = TileUtil.getDistanceBetweenTiles(targetPos.coord(), tilePos.coord());
		    		if (selectedTile == null || distance < shortestDistance) {
		    			selectedTile = t;
		    			shortestDistance = distance;
		    		}
		    		
		    		if (shortestDistance == 1) {
		    			break;
		    		}
		    	}
			} 
		}
		return selectedTile;
	}
	
	/**
	 * Move randomly if not in range of an enemy, but if in range, go the range max and attack.
	 * @param engine the engine
	 * @param moveComponent the move component of the enemy
	 * @param enemyPos the enemy position
	 * @return the selected tile. Null if no move needed
	 */
	private static Entity moveRandomlyButAttackFromRangeIfPossible(Entity enemyEntity, Room room,
			MoveComponent moveComponent, AttackComponent attackCompo, GridPositionComponent enemyPos, AttackTileSearchService atss) {
		Entity selectedTile = null;
		
		//First check if can attack the player
		Entity target = null;
		GridPositionComponent playerPos = Mappers.gridPositionComponent.get(room.floor.getGameScreen().player);
		AttackComponent attackComponent = Mappers.attackComponent.get(enemyEntity);
		for (Tile attackTile : attackComponent.allAttackableTiles) {
			if (attackTile.getGridPos().equals(playerPos.coord())) {
				target = room.floor.getGameScreen().player;
			}
		}


		if (target != null) {
			GridPositionComponent targetPos = Mappers.gridPositionComponent.get(target);
						
			int selectedDistance = 0;
			for (Entity t : moveComponent.movableTiles) {
	    		GridPositionComponent tilePos = Mappers.gridPositionComponent.get(t);
	    		int distance = TileUtil.getDistanceBetweenTiles(targetPos.coord(), tilePos.coord());
	    		if (distance >= attackCompo.getRangeMin() && distance <= attackCompo.getRangeMax() && distance > selectedDistance) {
	    			if (getOrbHeuristic(tilePos.coord(), enemyEntity, room) > 0) {
	    				continue;
	    			}
	    			
					Set<Tile> searchAttackTiles = atss.searchAttackEntitiesFromOnePosition(t, enemyEntity, room, true);
					if (!searchAttackTiles.isEmpty()) {
		    			selectedTile = t;
		    			selectedDistance = distance;

		    			if (selectedDistance == attackCompo.getRangeMax()) {
		    				break;
		    			}
					}
	    		}
	    	}

	    	
	    	if (selectedTile == null) {
	    		//No target in range, move randomly
				selectedTile = moveRandomly(enemyEntity, room,moveComponent);		
	    	}
		} else {
			//No target, move randomly
			selectedTile = moveRandomly(enemyEntity, room, moveComponent);				
		}
		return selectedTile;
	}
	
	

	/**
	 * Move randomly if not in range of an enemy, but if in range, move at close range to attack.
	 * @param engine the engine
	 * @param moveComponent the move component of the enemy
	 * @param enemyPos the enemy position
	 * @return the selected tile. Null if no move needed
	 */
	private static Entity moveRandomlyButAttackIfPossible(Entity enemyEntity, Room room,
			MoveComponent moveComponent, GridPositionComponent enemyPos) {
		Entity selectedTile = null;
		
		//First check if can attack the player
		Entity target = null;
		int shortestDistance = -1;
		GridPositionComponent playerPos = Mappers.gridPositionComponent.get(room.floor.getGameScreen().player);
		AttackComponent attackComponent = Mappers.attackComponent.get(enemyEntity);
		for (Tile attackTile : attackComponent.allAttackableTiles) {
			if (attackTile.getGridPos().equals(playerPos.coord())) {
				target = room.floor.getGameScreen().player;
				shortestDistance = TileUtil.getDistanceBetweenTiles(enemyPos.coord(), playerPos.coord());
			}
		}
		
//		int shortestDistance = -1;
//		for (Entity p : allPlayers) {
//			GridPositionComponent playerPos = Mappers.gridPositionComponent.get(p);
//			int distance = TileUtil.getDistanceBetweenTiles(enemyPos.coord(), playerPos.coord());
//			if (target == null || distance < shortestDistance) {
//				shortestDistance = distance;
//				target = p;
//			}
//		}
		
		
		if (shortestDistance == attackComponent.getRangeMax()) {
			//Already facing the player, don't need to move.
		} else {
			if (target != null) {
				GridPositionComponent targetPos = Mappers.gridPositionComponent.get(target);
				
				int currentDistance = -1;
		    	for (Entity t : moveComponent.movableTiles) {
		    		GridPositionComponent tilePos = Mappers.gridPositionComponent.get(t);
		    		int distance = TileUtil.getDistanceBetweenTiles(targetPos.coord(), tilePos.coord());
		    		if (distance >= attackComponent.getRangeMin() && distance <= attackComponent.getRangeMax()) {
		    			if (getOrbHeuristic(tilePos.coord(), enemyEntity, room) > 0) {
		    				continue;
		    			}
		    			
		    			if (distance > currentDistance) {
		    				selectedTile = t;
		    				currentDistance = distance;
		    			}
		    			if (distance == attackComponent.getRangeMax()) break;
		    		}
		    	}
		    	
		    	if (selectedTile == null) {
		    		//No target in range, move randomly
					selectedTile = moveRandomly(enemyEntity, room, moveComponent);		
		    	}
			} else {
				//No target, move randomly
				selectedTile = moveRandomly(enemyEntity, room, moveComponent);				
			}
		}
		return selectedTile;
	}
	
	
	
	private static int getOrbHeuristic(Vector2 pos, Entity enemyEntity, Room room) {
		if (Mappers.humanoidComponent.has(enemyEntity)) {
			Entity orb = TileUtil.getEntityWithComponentOnTile(pos, OrbComponent.class, room);
			if (orb != null) {
				OrbComponent orbComponent = Mappers.orbComponent.get(orb);
				if (orbComponent != null) {
					return orbComponent.getType().getHeuristic(enemyEntity);
				}
			}
		}
		return 0;
	}
}
