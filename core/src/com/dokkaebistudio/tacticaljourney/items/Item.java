/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.rendering.HUDRenderer;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * @author Callil
 *
 */
public abstract class Item {
	
	
	/** The name displayed. */
	private String label;
	/** The name of the image in the assets. */
	private AtlasRegion texture;
	/** Whether this item is picked up automatically while walking on its tile. */
	private boolean instantPickUp;
	/** Whether this item can go into the inventory. */
	private boolean goIntoInventory;
	
	private Integer randomValueMin;
	private Integer randomValueMax;	
	
	/**
	 * Constructor for basic items without random values
	 * @param label
	 * @param imageName
	 * @param instaPickUp
	 */
	Item(String label, AtlasRegion texture, boolean instaPickUp, boolean goIntoInventory) {
		this.setLabel(label);
		this.setTexture(texture);
		this.setInstantPickUp(instaPickUp);
		this.setGoIntoInventory(goIntoInventory);
	}
	
	/**
	 * Constructor for items with random values.
	 * @param label
	 * @param imageName
	 * @param instaPickUp
	 * @param valMin
	 * @param valMax
	 */
	Item(String label, AtlasRegion texture, boolean instaPickUp, boolean goIntoInventory, Integer valMin, Integer valMax) {
		this(label, texture, instaPickUp, goIntoInventory);
		this.setRandomValueMin(valMin);
		this.setRandomValueMax(valMax);
	}
	
	
	// Abstract methods
	
	/** Called when the item is picked up. */
	public boolean pickUp(Entity picker, Entity item, Room room) {
		ItemComponent itemComponent = Mappers.itemComponent.get(item);
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(picker);
		if (inventoryComponent != null) {
			if (!inventoryComponent.canStore(itemComponent)) return false;
			
			boolean stored = inventoryComponent.store(item, itemComponent, room);
			if (stored && !this.goIntoInventory) {
				room.removeEntity(item);
			}
			return stored;
		}

		return false;
	}
	
	/** Called when the item is used. */
	public abstract boolean use(Entity user, Entity item, Room room);
	
	/** Called when the item is dropped from an entity or the inventory. */
	public boolean drop(Entity dropper, Entity item, Room room) {
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(dropper);
		if (inventoryComponent != null) {
			inventoryComponent.remove(item);
		}
		
		GridPositionComponent playerPosCompo = Mappers.gridPositionComponent.get(dropper);
		GridPositionComponent itemPosCompo = Mappers.gridPositionComponent.get(item);
		itemPosCompo.coord().set(playerPosCompo.coord());
		itemPosCompo.setActive(item, room);
		return true;
	}
	
	/** Throw the item at the desired location. */
	public void onThrow(Vector2 thrownPosition, Entity thrower, Entity item, Room room) {
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(thrower);
		if (inventoryComponent != null) {
			inventoryComponent.remove(item);
		}
		
		GridPositionComponent itemPosCompo = Mappers.gridPositionComponent.get(item);
		itemPosCompo.coord().set(thrownPosition);
		itemPosCompo.setActive(item, room);
	}

	/** Return the description of the item. */
	public abstract String getDescription();
	
	/** Return the label on the action button for the item. */
	public abstract String getActionLabel();
	
	/** Return the location of the HUD element where the item is moved when picked up.
	 * Default is the inventory, but for money or ammos it's different.
	 * @return the location
	 */
	public Vector2 getPickupImageMoveDestination() {
		return HUDRenderer.POS_INVENTORY;
	}
	
	
	
	// Getters and Setters

	public boolean isInstantPickUp() {
		return instantPickUp;
	}


	public void setInstantPickUp(boolean instantPickUp) {
		this.instantPickUp = instantPickUp;
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public AtlasRegion getTexture() {
		return texture;
	}


	public void setTexture(AtlasRegion texture) {
		this.texture = texture;
	}

	
	public boolean isGoIntoInventory() {
		return goIntoInventory;
	}

	public void setGoIntoInventory(boolean goIntoInventory) {
		this.goIntoInventory = goIntoInventory;
	}

	public Integer getRandomValueMin() {
		return randomValueMin;
	}

	public void setRandomValueMin(Integer randomValueMin) {
		this.randomValueMin = randomValueMin;
	}

	public Integer getRandomValueMax() {
		return randomValueMax;
	}

	public void setRandomValueMax(Integer randomValueMax) {
		this.randomValueMax = randomValueMax;
	}
	
}
