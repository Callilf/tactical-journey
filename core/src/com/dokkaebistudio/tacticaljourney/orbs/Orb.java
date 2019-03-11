/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.orbs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * @author Callil
 *
 */
public abstract class Orb {
	
	/** The type of orb. */
	public OrbEnum type;
	
	/** The name displayed. */
	private String label;
	
	/** The name of the image in the assets. */
	private Array<Sprite> texture;
	

	
	/**
	 * Constructor for basic items without random values
	 * @param label
	 * @param texture
	 */
	protected Orb(String label, Array<Sprite> texture) {
		this.setLabel(label);
		this.setTexture(texture);
	}
	
	
	// Abstract methods
	
	/** Called when the item is picked up. */
	public void pickUp(Entity picker, Entity item, Room room) {

	}
	
	/** Called when the item is used. */
	public abstract boolean onContact(Entity user, Entity orb, Entity target, Room room);
	

	//***********************
	// Getters and Setters
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Array<Sprite> getTexture() {
		return texture;
	}

	public void setTexture(Array<Sprite> texture) {
		this.texture = texture;
	}
	
}
