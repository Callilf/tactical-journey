package com.dokkaebistudio.tacticaljourney.ces.components.neutrals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.ces.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.loot.DropRate;
import com.dokkaebistudio.tacticaljourney.ces.components.loot.DropRate.ItemPoolRarity;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPoolSingleton;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.LootUtil;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is a shopkeeper.
 * @author Callil
 *
 */
public class ShopKeeperComponent implements Component, Poolable {
	
	public static final int MAX_NUMBER_OF_ITEMS_FOR_SALE = 9;
	
	private Vector2[] itemPositions = {new Vector2(9, 5), new Vector2(11, 5), new Vector2(13, 5), 
			new Vector2(9, 7), new Vector2(13, 7), 
			new Vector2(7, 5), new Vector2(7, 7), 
			new Vector2(15, 5), new Vector2(15, 7)};

	/** Whether the shop keeper has become hostile to the player. */
	private boolean hostile;
	
	/** Whether the shop keeper is talking. */
	private boolean talking;
	
	/** The number of items for sale. Default is 3. */
	private int numberOfItems = 5;
	
	/** The items the shop keeper is selling. */
//	private List<Entity> soldItems = new ArrayList<>();
	private Map<Entity, Vector2> soldItems = new HashMap<>();
	
	/** The number of times the shop was restocked. */
	private int restockNumber = 0;
	
	/** Whether the shop has been requested for a restock. */
	private boolean requestRestock = false;
	
	/** The item pool. */
	private ItemPool itemPool;
	private DropRate dropRate;
	private RandomXS128 dropSeededRandom;
	
	
	@Override
	public void reset() {
		this.hostile = false;	
		this.restockNumber = 0;
		this.numberOfItems = 3;
		this.requestRestock = false;
		this.soldItems.clear();
	}
	
	public void increaseNumberOfItems(int amount) {
		this.numberOfItems += amount;
	}
	
	/**
	 * Whether the shop keeper has already sold at least one item.
	 * @return true if at leasto ne item sold, false otherwise.
	 */
	public boolean hasSoldItems() {
		return soldItems.size() < this.getNumberOfItems();
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
		int numberOfItemsToGenerate = this.getNumberOfItems() - soldItems.size();
		
		for (int i=0 ; i<numberOfItemsToGenerate ; i++) {
			Vector2 position = null;
			for (Vector2 pos : itemPositions) {
				if (!soldItems.containsValue(pos)) {
					position = pos;
					break;
				}
			}
			
			ItemPoolRarity rarity = LootUtil.getRarity(RandomSingleton.getNextChanceWithKarma(dropSeededRandom), dropRate);
			List<PooledItemDescriptor> itemTypes = this.itemPool.getItemTypes(1, rarity, dropSeededRandom);
			
			PooledItemDescriptor itemType = itemTypes.get(0);
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
		if (numberOfItems < 0) return 0;
		if (numberOfItems > MAX_NUMBER_OF_ITEMS_FOR_SALE) return MAX_NUMBER_OF_ITEMS_FOR_SALE;
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

	public ItemPool getItemPool() {
		return itemPool;
	}

	public void setItemPool(ItemPool itemPool) {
		this.itemPool = itemPool;
	}
	
	
	
	
	public static Serializer<ShopKeeperComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<ShopKeeperComponent>() {

			@Override
			public void write(Kryo kryo, Output output, ShopKeeperComponent object) {
				output.writeBoolean(object.hostile);
				output.writeInt(object.numberOfItems);
				kryo.writeClassAndObject(output, object.soldItems);
				output.writeInt(object.restockNumber);
				output.writeString(object.itemPool.id);			
				kryo.writeClassAndObject(output, object.dropRate);
				
				// Save the state of the random
				long seed0 = object.dropSeededRandom.getState(0);
				long seed1 = object.dropSeededRandom.getState(1);
				output.writeString(seed0 + "#" + seed1);

			}

			@Override
			public ShopKeeperComponent read(Kryo kryo, Input input, Class<? extends ShopKeeperComponent> type) {
				ShopKeeperComponent compo = engine.createComponent(ShopKeeperComponent.class);
				compo.hostile = input.readBoolean();
				compo.numberOfItems = input.readInt();
				compo.soldItems = (Map<Entity, Vector2>) kryo.readClassAndObject(input);
				compo.restockNumber = input.readInt();
				compo.itemPool = (ItemPool) ItemPoolSingleton.getInstance().getPoolById(input.readString());
				compo.dropRate = (DropRate) kryo.readClassAndObject(input);
				
				// Read the random state
				String randomState = input.readString();
				String[] split = randomState.split("#");
				compo.dropSeededRandom = new RandomXS128();
				compo.dropSeededRandom.setState(Long.valueOf(split[0]), Long.valueOf(split[1]));
				return compo;
			}
		
		};
	}

	public DropRate getDropRate() {
		return dropRate;
	}

	public void setDropRate(DropRate dropRate) {
		this.dropRate = dropRate;
	}

	public RandomXS128 getDropSeededRandom() {
		return dropSeededRandom;
	}

	public void setDropSeededRandom(RandomXS128 dropSeededRandom) {
		this.dropSeededRandom = dropSeededRandom;
	}

	
}
