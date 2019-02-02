package com.dokkaebistudio.tacticaljourney.components.item;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.items.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

public class ItemComponent implements Component {
		
	/** The king of item. */
	private ItemEnum itemType;

	
	/**
	 * Pick up this item.
	 * @param picker the entity that picks it up
	 */
	public boolean pickUp(Entity picker, Entity item, Room room) {
		return itemType.pickUp(picker, item, room);
	}

	/**
	 * Use the given item.
	 * @param user the entity that uses the item (usually the player).
	 * @param item the item to use
	 */
	public boolean use(Entity user, Entity item, Room room) {
		return itemType.use(user, item, room);
	}
	
	/**
	 * Drop the given item.
	 * @param dropper the entity that drops the item (usually the player).
	 * @param item the item to drop
	 * @param room the room in which the item is dropped
	 * @return true if the item was dropped
	 */
	public boolean drop(Entity dropper, Entity item, Room room) {
		return itemType.drop(dropper, item, room);
	}
	
	
	// Getters and Setters

	public ItemEnum getItemType() {
		return itemType;
	}

	public void setItemType(ItemEnum itemType) {
		this.itemType = itemType;
	}
}
