/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.ces.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.wheel.AttackWheel;
import com.dokkaebistudio.tacticaljourney.wheel.Sector;

/**
 * A state alteration which can be positive or negative.
 * @author Callil
 *
 */
public abstract class Alteration {
	
	/** The id of the entity representing the item linked to this alteration. 
	 * Null if the alteration has been infused. */
	private Integer itemEntityId;
	
	private boolean infused = true;
	
	public abstract String title();
	public abstract String description();
	public abstract RegionDescriptor texture();
	
	public Integer getCurrentProcChance(Entity user) { return null; }
	
	
	
	// Receive / remove

	/** Called when this alteration is received by an entity. */
	public void onReceive(Entity entity) {};
	
	/** Called when this alteration is removed from an entity. */
	public void onRemove(Entity entity) {};
	
	
	// Turns
	
	public void onPlayerTurnStarts(Entity entity, Room room) {};
	public void onPlayerTurnEnds(Entity entity, Room room) {};
	
	
	// Combat
	
	/**
	 * Called when the blessed entity attacks.
	 * @param attacker the blessed entity
	 * @param target the target
	 * @param room the room
	 */
	public void onAttack(Entity attacker, Entity target, Sector sector, AttackComponent attackCompo, Room room) {};
	
	/**
	 * Called when the blessed entity attacks an empty tile.
	 * @param attacker the blessed entity
	 * @param tile the tile attacked
	 * @param room the room
	 */
	public void onAttackEmptyTile(Entity attacker, Tile tile, AttackComponent attackCompo, Room room) {};
	
	/**
	 * Called when the blessed entity kills a target.
	 * @param attacker the blessed entity
	 * @param target the target
	 * @param room the room
	 */
	public void onKill(Entity attacker, Entity target, Room room) {};
	
	/** Called when the entity receives an attack, before the damages are applied. 
	 * @return true if the attack is really received, false if the attack has to abort */
	public boolean onReceiveAttack(Entity user, Entity attacker, Room room) { return true; };
	
	/** Called when receiving damages. */
	public void onReceiveDamage(Entity user, Entity attacker, Room room) {};
	
	public void onDeath(Entity user, Entity attacker, Room room) {};
	
	
	// Room and floor
	
	
	/** Called when the player enters a room for the first time. */
	public void onRoomVisited(Entity entity, Room room) {};
	
	/** Called when a room has been cleared. */
	public void onRoomCleared(Entity entity, Room room) {};
	
	/** Called when the player enters a floor for the first time. */
	public void onFloorVisited(Entity entity, Floor floor, Room room) {};
	
	

	
	// Misc
	
	public void onArriveOnTile(Vector2 gridPos, Entity mover, Room room) {}
	
	public boolean onReceiveStatusEffect(Entity entity, Status status, Room room) { return true; }
	
	public void onRemoveStatusEffect(Entity entity, Status status, Room room) {}

	
	public void onModifyWheelSectors(AttackWheel wheel, Entity entity, Room room) {}
	
	/** Called when computing the number of items the shop should sell. */
	public int onShopNumberOfItems(Entity entity, Entity shopkeeper, Room room) { return 0; };
	
	/** Called when the level up popin pops up. */
	public void onLevelUp(Entity entity, Room room) {}
	
	/**
	 * Called when an item is picked up. If the return value is negative, the item cannot be picked up.
	 * @param picker the picker
	 * @param item the item to pick
	 * @param room the room
	 * @return -1 : cannot be picked. 0 or >0 can be picked
	 */
	public int onPickupItem(Entity picker, Entity item, Room room) { return 0; }
	/**
	 * Called when an item is used. If the return value is negative, the item cannot be picked up.
	 * @param user the user
	 * @param item the item to use
	 * @param room the room
	 * @return -1 : cannot be used. 0 or >0 can be used
	 */
	public int onUseItem(Entity user, Entity item, Room room) { return 0; }

	
	
	// getters and setters
	
	public boolean isInfused() {
		return infused;
	}
	public void setInfused(boolean infused) {
		this.infused = infused;
		if (this.infused) {
			itemEntityId = null;
		}
	}
	
	public Integer getItemEntityId() {
		return itemEntityId;
	}
	public void setItemEntityId(Integer itemEntityId) {
		this.itemEntityId = itemEntityId;
		if (itemEntityId != null) {
			this.infused = false;
		}
	}

	
	
}
