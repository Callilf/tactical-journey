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
	public void pickUp(Entity picker, Entity item, Room room) {
		if (itemType != null) {
			itemType.pickUp(picker, item, room);
		}
		room.removeEntity(item);
	}

	
	
	// Getters and Setters

	public ItemEnum getItemType() {
		return itemType;
	}

	public void setItemType(ItemEnum itemType) {
		this.itemType = itemType;
	}
}
