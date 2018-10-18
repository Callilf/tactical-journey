package com.dokkaebistudio.tacticaljourney.ai.enemies;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;

public class EnemyActionSelector {

	
	/**
	 * Select the tile to move on.
	 * @param enemyEntity the enemy entity
	 * @param moveComponent the MoveComponent of the enemy
	 * @return the tile to move on, null if no move is needed
	 */
	public static Entity selectTileToMove(Entity enemyEntity, MoveComponent moveComponent) {
    	Entity selectedTile = null;
    	for (Entity t : moveComponent.movableTiles) {
    		selectedTile = t;
    		break;
    	}
    	return selectedTile;
	}
	
}
