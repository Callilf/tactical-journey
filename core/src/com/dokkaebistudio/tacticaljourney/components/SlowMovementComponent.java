package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;

/**
 * Marker to indicate that this entity is reduces the speed of any entity that walks through it.
 * @author Callil
 *
 */
public class SlowMovementComponent implements Component {

	/**
	 * The number of movement consumed when walked through this entity.
	 */
	private int movementConsumed;
	
	
	

	public int getMovementConsumed() {
		return movementConsumed;
	}

	public void setMovementConsumed(int movementConsumed) {
		this.movementConsumed = movementConsumed;
	}
}
