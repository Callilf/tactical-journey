package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Marker to indicate that this entity is flammable.
 * @author Callil
 *
 */
public class FlammableComponent implements Component, Poolable {
	
	/** Whether this entity can ignite due to propagation. */
	private boolean propagate;
	
	/** Whether this entity is destroyed when ignited. */
	private boolean destroyed;


	@Override
	public void reset() {
		propagate = false;	
		destroyed = false;
	}
	
	
	//************************
	// Getters and Setters
	
	public boolean isPropagate() {
		return propagate;
	}

	public void setPropagate(boolean propagate) {
		this.propagate = propagate;
	}


	public boolean isDestroyed() {
		return destroyed;
	}


	public void setDestroyed(boolean destroy) {
		this.destroyed = destroy;
	}
	
	
	
}
