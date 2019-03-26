/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.rendering.HUDRenderer;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * @author Callil
 *
 */
public abstract class AbstractItem {
	
	/** The type of item. */
	public ItemEnum type;
	/** The name displayed. */
	private String label;
	/** The name of the image in the assets. */
	private RegionDescriptor texture;
	/** Whether this item is picked up automatically while walking on its tile. */
	private boolean instantPickUp;
	/** Whether this item can go into the inventory. */
	private boolean goIntoInventory;
	
	
	private Integer quantity;
	
	/**
	 * Constructor for basic items without random values
	 * @param label
	 * @param imageName
	 * @param instaPickUp
	 */
	protected AbstractItem(String label, RegionDescriptor texture, boolean instaPickUp, boolean goIntoInventory) {
		this.setLabel(label);
		this.setTexture(texture);
		this.setInstantPickUp(instaPickUp);
		this.setGoIntoInventory(goIntoInventory);
	}
	
	/**
	 * Constructor for basic items without random values
	 * @param label
	 * @param imageName
	 * @param instaPickUp
	 */
	protected AbstractItem(ItemEnum itemType, RegionDescriptor texture, boolean instaPickUp, boolean goIntoInventory) {
		this.type = itemType;
		this.setLabel(itemType.getName());
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
	protected AbstractItem(String label, RegionDescriptor texture, boolean instaPickUp, boolean goIntoInventory, Integer valMin, Integer valMax) {
		this(label, texture, instaPickUp, goIntoInventory);
		
		RandomSingleton random = RandomSingleton.getInstance();
		int value = valMin;
		if (valMax > valMin) {
			value += random.nextSeededInt(valMax - valMin + 1);
		}
		this.quantity = value;
	}
	
	
	// Abstract methods
	
	/** Called when the item is picked up. */
	public boolean pickUp(Entity picker, Entity item, Room room) {
		ItemComponent itemComponent = Mappers.itemComponent.get(item);
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(picker);
		if (inventoryComponent != null) {
			if (!inventoryComponent.canStore(itemComponent)) {
				Journal.addEntry("[SCARLET]Impossible to pick up the " + itemComponent.getItemLabel());
				return false;
			}
			
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
		
		if (room != null) {
			GridPositionComponent dropperPosCompo = Mappers.gridPositionComponent.get(dropper);
			GridPositionComponent itemPosCompo = Mappers.gridPositionComponent.get(item);
			itemPosCompo.coord().set(dropperPosCompo.coord());
			itemPosCompo.setActive(item, room);
		}
		return true;
	}
	
	/** Called when the item is dropped from an dead entity. */
	public boolean drop(Vector2 position, Entity item, Room room) {
		GridPositionComponent itemPosCompo = Mappers.gridPositionComponent.get(item);
		itemPosCompo.coord().set(position);
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
	
	
	/** Called when the item is infused. */
	public boolean infuse(Entity player, Entity item, Room room) {
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(player);
		if (inventoryComponent != null) {
			inventoryComponent.remove(item);
		}

		room.removeEntity(item);
		return true;
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
	
	/**
	 * Whether this item can be stacked in the inventory.
	 * @return true is stackable
	 */
	public boolean isStackable() {
		return false;
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


	public RegionDescriptor getTexture() {
		return texture;
	}


	public void setTexture(RegionDescriptor texture) {
		this.texture = texture;
	}

	
	public boolean isGoIntoInventory() {
		return goIntoInventory;
	}

	public void setGoIntoInventory(boolean goIntoInventory) {
		this.goIntoInventory = goIntoInventory;
	}

	public Integer getQuantity() {
		return quantity;
	}
	
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}
