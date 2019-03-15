package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;

/**
 * Marker to indicate that this entity can be inspected.
 * @author Callil
 *
 */
public class InspectableComponent implements Component {

	private String title;
	private String description;
	
	
	
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
	
}
