package com.dokkaebistudio.tacticaljourney.components.item;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.items.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

public class ItemComponent implements Component {
		
	/** The king of item. */
	private ItemEnum itemType;
	
	/** The random value used by some items. Null if not used. */
	private Integer randomValue;

	
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
	
	
	//**********************************
	// Display util methods
	
	/**
	 * Get the label. Replace the # per the random number, and remove the [] depending on the singular or plural.
	 * @return the label to display
	 */
	public String getItemLabel() {
		if (this.getRandomValue() != null) {
			String label = itemType.getLabel().replace("#", String.valueOf(this.getRandomValue().intValue()));
			Integer val = this.getRandomValue();
			if (val.intValue() == 1) {
				label = label.replaceAll("\\[.*?\\]", "");
			} else {
				label = label.replaceAll("\\[", "");
				label = label.replaceAll("\\]", "");
			}
			return label;
		}
		return itemType.getLabel();
	}
	
	public String getItemDescription() {
		return itemType.getDescription();
	}
	
	public String getItemActionLabel() {
		return itemType.getActionLabel();
	}
	
	public String getItemImageName() {
		return itemType.getImageName();
	}
	
	
	
	
	// Getters and Setters

	public ItemEnum getItemType() {
		return itemType;
	}

	public void setItemType(ItemEnum itemType) {
		this.itemType = itemType;
	}

	public Integer getRandomValue() {
		if (randomValue == null && this.itemType.getRandomValueMax() != null) {
			RandomXS128 random = RandomSingleton.getInstance().getSeededRandom();
			randomValue = this.itemType.getRandomValueMin();
			if (this.itemType.getRandomValueMax() > this.itemType.getRandomValueMin()) {
				randomValue += random.nextInt(this.itemType.getRandomValueMax() - this.itemType.getRandomValueMin());
			}
		}
		return randomValue;
	}

	public void setRandomValue(Integer value) {
		this.randomValue = value;
	}
}
