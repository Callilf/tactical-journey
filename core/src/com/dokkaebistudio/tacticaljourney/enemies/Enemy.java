/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.enemies;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * @author Callil
 *
 */
public abstract class Enemy {
	
	public abstract String title();
	
	/**
	 * Called when the player enter the room for the first time.
	 * @param enemy the enemy entity
	 * @param room the room where this enemy is
	 */
	public void onRoomVisited(Entity enemy, Room room) {}

	public void onStartTurn(Entity enemy, Room room) {}
	public void onEndTurn(Entity enemy, Room room) {}
	
	public void onStartMovement(Entity enemy) {}
	public void onEndMovement(Entity enemy) {}
	
	public void onAttack(Entity enemy, Entity target, Room room) {}
	public boolean onReceiveAttack(Entity enemy, Entity attacker, Room room) { return true; }
	public void onReceiveDamage(int damage, Entity enemy, Entity attacker, Room room) {}
	public void onDeath(Entity enemy, Entity attacker, Room room) {}
	
	public void onAlerted(Entity enemy, Entity player, Room room) {}
	
}
