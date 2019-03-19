package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Marker to indicate that this entity can be inspected.
 * @author Callil
 *
 */
public class InspectableComponent implements Component, Poolable {

	private String title;
	private String description;
	
	/** Whether the inspection if displayed in the big popup (for enemies or special entities). */
	private boolean bigPopup;
	
	
	
	@Override
	public void reset() {
		this.setBigPopup(false);
	}
	
	//****************************
	// Getters and Setters
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isBigPopup() {
		return bigPopup;
	}

	public void setBigPopup(boolean bigPopup) {
		this.bigPopup = bigPopup;
	} 
	
}