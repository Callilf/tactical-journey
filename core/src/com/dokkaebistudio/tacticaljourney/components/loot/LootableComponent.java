package com.dokkaebistudio.tacticaljourney.components.loot;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.enums.LootableEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPoolSingleton;
import com.dokkaebistudio.tacticaljourney.items.pools.lootables.LootableItemPool;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Indicate that this entity can be looted by the player.
 * @author Callil
 *
 */
public class LootableComponent implements Component, Poolable {
	
	/** The type of lootable. */
	private LootableEnum type;
	
	/** The item pool from where the random items are chosen. */
	private LootableItemPool itemPool;
	
	/** The minimum number of items in this lootable. */
	private int minNumberOfItems;
	
	/** The maximum number of items in this lootable. */
	private int maxNumberOfItems;
	
	/** The loot. */
	private List<Entity> items = new ArrayList<>();
	
	/** Items that could not be fully picked up (used for the take all action). */
	private List<Entity> standByItems = new ArrayList<>();
	private List<Entity> allItems = new ArrayList<>();


	private LootableStateEnum lootableState = LootableStateEnum.CLOSED;
	
	public enum LootableStateEnum {
		CLOSED,
		OPENED;
	}
	
	
	@Override
	public void reset() {
		items.clear();
		lootableState = LootableStateEnum.CLOSED;
	}
	
	/**
	 * Get the number of turns to open the lootable.
	 * @return the number of turns
	 */
	public int getNbTurnsToOpen() {
		if (lootableState == LootableStateEnum.CLOSED) {
			return type.getNbTurnsToOpen();
		} else {
			return 0;
		}
	}
	
	
	public List<Entity> getAllItems() {
		allItems.clear();
		for (Entity item : standByItems) {
			allItems.add(item);
		}
		for (Entity item : items) {
			allItems.add(item);
		}
		return allItems;
	}

	
	public void finishTakeAll() {
		for (Entity e : this.standByItems) {
			this.items.add(e);
		}
		this.standByItems.clear();
	}
	
	
	// Getters and Setters
	
	public List<Entity> getItems() {
		return items;
	}

	public void setItems(List<Entity> items) {
		this.items = items;
	}

	public LootableEnum getType() {
		return type;
	}

	public void setType(LootableEnum type) {
		this.type = type;
	}


	public LootableStateEnum getLootableState() {
		return lootableState;
	}


	public void setLootableState(LootableStateEnum lootableState, Entity lootable) {
		this.lootableState = lootableState;
		
		if (lootable != null) {
			RegionDescriptor newRegion = lootableState == LootableStateEnum.CLOSED ? type.getClosedTexture() : type.getOpenedTexture();
			Mappers.spriteComponent.get(lootable).updateSprite(newRegion);
		}
	}

	public List<Entity> getStandByItems() {
		return standByItems;
	}

	public void setStandByItems(List<Entity> standByItems) {
		this.standByItems = standByItems;
	}

	public LootableItemPool getItemPool() {
		return itemPool;
	}

	public void setItemPool(LootableItemPool itemPool) {
		this.itemPool = itemPool;
	}

	public int getMaxNumberOfItems() {
		return maxNumberOfItems;
	}

	public void setMaxNumberOfItems(int maxNumberOfItems) {
		this.maxNumberOfItems = maxNumberOfItems;
	}

	public int getMinNumberOfItems() {
		return minNumberOfItems;
	}

	public void setMinNumberOfItems(int minNumberOfItems) {
		this.minNumberOfItems = minNumberOfItems;
	}

	
	
	
	public static Serializer<LootableComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<LootableComponent>() {

			@Override
			public void write(Kryo kryo, Output output, LootableComponent object) {
				output.writeString(object.type.name());
				output.writeInt(object.minNumberOfItems);
				output.writeInt(object.maxNumberOfItems);
				kryo.writeClassAndObject(output, object.items);
				output.writeString(object.lootableState.name());
				output.writeString(object.itemPool.id);				

			}

			@Override
			public LootableComponent read(Kryo kryo, Input input, Class<LootableComponent> type) {
				LootableComponent compo = engine.createComponent(LootableComponent.class);

				compo.type = LootableEnum.valueOf(input.readString()); 
				compo.minNumberOfItems = input.readInt(); 
				compo.maxNumberOfItems = input.readInt(); 
				compo.items = (List<Entity>) kryo.readClassAndObject(input);
				compo.lootableState = LootableStateEnum.valueOf(input.readString()); 
				compo.itemPool = (LootableItemPool) ItemPoolSingleton.getInstance().getPoolById(input.readString());
				
				return compo;
			}
		
		};
	}
	
}
