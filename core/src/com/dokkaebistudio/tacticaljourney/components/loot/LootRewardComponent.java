package com.dokkaebistudio.tacticaljourney.components.loot;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.components.ExpRewardComponent;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.EnemyItemPool;
import com.dokkaebistudio.tacticaljourney.room.Floor;
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
	private Entity drop;

	/** The drop rate in percent for the given entity. */
	private DropRate dropRate;
	
	/** The pool of items that can drop. */
	private EnemyItemPool itemPool;

	
	
	@Override
	public void reset() {
		drop = null;
		dropRate.reset();
	}
	
	
	// Getters and Setters
	
	public Entity getDrop() {
		return drop;
	}

	public void setDrop(Entity drop) {
		this.drop = drop;
	}


	public DropRate getDropRate() {
		return dropRate;
	}


	public void setDropRate(DropRate dropRate) {
		this.dropRate = dropRate;
	}


	public EnemyItemPool getItemPool() {
		return itemPool;
	}


	public void setItemPool(EnemyItemPool itemPool) {
		this.itemPool = itemPool;
	}
	
	
	
	
	
	public static Serializer<LootRewardComponent> getSerializer(final PooledEngine engine, final Floor floor) {
		return new Serializer<LootRewardComponent>() {

			@Override
			public void write(Kryo kryo, Output output, LootRewardComponent object) {
				kryo.writeClassAndObject(output, object.drop);
				kryo.writeClassAndObject(output, object.dropRate);				
				//TODO item pool
				
			}

			@Override
			public LootRewardComponent read(Kryo kryo, Input input, Class<LootRewardComponent> type) {
				LootRewardComponent compo = engine.createComponent(LootRewardComponent.class);
				compo.drop = (Entity) kryo.readClassAndObject(input);
				compo.dropRate = (DropRate) kryo.readClassAndObject(input);
				//TODO item pool
				
				return compo;
			}
		
		};
	}
}
