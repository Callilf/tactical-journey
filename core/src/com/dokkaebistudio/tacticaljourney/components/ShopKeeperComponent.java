package com.dokkaebistudio.tacticaljourney.components;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
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
	private List<Entity> soldItems = new ArrayList<>();
	
	/** The number of times the shop was restocked. */
	private int restockNumber = 0;
	
	
	@Override
	public void reset() {
		this.hostile = false;	
		this.restockNumber = 0;
		this.numberOfItems = 3;
	}
	
	
	
	/**
	 * Fill the shop's items.
	 * @param numberOfItems the number of items
	 * @param entityFactory the entity factory
	 */
	public void stock(Room room, EntityFactory entityFactory) {
		int numberOfItemsToGenerate = numberOfItems - soldItems.size();
		
		List<PooledItemDescriptor> itemTypes = ShopItemPool.getItemTypes(numberOfItemsToGenerate);

		for (int i=0 ; i<numberOfItemsToGenerate ; i++) {
			PooledItemDescriptor itemType = itemTypes.get(i);
			Entity item = entityFactory.itemFactory.createItem(itemType.getType(), room, itemPositions[i]);
			ItemComponent ic = Mappers.itemComponent.get(item);
			ic.setPrice(itemType.getPrice());

			soldItems.add(item);
		}
	}
	
	/**
	 * Restock the shop.
	 * @param numberOfItems the number of items the shop sells
	 * @param entityFactory the entity factory
	 */
	public void restock(Room room, EntityFactory entityFactory) {
		stock(room, entityFactory);
		this.restockNumber ++;
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

	public List<Entity> getSoldItems() {
		return soldItems;
	}

	public void setSoldItems(List<Entity> soldItems) {
		this.soldItems = soldItems;
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

	
}
