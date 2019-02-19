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
	
	/** Whether this statue still has a curse to deliver when destroyed. */
	private boolean justDestroyed = false;
	

	
	@Override
	public void reset() {
		this.setHasBlessing(true);
		this.setJustDestroyed(false);
	}
	
	//*********************************
	// Getters and Setters

	public boolean isHasBlessing() {
		return hasBlessing;
	}

	public void setHasBlessing(boolean hasBlessing) {
		this.hasBlessing = hasBlessing;
	}

	public boolean wasJustDestroyed() {
		return justDestroyed;
	}

	public void setJustDestroyed(boolean justDestroyed) {
		this.justDestroyed = justDestroyed;
	}

	
}
