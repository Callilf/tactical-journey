package com.dokkaebistudio.tacticaljourney.components.item;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.items.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class ItemComponent implements Component, Poolable {
		
	/** The king of item. */
	private ItemEnum itemType;
	
	/** The random value used by some items. Null if not used. */
	private Integer randomValue;
	
	/** The displayer that shows the quantity of this item (ex: quantity of arrows or bombs). */
	private Entity quantityDisplayer;
	
	/** The price of the item. Null if no price. */
	private Integer price;
	
	/** The displayer that shows the quantity of this item (ex: quantity of arrows or bombs). */
	private Entity priceDisplayer;



	@Override
	public void reset() {
		this.randomValue = null;
		this.quantityDisplayer = null;
		this.price = null;
		this.priceDisplayer = null;
	}
	
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
			int value = this.itemType.getRandomValueMin();
			if (this.itemType.getRandomValueMax() > this.itemType.getRandomValueMin()) {
				value += random.nextInt(this.itemType.getRandomValueMax() - this.itemType.getRandomValueMin());
			}
			setRandomValue(value);
		}
		return randomValue;
	}

	public void setRandomValue(Integer value) {
		this.randomValue = value;
		
		if (quantityDisplayer != null) {
			TextComponent textComponent = Mappers.textComponent.get(quantityDisplayer);
			textComponent.setText(String.valueOf(value));
		}
	}

	public Entity getQuantityDisplayer() {
		return quantityDisplayer;
	}

	public void setQuantityDisplayer(Entity quantityDisplayer) {
		this.quantityDisplayer = quantityDisplayer;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
		
		if (priceDisplayer != null) {
			TextComponent textComponent = Mappers.textComponent.get(priceDisplayer);
			textComponent.setText(String.valueOf(price));
		}
	}

	public Entity getPriceDisplayer() {
		return priceDisplayer;
	}

	public void setPriceDisplayer(Entity priceDisplayer) {
		this.priceDisplayer = priceDisplayer;
	}
}
