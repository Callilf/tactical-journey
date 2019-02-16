package com.dokkaebistudio.tacticaljourney.components.player;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.enums.InventoryDisplayModeEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Means that the entity can carry items.
 * @author Callil
 *
 */
public class InventoryComponent implements Component, Poolable {
	/** The player. */
	public Entity player;
	
	/** The display mode of the inventory. */
	private InventoryDisplayModeEnum displayMode;
	
	/** The entity being looted (if any). */
	private Entity lootableEntity;
	private Integer turnsToWaitBeforeLooting;
	
	/** The number of slots in the inventory. */
	private int numberOfSlots;
	
	/** The slots of the inventory. */
	private Entity[] slots = new Entity[16];
	private List<List<Entity>> stackedItems = new ArrayList<>();

	
	private int firstEmptySlot = 0;
	
	private InventoryActionEnum currentAction;
	private Entity currentItem;
	private boolean inventoryActionInProgress;
	private boolean needInventoryRefresh = false;
	private boolean interrupted = false;
	
	
	public enum InventoryActionEnum {
		DISPLAY_POPIN,
		PICKUP,
		PICKUP_AND_USE,
		USE,
		DROP,
		THROW,
		BUY;
	}
	
	
	public void init() {
		for (int i=0 ; i<16 ; i++) {
			ArrayList<Entity> arrayList = new ArrayList<>();
			stackedItems.add(arrayList);
		}

	}
	
	@Override
	public void reset() {
		player = null;
		slots = new Entity[16];
		firstEmptySlot = 0;
		displayMode = InventoryDisplayModeEnum.NONE;
		for (List<Entity> l : stackedItems) {
			l.clear();
		}
	}
	
	

	/** Check whether there is space in the inventory for the given item component. */
	public boolean canStore(ItemComponent itemCompo) {
		//TODO : handle stackable items here later
		if (itemCompo != null && itemCompo.getItemType().isGoIntoInventory()) {
			return firstEmptySlot < numberOfSlots;
		} else {
			return true;
		}
	}
	
	/**
	 * Store the given entity in the inventory.
	 * @param item the entity to store
	 * @return true if the item was fully stored. False not stored or a part of it remains.
	 */
	public boolean store(Entity item, ItemComponent itemCompo, Room room) {
		if (itemCompo != null && itemCompo.getItemType().isGoIntoInventory()) {
			
			boolean stacked = false;
			if (itemCompo.getItemType().isStackable()) {
				// Check if there is already an item of this type
				for (int i=0 ; i<firstEmptySlot ; i++) {
					Entity entity = slots[i];
					ItemComponent itemComponent = Mappers.itemComponent.get(entity);
					if (itemComponent != null 
							&& itemComponent.getItemType().getClass().equals(itemCompo.getItemType().getClass())) {
						// Already a similar item in inventory
						stackedItems.get(i).add(item);
						stacked = true;
					}
				}
			}
			
			
			if (!stacked) {
				//This item can be stored in the intentory
				slots[firstEmptySlot] = item;
				stackedItems.get(firstEmptySlot).clear();
				firstEmptySlot ++;
			}
			
			if (room != null) {
				GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(item);
				gridPositionComponent.setInactive(item, room);
			}
			return true;
		} else {
			//This item does not go into inventory, it's used right away
			return itemCompo.use(player, item, room);
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
		Entity e = null;
		
		if (!stackedItems.get(slotIndex).isEmpty()) {
			// Stacked item
			
			e = stackedItems.get(slotIndex).get(0);
			stackedItems.get(slotIndex).remove(0);
			
		} else {
			// Item not stacked
			
			e = slots[slotIndex];
			slots[slotIndex] = null;
			stackedItems.get(slotIndex).clear();
			
			// Shift all elements
			for (int i=slotIndex ; i<firstEmptySlot ; i++) {
				if (i+1 >= numberOfSlots) {
					slots[i] = null;
					stackedItems.get(i).clear();
				} else {
					slots[i] = slots[i+1];
					stackedItems.get(i).addAll(stackedItems.get(i + 1));	
				}
			}
			firstEmptySlot --;
		
		}
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
	 * Get the number of entities at the given index in the inventory.
	 * @param slotIndex the index of the slot
	 * @return the number of entities. 0 if no entities.
	 */
	public int getQuantity(int slotIndex) {
		if (stackedItems.get(slotIndex).isEmpty()) {
			return slots[slotIndex] != null ? 1 : 0;
		}
		return stackedItems.get(slotIndex).size() + 1;
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
	
	
	public void interrupt() {
		this.interrupted = true;
		this.inventoryActionInProgress = false;
		this.needInventoryRefresh = true;
	}
	
	//*************************************
	// Getters and Setters !
	
	public int getNumberOfSlots() {
		return numberOfSlots;
	}

	public void setNumberOfSlots(int numberOfSlots) {
		this.numberOfSlots = numberOfSlots;
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



	public Entity getLootableEntity() {
		return lootableEntity;
	}



	public void setLootableEntity(Entity lootableEntity) {
		this.lootableEntity = lootableEntity;
	}



	public InventoryDisplayModeEnum getDisplayMode() {
		return displayMode;
	}



	public void setDisplayMode(InventoryDisplayModeEnum displayMode) {
		this.displayMode = displayMode;
	}



	public Integer getTurnsToWaitBeforeLooting() {
		return turnsToWaitBeforeLooting;
	}



	public void setTurnsToWaitBeforeLooting(Integer turnsToWaitBeforeLooting) {
		this.turnsToWaitBeforeLooting = turnsToWaitBeforeLooting;
	}



	public boolean isInventoryActionInProgress() {
		return inventoryActionInProgress;
	}



	public void setInventoryActionInProgress(boolean inventoryActionInProgress) {
		this.inventoryActionInProgress = inventoryActionInProgress;
	}


	public boolean isNeedInventoryRefresh() {
		return needInventoryRefresh;
	}



	public void setNeedInventoryRefresh(boolean needInventoryRefresh) {
		this.needInventoryRefresh = needInventoryRefresh;
	}



	public boolean isInterrupted() {
		return interrupted;
	}



	public void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}



}
