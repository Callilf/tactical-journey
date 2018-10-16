package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

/**
 * Marker to indicate that this entity is a damage indicator to display on screen.
 * @author Callil
 *
 */
public class DamageDisplayComponent implements Component {
	
	private Vector2 initialPosition;
	

	public Vector2 getInitialPosition() {
		return initialPosition;
	}

	public void setInitialPosition(Vector2 initialPosition) {
		this.initialPosition = initialPosition;
	}

}
