package com.dokkaebistudio.tacticaljourney.components.player;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.enums.InventoryDisplayModeEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Means that the entity can carry items.
 * @author Callil
 *
 */
public class InventoryComponent implements Component, Poolable {
	public final static int MAX_SLOTS = 96;
	
	/** The player. */
	public Entity player;
	
	/** The display mode of the inventory. */
	private InventoryDisplayModeEnum displayMode;
	
	/** The entity being looted (if any). */
	private Entity lootableEntity;
	private Integer turnsToWaitBeforeLooting;
	
	/** The number of slots in the inventory. */
	private int numberOfSlots;
	
	/** The slots of the inventory. Each slot contains a list of entities to
	 * handle stacked items. */
	private List<List<Entity>> slots = new ArrayList<>(96);
	
	/** Whether the player has the key to the next floor. */
	private boolean hasKey = false;
	private boolean keyJustChanged = false;


	/** The soulbender in case of an infusion. */
	private Entity soulbender;

	
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
		BUY,
		INFUSE;
	}
	
	
	public void init() {
		for (int i=0 ; i<MAX_SLOTS ; i++) {
			ArrayList<Entity> arrayList = new ArrayList<>();
			slots.add(arrayList);
		}

	}
	
	@Override
	public void reset() {
		player = null;
		numberOfSlots = 8;
//		slots = new Entity[16];
		firstEmptySlot = 0;
		hasKey = false;
		displayMode = InventoryDisplayModeEnum.NONE;
		for (List<Entity> l : slots) {
			l.clear();
		}
	}
	
	

	/** Check whether there is space in the inventory for the given item component. */
	public boolean canStore(ItemComponent itemCompo) {
		if (itemCompo != null && itemCompo.getItemType().isGoIntoInventory()) {
			
			if (itemCompo.getItemType().isStackable()) {
				// Check if there is already an item of this type
				for (int i=0 ; i<firstEmptySlot ; i++) {
					if (slots.get(i).isEmpty()) continue;
					
					Entity entity = slots.get(i).get(0);
					ItemComponent itemComponent = Mappers.itemComponent.get(entity);
					if (itemComponent != null 
							&& itemComponent.getItemType().getClass().equals(itemCompo.getItemType().getClass())) {
						// Already a similar item in inventory
						return true;
					}
				}

			}
			
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
				int index = this.indexOf(itemCompo.getItemType().getClass());
				if (index != -1) {
					// Already a similar item in inventory
					slots.get(index).add(item);
					stacked = true;
				}
			}
			
			
			if (!stacked) {
				//This item can be stored in the intentory
				slots.get(firstEmptySlot).clear();
				slots.get(firstEmptySlot).add(item);
				firstEmptySlot ++;
			}
			
			if (room != null) {
				GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(item);
				gridPositionComponent.setInactive(item, room);
			}
			
			Journal.addEntry("You picked up a " + itemCompo.getItemLabel());
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
			if (slots.get(i).isEmpty()) continue;
			
			if (slots.get(i).get(0) == e) {
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
		if (slots.get(slotIndex).isEmpty()) return null;
		
		Entity e =  slots.get(slotIndex).get(0);
		slots.get(slotIndex).remove(0);
			
		if (slots.get(slotIndex).isEmpty()) {
			
			// Shift all elements
			for (int i=slotIndex ; i<firstEmptySlot ; i++) {
				slots.get(i).clear();
				if (i + 1 < numberOfSlots) {
					slots.get(i).addAll(slots.get(i + 1));
				}
			}
			firstEmptySlot--;
		}

		return e;
	}
	
	/**
	 * Get the entity at the given index in the inventory.
	 * @param slotIndex the index of the slot
	 * @return the entity
	 */
	public Entity get(int slotIndex) {
		if (slots.size() <= slotIndex) return null;
		if (slots.get(slotIndex).isEmpty()) return null;
		return slots.get(slotIndex).get(0);
	}
	
	/**
	 * Get the number of entities at the given index in the inventory.
	 * @param slotIndex the index of the slot
	 * @return the number of entities. 0 if no entities.
	 */
	public int getQuantity(int slotIndex) {
		return slots.get(slotIndex).size();
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
	
	
	/**
	 * Check whether an item of the given class is in the inventory.
	 * @param itemClass the item class
	 * @return true of an item of this class is in the inventory
	 */
	public boolean contains(Class itemClass) {
		return indexOf(itemClass) >= 0;
	}
	
	/**
	 * Return the slot containing an item of the given class, or -1 if no
	 * item of this class.
	 * @param itemClass the item class
	 * @return the slot index or -1.
	 */
	public int indexOf(Class itemClass) {
		for (int i=0 ; i<firstEmptySlot ; i++) {
			if (slots.get(i).isEmpty()) continue;
			
			Entity entity = slots.get(i).get(0);
			ItemComponent itemComponent = Mappers.itemComponent.get(entity);
			if (itemComponent != null 
					&& itemComponent.getItemType().getClass().equals(itemClass)) {
				// Already a similar item in inventory
				return i;
			}
		}
		return -1;
	}
	
	
	public void addSlot() {
		this.numberOfSlots++;
	}
	
	public void removeSlot(Room room) {
		if (numberOfSlots == 0) return;
		
		// Check if the slot is filled, if so, drop the content on the floor
		if (slots.size() >= this.numberOfSlots) {
			List<Entity> list = slots.get(this.numberOfSlots - 1);
				if (list != null && !list.isEmpty()) {
				List<Entity> clonedList = new ArrayList<>(list);
				for (Entity currentItem : clonedList) {
					ItemComponent itemComponent = Mappers.itemComponent.get(currentItem);
					itemComponent.drop(player, currentItem, room);
					room.getAddedItems().add(currentItem);
				}
			}
		}
		
		// Reduce the number of slots
		this.numberOfSlots--;
	}
	
	
	//*************************************
	// Getters and Setters !
	
	public int getNumberOfSlots() {
		return Math.min(numberOfSlots, MAX_SLOTS);
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

	public boolean hasKey() {
		return hasKey;
	}

	public void getKey() {
		this.hasKey = true;
		this.keyJustChanged = true;
	}
	public void removeKey() {
		this.hasKey = false;
		this.keyJustChanged = true;
	}

	public boolean hasKeyChanged() {
		return this.keyJustChanged;
	}

	public Entity getSoulbender() {
		return soulbender;
	}

	public void setSoulbender(Entity soulbender) {
		this.soulbender = soulbender;
	}
	
	
	
	
	
	public static Serializer<InventoryComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<InventoryComponent>() {

			@Override
			public void write(Kryo kryo, Output output, InventoryComponent object) {

				output.writeInt(object.numberOfSlots);
				kryo.writeClassAndObject(output, object.slots);
				output.writeBoolean(object.hasKey);
				output.writeInt(object.firstEmptySlot);

			}

			@Override
			public InventoryComponent read(Kryo kryo, Input input, Class<InventoryComponent> type) {
				InventoryComponent compo = engine.createComponent(InventoryComponent.class);

				compo.numberOfSlots = input.readInt();
				compo.slots = (List<List<Entity>>) kryo.readClassAndObject(input);
				compo.hasKey = input.readBoolean();
				compo.firstEmptySlot = input.readInt();
				
				compo.setDisplayMode(InventoryDisplayModeEnum.NONE);
				return compo;
			}
		
		};
	}
}
