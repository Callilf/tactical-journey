package com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.creatures.subsystems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.creatures.CreatureSystem;
import com.dokkaebistudio.tacticaljourney.room.Room;

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
