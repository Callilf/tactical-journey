package com.dokkaebistudio.tacticaljourney.components.display;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Marker to indicate that this entity is a damage indicator to display on screen.
 * @author Callil
 *
 */
public class DamageDisplayComponent implements Component, Poolable {
	
	private final Vector2 initialPosition = new Vector2(-100, -100);
	
	
	
	@Override
	public void reset() {
		initialPosition.set(-100,-100);		
	}

	
	//***********************
	// Getters and Setters
	
	public Vector2 getInitialPosition() {
		return initialPosition;
	}

	public void setInitialPosition(Vector2 initialPosition) {
		this.initialPosition.set(initialPosition);
	}

}
