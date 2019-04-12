package com.dokkaebistudio.tacticaljourney.components.loot;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPoolSingleton;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity that drop loot.
 * @author Callil
 *
 */
public class LootRewardComponent implements Component, Poolable {

	/** The item to drop. Null if no drop. */
	private Entity latestItem;

	/** The drop rate in percent for the given entity. */
	private DropRate dropRate;
	
	/** The pool of items that can drop. */
	private ItemPool itemPool;
	
	/** The random used to compute the drop item. */
	private RandomXS128 dropSeededRandom;

	
	
	@Override
	public void reset() {
		latestItem = null;
		dropRate.reset();
		itemPool = null;
		dropSeededRandom = null;
	}
	
	
	// Getters and Setters
	
	public Entity getLatestItem() {
		return latestItem;
	}

	public void setLatestItem(Entity latestItem) {
		this.latestItem = latestItem;
	}

	public DropRate getDropRate() {
		return dropRate;
	}


	public void setDropRate(DropRate dropRate) {
		this.dropRate = dropRate;
	}


	public ItemPool getItemPool() {
		return itemPool;
	}


	public void setItemPool(ItemPool itemPool) {
		this.itemPool = itemPool;
	}
	
	
	public RandomXS128 getDropSeededRandom() {
		return dropSeededRandom;
	}


	public void setDropSeededRandom(RandomXS128 dropSeededRandom) {
		this.dropSeededRandom = dropSeededRandom;
	}
	
	
	public static Serializer<LootRewardComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<LootRewardComponent>() {

			@Override
			public void write(Kryo kryo, Output output, LootRewardComponent object) {
				kryo.writeClassAndObject(output, object.latestItem);
				kryo.writeClassAndObject(output, object.dropRate);
				output.writeString(object.itemPool.id);				
				
				// Save the state of the random
				long seed0 = object.dropSeededRandom.getState(0);
				long seed1 = object.dropSeededRandom.getState(1);
				output.writeString(seed0 + "#" + seed1);
			}

			@Override
			public LootRewardComponent read(Kryo kryo, Input input, Class<LootRewardComponent> type) {
				LootRewardComponent compo = engine.createComponent(LootRewardComponent.class);
				compo.latestItem = (Entity) kryo.readClassAndObject(input);
				if (compo.latestItem != null) {
					engine.addEntity(compo.latestItem);
				}

				compo.dropRate = (DropRate) kryo.readClassAndObject(input);
				compo.itemPool = (ItemPool) ItemPoolSingleton.getInstance().getPoolById(input.readString());
				
				// Read the random state
				String randomState = input.readString();
				String[] split = randomState.split("#");
				compo.dropSeededRandom = new RandomXS128();
				compo.dropSeededRandom.setState(Long.valueOf(split[0]), Long.valueOf(split[1]));
				return compo;
			}
		
		};
	}
}
