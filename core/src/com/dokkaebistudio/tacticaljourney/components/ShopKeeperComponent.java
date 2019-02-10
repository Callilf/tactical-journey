package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Marker to indicate that this entity is a shopkeeper.
 * @author Callil
 *
 */
public class ShopKeeperComponent implements Component, Poolable {

	/** Whether the shop keeper has become hostile to the player. */
	private boolean hostile;
	
	/** Whether the shop keeper is talking. */
	private boolean talking;
	
	
	@Override
	public void reset() {
		this.hostile = false;	
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

	
}
