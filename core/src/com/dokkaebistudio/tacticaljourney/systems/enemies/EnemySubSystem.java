package com.dokkaebistudio.tacticaljourney.systems.enemies;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.EnemySystem;

public abstract class EnemySubSystem {

	/**
	 * 
	 * @return true if the enemy's actions were handled in the sub system.
	 */
	public abstract boolean update(EnemySystem enemySystem, Entity enemy, Room room);
	
	public boolean computeMovableTilesToDisplayToPlayer(EnemySystem system, Entity enemyEntity, Room room) {
		return false;
	}
}
