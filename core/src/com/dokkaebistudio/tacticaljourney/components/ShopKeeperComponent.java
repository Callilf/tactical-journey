package com.dokkaebistudio.tacticaljourney.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;
import com.dokkaebistudio.tacticaljourney.items.pools.shops.ShopItemPool;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Marker to indicate that this entity is a shopkeeper.
 * @author Callil
 *
 */
public class ShopKeeperComponent implements Component, Poolable {
	
	private Vector2[] itemPositions = {new Vector2(9, 5), new Vector2(11, 5), new Vector2(13, 5), 
			new Vector2(9, 7), new Vector2(13, 7), 
			new Vector2(7, 5), new Vector2(7, 7), 
			new Vector2(15, 5), new Vector2(15, 7)};

	/** Whether the shop keeper has become hostile to the player. */
	private boolean hostile;
	
	/** Whether the shop keeper is talking. */
	private boolean talking;
	
	/** The number of items for sale. Default is 3. */
	private int numberOfItems = 3;
	
	/** The items the shop keeper is selling. */
//	private List<Entity> soldItems = new ArrayList<>();
	private Map<Entity, Vector2> soldItems = new HashMap<>();
	
	/** The number of times the shop was restocked. */
	private int restockNumber = 0;
	
	/** Whether the shop has been requested for a restock. */
	private boolean requestRestock = false;
	
	/** The item pool. */
	private ShopItemPool itemPool;
	
	
	// Speeches 
	private boolean firstSpeech = true;
	
	/** The different sentences the shop keeper can say when being talked to. */
	private List<String> mainSpeeches = new ArrayList<>();
	
	
	@Override
	public void reset() {
		this.hostile = false;	
		this.restockNumber = 0;
		this.numberOfItems = 3;
		this.requestRestock = false;
		this.firstSpeech = true;
		this.soldItems.clear();
	}
	
	/**
	 * Whether the shop keeper has already sold at least one item.
	 * @return true if at leasto ne item sold, false otherwise.
	 */
	public boolean hasSoldItems() {
		return soldItems.size() < numberOfItems;
	}
	
	/**
	 * Get the price for restocking the shop.
	 * The price increases after each restock.
	 * @return the price for restocking
	 */
	public int getRestockPrice() {
		return 10 * (restockNumber + 1);
	}
	
	/**
	 * Fill the shop's items.
	 * @param numberOfItems the number of items
	 * @param entityFactory the entity factory
	 */
	public void stock(Room room) {
		int numberOfItemsToGenerate = numberOfItems - soldItems.size();
		
		List<PooledItemDescriptor> itemTypes = this.itemPool.getItemTypes(numberOfItemsToGenerate);

		for (int i=0 ; i<numberOfItemsToGenerate ; i++) {
			Vector2 position = null;
			for (Vector2 pos : itemPositions) {
				if (!soldItems.containsValue(pos)) {
					position = pos;
					break;
				}
			}
			
			
			PooledItemDescriptor itemType = itemTypes.get(i);
			Entity item = room.entityFactory.itemFactory.createItem(itemType.getType(), room, position);
			ItemComponent ic = Mappers.itemComponent.get(item);
			ic.setPrice(itemType.getPrice());

			soldItems.put(item, position);
		}
	}
	
	/**
	 * Restock the shop.
	 * @param numberOfItems the number of items the shop sells
	 * @param entityFactory the entity factory
	 */
	public void restock(Room room) {
		stock(room);
		this.restockNumber ++;
	}
	
	/**
	 * Whether the shop contains the given item.
	 * @param item the item to check
	 * @return true if the shop has this item
	 */
	public boolean containItem(Entity item) {
		return soldItems.containsKey(item);
	}
	
	/**
	 * Remove the given item from the shop's items for sale
	 * @param item the item to remove
	 */
	public void removeItem(Entity item) {
		soldItems.remove(item);
	}
	
	
	
	
	
	
	//**************************
	// Speech related methods
	
	public void addSpeech(String s) {
		mainSpeeches.add(s);
	}
	
	public String getSpeech() {
		if (firstSpeech) {
			firstSpeech = false;
			return mainSpeeches.get(0);
		} else {
			RandomXS128 unseededRandom = RandomSingleton.getInstance().getUnseededRandom();
			int nextInt = unseededRandom.nextInt(mainSpeeches.size());
			return mainSpeeches.get(nextInt);
		}
	}
	
	
	//*********************************
	// Getters and Setters

	public boolean isHostile() {
		return hostile;
	}

	public void setHostile(boolean hostile) {
		this.hostile = hostile;
	}

	public boolean isTalking() {
		return talking;
	}

	public void setTalking(boolean talking) {
		this.talking = talking;
	}

	public int getRestockNumber() {
		return restockNumber;
	}

	public void setRestockNumber(int restockNumber) {
		this.restockNumber = restockNumber;
	}

	public int getNumberOfItems() {
		return numberOfItems;
	}

	public void setNumberOfItems(int numberOfItems) {
		this.numberOfItems = numberOfItems;
	}

	public boolean isRequestRestock() {
		return requestRestock;
	}

	public void setRequestRestock(boolean requestRestock) {
		this.requestRestock = requestRestock;
	}

	public ShopItemPool getItemPool() {
		return itemPool;
	}

	public void setItemPool(ShopItemPool itemPool) {
		this.itemPool = itemPool;
	}

	
}
