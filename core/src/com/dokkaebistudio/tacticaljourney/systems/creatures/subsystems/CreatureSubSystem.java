package com.dokkaebistudio.tacticaljourney.systems.creatures.subsystems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.creatures.CreatureSystem;

public abstract class CreatureSubSystem {

	/**
	 * 
	 * @return true if the enemy's actions were handled in the sub system.
	 */
	public abstract boolean update(CreatureSystem enemySystem, Entity enemy, Room room);
	
	public boolean computeMovableTilesToDisplayToPlayer(CreatureSystem system, Entity enemyEntity, Room room) {
		return false;
	}
}
