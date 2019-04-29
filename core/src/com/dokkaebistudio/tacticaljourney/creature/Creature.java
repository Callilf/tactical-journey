/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.creature;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * @author Callil
 *
 */
public abstract class Creature {
	
	public abstract String title();
	
	/**
	 * Called when the player enter the room for the first time.
	 * @param creature the creature entity
	 * @param room the room where this creature is
	 */
	public void onRoomVisited(Entity creature, Room room) {}

	public void onStartTurn(Entity creature, Room room) {}
	public void onEndTurn(Entity creature, Room room) {}
	
	public void onStartMovement(Entity creature) {}
	public void onEndMovement(Entity creature) {}
	
	public void onAttack(Entity creature, Entity target, Room room) {}
	public boolean onReceiveAttack(Entity creature, Entity attacker, Room room) { return true; }
	public void onReceiveDamage(int damage, Entity creature, Entity attacker, Room room) {
		Mappers.aiComponent.get(creature).setAlerted(false, creature, attacker);
	}
	
	public void onKill(Entity creature, Entity target, Room room) {}

	public void onDeath(Entity creature, Entity attacker, Room room) {}
		
	
	/**
	 * Called when the previous target is removed from the game.
	 * @param creature the creature.
	 * @param room the room
	 */
	public void onLoseTarget(Entity creature, Room room) {
		Mappers.aiComponent.get(creature).setAlerted(false, creature, null);
	}
	
	public void onAlerted(Entity creature, Entity target, Room room) {}
	
	
	public void onRoomCleared(Entity creature, Room room) {}

}
