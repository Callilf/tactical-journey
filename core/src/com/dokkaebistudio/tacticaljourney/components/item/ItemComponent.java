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
		boolean pickedUp = itemType.pickUp(picker, item, room);
		if (!pickedUp) {
			//TODO : display "no space in inventory"
		}
	}

	/**
	 * Pick up this item.
	 * @param picker the entity that picks it up
	 */
	public boolean use(Entity picker, Entity item, Room room) {
		return itemType.use(picker, item, room);
	}
	
	
	// Getters and Setters

	public ItemEnum getItemType() {
		return itemType;
	}

	public void setItemType(ItemEnum itemType) {
		this.itemType = itemType;
	}
}
