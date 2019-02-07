package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Marker to indicate that this entity that drop loot.
 * @author Callil
 *
 */
public class LootRewardComponent implements Component, Poolable {

	/** The item to drop. Null if no drop. */
	private Entity drop;

	/** The drop rate in percent for the given entity. */
	private float dropRate;

	
	
	@Override
	public void reset() {
		drop = null;	
	}
	
	
	// Getters and Setters
	
	public Entity getDrop() {
		return drop;
	}

	public void setDrop(Entity drop) {
		this.drop = drop;
	}


	public float getDropRate() {
		return dropRate;
	}


	public void setDropRate(float dropRate) {
		this.dropRate = dropRate;
	}
	
}
