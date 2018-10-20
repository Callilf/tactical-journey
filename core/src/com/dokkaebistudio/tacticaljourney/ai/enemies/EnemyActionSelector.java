package com.dokkaebistudio.tacticaljourney.ai.enemies;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class EnemyActionSelector {

    private static final ComponentMapper<GridPositionComponent> gridPositionM = ComponentMapper.getFor(GridPositionComponent.class);
    private static final ComponentMapper<MoveComponent> moveCompoM = ComponentMapper.getFor(MoveComponent.class);
    private static final ComponentMapper<EnemyComponent> enemyCompoM = ComponentMapper.getFor(EnemyComponent.class);

	
	/**
	 * Select the tile to move on.
	 * @param enemyEntity the enemy entity
	 * @param engine the pooled engine
	 * @return the tile to move on, null if no move is needed
	 */
	public static Entity selectTileToMove(Entity enemyEntity, PooledEngine engine) {
    	Entity selectedTile = null;
    	MoveComponent moveComponent = moveCompoM.get(enemyEntity);
    	GridPositionComponent enemyPos = gridPositionM.get(enemyEntity);
    	EnemyComponent enemyComponent = enemyCompoM.get(enemyEntity);
    	
    	    	
    	switch (enemyComponent.getMoveStrategy()) {
    	case MOVE_TOWARD_PLAYER :
	    	//Strategy 1 : move toward the player(s)
	    	selectedTile = moveTowardPlayerStrategy(engine, moveComponent, enemyPos);
	    	break;
	    	
    	case MOVE_RANDOMLY :
    		selectedTile = moveRandomly(moveComponent);
    		
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

		RandomXS128 random = RandomSingleton.getInstance().getRandom();
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
			GridPositionComponent playerPos = gridPositionM.get(p);
			int distance = TileUtil.getDistanceBetweenTiles(enemyPos.coord, playerPos.coord);
			if (target == null || distance < shortestDistance) {
				shortestDistance = distance;
				target = p;
			}
		}
		
		if (shortestDistance == 1) {
			//Already facing the player, don't need to move.
		} else {
			if (target != null) {
				GridPositionComponent targetPos = gridPositionM.get(target);
				shortestDistance = -1;
		    	for (Entity t : moveComponent.movableTiles) {
		    		GridPositionComponent tilePos = gridPositionM.get(t);
		    		int distance = TileUtil.getDistanceBetweenTiles(targetPos.coord, tilePos.coord);
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
	
}
