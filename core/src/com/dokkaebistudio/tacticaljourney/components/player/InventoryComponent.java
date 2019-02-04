package com.dokkaebistudio.tacticaljourney.components.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Means that the entity can carry items.
 * @author Callil
 *
 */
public class InventoryComponent implements Component, Poolable {
	
	/** Whether the inventory is being displayed or not. */
	private boolean inventoryDisplayed;
	
	/** Whether the inventory is being displayed because of looting or not. */
	private boolean lootInventoryDisplayed;
	/** The entity being looted. */
	private Entity lootableEntity;
	
	/** The number of slots in the inventory. */
	private int numberOfSlots;
	
	/** The slots of the inventory. */
	private Entity[] slots = new Entity[16];
	
	private int firstEmptySlot = 0;
	
	private InventoryActionEnum currentAction;
	private Entity currentItem;
	
	
	public enum InventoryActionEnum {
		DISPLAY_POPIN,
		PICKUP,
		PICKUP_AND_USE,
		USE,
		DROP;
	}
	
	
	@Override
	public void reset() {
		slots = new Entity[16];
		firstEmptySlot = 0;
		inventoryDisplayed = false;
	}
	
	

	/** Check whether there is space in the inventory. */
	public boolean canStore() {
		return firstEmptySlot < numberOfSlots;
	}
	
	/**
	 * Store the given entity in the inventory.
	 * @param e the entity to store
	 */
	public void store(Entity e, Room r) {
		slots[firstEmptySlot] = e;
		firstEmptySlot ++;
		
		if (r != null) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(e);
			gridPositionComponent.setInactive(e, r);
		}
	}
	
	/**
	 * Remove the entity from the inventory.
	 * @param e the entity to remove
	 */
	public void remove(Entity e) {
		for (int i=0 ; i<firstEmptySlot ; i++) {
			if (slots[i] == e) {
				getAndRemove(i);
			}
		}
	}
	
	/**
	 * Get and remove the entity at the given index in the inventory.
	 * @param slotIndex the index of the slot
	 * @return the entity
	 */
	public Entity getAndRemove(int slotIndex) {
		Entity e = slots[slotIndex];
		slots[slotIndex] = null;
		
		// Shift all elements
		for (int i=slotIndex ; i<firstEmptySlot ; i++) {
			if (i+1 >= numberOfSlots) {
				slots[i] = null;
			} else {
				slots[i] = slots[i+1];
			}
		}
		firstEmptySlot --;
		
		return e;
	}
	
	/**
	 * Get the entity at the given index in the inventory.
	 * @param slotIndex the index of the slot
	 * @return the entity
	 */
	public Entity get(int slotIndex) {
		return slots[slotIndex];
	}

	
	/**
	 * Set a flag to inform that we want to use the given item.
	 * @param item the item to use
	 */
	public void requestAction(InventoryActionEnum action, Entity item) {
		this.currentAction = action;
		this.currentItem = item;
	}
	
	public void clearCurrentAction() {
		this.currentAction = null;
		this.currentItem = null;
	}
	
	
	// Getters and Setters !
	
	public int getNumberOfSlots() {
		return numberOfSlots;
	}

	public void setNumberOfSlots(int numberOfSlots) {
		this.numberOfSlots = numberOfSlots;
	}



	public boolean isInventoryDisplayed() {
		return inventoryDisplayed;
	}



	public void setInventoryDisplayed(boolean inventoryDisplayed) {
		this.inventoryDisplayed = inventoryDisplayed;
	}



	public InventoryActionEnum getCurrentAction() {
		return currentAction;
	}



	public void setCurrentAction(InventoryActionEnum currentAction) {
		this.currentAction = currentAction;
	}



	public Entity getCurrentItem() {
		return currentItem;
	}



	public void setCurrentItem(Entity currentItem) {
		this.currentItem = currentItem;
	}



	public boolean isLootInventoryDisplayed() {
		return lootInventoryDisplayed;
	}



	public void setLootInventoryDisplayed(boolean lootInventoryDisplayed) {
		this.lootInventoryDisplayed = lootInventoryDisplayed;
	}



	public Entity getLootableEntity() {
		return lootableEntity;
	}



	public void setLootableEntity(Entity lootableEntity) {
		this.lootableEntity = lootableEntity;
	}



}
