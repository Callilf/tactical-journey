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
	
	/** The number of slots in the inventory. */
	private int numberOfSlots;
	
	/** The slots of the inventory. */
	private Entity[] slots = new Entity[16];
	
	private int firstEmptySlot = 0;
	
	
	
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
		
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(e);
		gridPositionComponent.setInactive(e, r);
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

}
