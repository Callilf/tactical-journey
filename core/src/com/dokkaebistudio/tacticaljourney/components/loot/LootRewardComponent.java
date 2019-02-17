package com.dokkaebistudio.tacticaljourney.components.loot;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.EnemyItemPool;

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
	
}
