package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Marker to indicate that this entity is a statue.
 * @author Callil
 *
 */
public class StatueComponent implements Component, Poolable {

	/** Whether this statue still has a blessing to offer. */
	private boolean hasBlessing = true;
	

	
	@Override
	public void reset() {
		this.setHasBlessing(true);	
	}
	
	//*********************************
	// Getters and Setters

	public boolean isHasBlessing() {
		return hasBlessing;
	}

	public void setHasBlessing(boolean hasBlessing) {
		this.hasBlessing = hasBlessing;
	}

	
}
