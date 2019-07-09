package com.dokkaebistudio.tacticaljourney.ai.enemies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTileSearchService;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.ces.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.attack.AttackSkill;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.orbs.OrbComponent;
import com.dokkaebistudio.tacticaljourney.creature.enemies.enums.AIMoveStrategy;
import com.dokkaebistudio.tacticaljourney.creature.enemies.tribesmen.EnemyTribesmanScout;
import com.dokkaebistudio.tacticaljourney.creature.enemies.tribesmen.EnemyTribesmanShaman;
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
    	AIComponent aiComponent = Mappers.aiComponent.get(enemyEntity);
    	
    	AIMoveStrategy moveStrategy = aiComponent.isAlerted() ? aiComponent.getAlertedMoveStrategy() : aiComponent.getBasicMoveStrategy();
    	    	
    	switch (moveStrategy) {
    	case MOVE_TOWARDS_TARGET :
	    	//Strategy 1 : move toward the player(s)
	    	selectedTile = moveTowardsTargetStrategy(enemyEntity, room, moveComponent, enemyPos);
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
	private static Entity moveTowardsTargetStrategy(Entity enemyEntity, Room room,
			MoveComponent moveComponent, GridPositionComponent enemyPos) {
		Entity selectedTile = null;

		TargetInfo selectTarget = selectTarget(enemyEntity, enemyPos, room);
		int shortestDistance = selectTarget != null ? selectTarget.distance : -1;
		Entity target = selectTarget != null ? selectTarget.target : null;
		
		AttackComponent attackComponent = Mappers.attackComponent.get(enemyEntity);

		if (shortestDistance == attackComponent.getActiveSkill().getRangeMax()) {
			//Already facing the target, don't need to move.
		} else {
			if (target != null) {
				selectedTile = getSelectedTileForMaxDamage(enemyEntity, target, room);

				if (selectedTile == null) {
					// No target in range, just move without attacking
					GridPositionComponent targetPos = Mappers.gridPositionComponent.get(target);
					shortestDistance = -1;
					
			    	for (Entity t : moveComponent.movableTiles) {
			    		GridPositionComponent tilePos = Mappers.gridPositionComponent.get(t);
			    		int distance = TileUtil.getDistanceBetweenTiles(targetPos.coord(), tilePos.coord());
			    		if (selectedTile == null || distance < shortestDistance) {
			    			if (getOrbHeuristic(tilePos.coord(), enemyEntity, room) > 0) {
			    				continue;
			    			}
			    			
			    			selectedTile = t;
			    			shortestDistance = distance;
			    		}
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
		AIComponent currentAICompo = Mappers.aiComponent.get(enemyEntity);
		EnemyComponent currentEnemyCompo = Mappers.enemyComponent.get(enemyEntity);

		EnemyTribesmanScout type = (EnemyTribesmanScout) currentAICompo.getType();
		boolean unalertedFriends = type.hasFriendsNotAlerted();
		
		List<Entity> friends = new ArrayList<>();
		for(Entity e : room.getEnemies()) {
			if (e == enemyEntity) continue;
			EnemyComponent ec = Mappers.enemyComponent.get(e);
			AIComponent aic = Mappers.aiComponent.get(e);
			if (ec != null 
					&& ec.getFaction() == currentEnemyCompo.getFaction() 
					&& !(aic.getType() instanceof EnemyTribesmanShaman)
					&& (!unalertedFriends || (unalertedFriends && !aic.isAlerted()) )) {
				friends.add(e);
			}
		}
		if (friends.size() == 0) {
			// No other members of this faction
			return moveTowardsTargetStrategy(enemyEntity, room, moveComponent, enemyPos);
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
		
		TargetInfo selectTarget = selectTarget(enemyEntity, enemyPos, room);
		int shortestDistance = selectTarget != null ? selectTarget.distance : -1;
		Entity target = selectTarget != null ? selectTarget.target : null;

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
	    			
					Set<Tile> searchAttackTiles = atss.searchAttackEntitiesFromOnePosition(enemyEntity, t, attackCompo.getActiveSkill(), room, true);
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
		
		TargetInfo selectTarget = selectTarget(enemyEntity, enemyPos, room);
		int shortestDistance = selectTarget != null ? selectTarget.distance : -1;
		Entity target = selectTarget != null ? selectTarget.target : null;
		
		AttackComponent attackComponent = Mappers.attackComponent.get(enemyEntity);
		if (shortestDistance == attackComponent.getActiveSkill().getRangeMax()) {
			//Already facing the player, don't need to move.
		} else {
			if (target != null) {
				selectedTile = getSelectedTileForMaxDamage(enemyEntity, target, room);
		    	
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
			Optional<Entity> orb = TileUtil.getEntityWithComponentOnTile(pos, OrbComponent.class, room);
			if (orb.isPresent()) {
				OrbComponent orbComponent = Mappers.orbComponent.get(orb.get());
				if (orbComponent != null) {
					return orbComponent.getType().getHeuristic(enemyEntity);
				}
			}
		}
		return 0;
	}
	
	
	
	public static Entity getSelectedTileForMaxDamage(Entity attacker, Entity target, Room room) {
		Entity selectedTile = null;
		
		MoveComponent moveComponent = Mappers.moveComponent.get(attacker);
		AttackComponent attackComponent = Mappers.attackComponent.get(attacker);
		GridPositionComponent targetPos = Mappers.gridPositionComponent.get(target);
		
		int currentDistance = -1;
		for (AttackSkill as : attackComponent.getSkills()) {
			if (!as.isActive()) continue;

	    	for (Entity t : moveComponent.movableTiles) {
	    		GridPositionComponent tilePos = Mappers.gridPositionComponent.get(t);
	    		int distance = TileUtil.getDistanceBetweenTiles(targetPos.coord(), tilePos.coord());
    		
	    		if (distance >= as.getRangeMin() && distance <= as.getRangeMax()) {
	    			if (getOrbHeuristic(tilePos.coord(), attacker, room) > 0) {
	    				continue;
	    			}
	    			
	    			if (distance > currentDistance) {
	    				selectedTile = t;
	    				attackComponent.setActiveSkill(as);
	    				currentDistance = distance;
	    			}
	    			if (distance == as.getRangeMax()) break;
	    		
	    		}
	    	}
	    	
			if (selectedTile != null) break;
		}
		
    	return selectedTile;
	}
	
	public static TargetInfo selectTarget(Entity enemyEntity, GridPositionComponent enemyPos, Room room) {
		int shortestDistance = -1;
		Entity target = null;

		TargetInfo ti = null;
		AIComponent aiComponent = Mappers.aiComponent.get(enemyEntity);
		
		if (aiComponent.getTarget() != null) {
			target = aiComponent.getTarget();
			GridPositionComponent targetPos = Mappers.gridPositionComponent.get(target);
			if (targetPos == null) {
				target = null;
			} else {
				shortestDistance = TileUtil.getDistanceBetweenTiles(enemyPos.coord(), targetPos.coord());
				ti = new TargetInfo(shortestDistance, target);
			}
		}
		
		if (ti == null) {
			List<Entity> allTargets = null;
			if (Mappers.enemyComponent.has(enemyEntity)) {
				allTargets = room.getAllies();
			} else {
				allTargets = room.getEnemies();
			}
			
			//First find the closest target
			for (Entity p : allTargets) {
				GridPositionComponent playerPos = Mappers.gridPositionComponent.get(p);
				int distance = TileUtil.getDistanceBetweenTiles(enemyPos.coord(), playerPos.coord());
				if (target == null || distance < shortestDistance) {
					shortestDistance = distance;
					target = p;
				}
			}
			ti = new TargetInfo(shortestDistance, target);
		}
		
		return ti;
	}
	
	
	
	private static class TargetInfo {
		
		public int distance;
		public Entity target;
		
		public TargetInfo(int distance, Entity target) {
			this.distance = distance;
			this.target = target;
		}
	}
	
}
