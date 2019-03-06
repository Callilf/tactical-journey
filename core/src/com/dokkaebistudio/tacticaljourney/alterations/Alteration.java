/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * A state alteration which can be positive or negative.
 * @author Callil
 *
 */
public abstract class Alteration {
	
	public abstract String title();
	public abstract String description();
	public abstract AtlasRegion texture();

	/** Called when this alteration is received by an entity. */
	public void onReceive(Entity entity) {};
	
	/** Called when this alteration is removed from an entity. */
	public void onRemove(Entity entity) {};
	
	
	/**
	 * Called when the blessed entity attacks.
	 * @param attacker the blessed entity
	 * @param target the target
	 * @param room the room
	 */
	public void onAttack(Entity attacker, Entity target, Room room) {};
	
	/**
	 * Called when the blessed entity kills a target.
	 * @param attacker the blessed entity
	 * @param target the target
	 * @param room the room
	 */
	public void onKill(Entity attacker, Entity target, Room room) {};
	
	public void onReceiveDamage(Entity user, Entity attacker, Room room) {};
	public void onDeath(Entity user, Entity attacker, Room room) {};
	
	/** Called when a room has been cleared. */
	public void onRoomCleared(Entity entity, Room room) {};
	
	
}
