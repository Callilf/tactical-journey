package com.dokkaebistudio.tacticaljourney.ai.enemies;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.enemies.enums.EnemyMoveStrategy;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class EnemyActionSelector {
	
	/**
	 * Select the tile to move on.
	 * @param enemyEntity the enemy entity
	 * @param engine the pooled engine
	 * @return the tile to move on, null if no move is needed
	 */
	public static Entity selectTileToMove(Entity enemyEntity, PooledEngine engine) {
    	Entity selectedTile = null;
    	MoveComponent moveComponent = Mappers.moveComponent.get(enemyEntity);
    	GridPositionComponent enemyPos = Mappers.gridPositionComponent.get(enemyEntity);
    	EnemyComponent enemyComponent = Mappers.enemyComponent.get(enemyEntity);
    	
    	EnemyMoveStrategy moveStrategy = enemyComponent.isAlerted() ? enemyComponent.getAlertedMoveStrategy() : enemyComponent.getBasicMoveStrategy();
    	    	
    	switch (moveStrategy) {
    	case MOVE_TOWARD_PLAYER :
	    	//Strategy 1 : move toward the player(s)
	    	selectedTile = moveTowardPlayerStrategy(engine, moveComponent, enemyPos);
	    	break;
	    	
    	case MOVE_RANDOMLY :
    		selectedTile = moveRandomly(moveComponent);
    		break;
    		
    	case MOVE_RANDOMLY_BUT_ATTACK_IF_POSSIBLE:
    		selectedTile = moveRandomlyButAttackIfPossible(engine, moveComponent, enemyPos);
    		break;
    		
    	case MOVE_RANDOMLY_BUT_ATTACK_FROM_RANGE_IF_POSSIBLE:
    		AttackComponent attackCompo = Mappers.attackComponent.get(enemyEntity);
    		selectedTile = moveRandomlyButAttackFromRangeIfPossible(engine, moveComponent, attackCompo,enemyPos);
    		break;

    		
    	case STANDING_STILL:
    	default:
    		break;
	    	
    	}
    	
    	return selectedTile;
	}

	private static Entity moveRandomly(MoveComponent moveComponent) {
		Entity selectedTile;
		List<Entity> movableTilesList = new ArrayList<>(moveComponent.movableTiles);

		RandomXS128 random = RandomSingleton.getInstance().getSeededRandom();
		int randomIndex = random.nextInt(movableTilesList.size());
		selectedTile = movableTilesList.get(randomIndex);
		return selectedTile;
	}

	/**
	 * Select the best tile to move toward the player.
	 * @param engine the engine
	 * @param moveComponent the move component of the enemy
	 * @param enemyPos the enemy position
	 * @return the selected tile. Null if no move needed
	 */
	private static Entity moveTowardPlayerStrategy(PooledEngine engine,
			MoveComponent moveComponent, GridPositionComponent enemyPos) {
		Entity selectedTile = null;
		Family family = Family.all(PlayerComponent.class, GridPositionComponent.class).get();
		ImmutableArray<Entity> allPlayers = engine.getEntitiesFor(family);
		
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
		
		if (shortestDistance == 1) {
			//Already facing the player, don't need to move.
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
			} else {
				//No target, move randomly
				selectedTile = moveRandomly(moveComponent);				
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
	private static Entity moveRandomlyButAttackFromRangeIfPossible(PooledEngine engine,
			MoveComponent moveComponent, AttackComponent attackCompo, GridPositionComponent enemyPos) {
		Entity selectedTile = null;
		Family family = Family.all(PlayerComponent.class, GridPositionComponent.class).get();
		ImmutableArray<Entity> allPlayers = engine.getEntitiesFor(family);
		
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
		

		if (target != null) {
			GridPositionComponent targetPos = Mappers.gridPositionComponent.get(target);

			boolean playerAttackable = shortestDistance <= moveComponent.moveSpeed;			
			
			if (!playerAttackable) {
				for (Entity t : attackCompo.attackableTiles) {
		    		GridPositionComponent tilePos = Mappers.gridPositionComponent.get(t);
		    		if (tilePos.coord().equals(targetPos.coord())) {
		    			//The player can be shot at
		    			playerAttackable = true;
		    			break;
		    		}
				}
			}
						
			if (playerAttackable) {
				int selectedDistance = 0;
				for (Entity t : moveComponent.movableTiles) {
		    		GridPositionComponent tilePos = Mappers.gridPositionComponent.get(t);
		    		int distance = TileUtil.getDistanceBetweenTiles(targetPos.coord(), tilePos.coord());
		    		if (distance >= attackCompo.getRangeMin() && distance <= attackCompo.getRangeMax() && distance > selectedDistance) {
		    			selectedTile = t;
		    			selectedDistance = distance;
		    			if (selectedDistance == attackCompo.getRangeMax()) {
		    				break;
		    			}
		    		}
		    	}
			} else {
				selectedTile = moveRandomly(moveComponent);		
			}
	    	
	    	if (selectedTile == null) {
	    		//No target in range, move randomly
				selectedTile = moveRandomly(moveComponent);		
	    	}
		} else {
			//No target, move randomly
			selectedTile = moveRandomly(moveComponent);				
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
	private static Entity moveRandomlyButAttackIfPossible(PooledEngine engine,
			MoveComponent moveComponent, GridPositionComponent enemyPos) {
		Entity selectedTile = null;
		Family family = Family.all(PlayerComponent.class, GridPositionComponent.class).get();
		ImmutableArray<Entity> allPlayers = engine.getEntitiesFor(family);
		
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
		
		if (shortestDistance == 1) {
			//Already facing the player, don't need to move.
		} else {
			if (target != null) {
				GridPositionComponent targetPos = Mappers.gridPositionComponent.get(target);
		    	for (Entity t : moveComponent.movableTiles) {
		    		GridPositionComponent tilePos = Mappers.gridPositionComponent.get(t);
		    		int distance = TileUtil.getDistanceBetweenTiles(targetPos.coord(), tilePos.coord());
		    		if (distance == 1) {
		    			selectedTile = t;
		    			break;
		    		}
		    	}
		    	
		    	if (selectedTile == null) {
		    		//No target in range, move randomly
					selectedTile = moveRandomly(moveComponent);		
		    	}
			} else {
				//No target, move randomly
				selectedTile = moveRandomly(moveComponent);				
			}
		}
		return selectedTile;
	}
	
}
