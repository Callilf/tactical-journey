package com.dokkaebistudio.tacticaljourney.components.orbs;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.orbs.Orb;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * Marker to indicate that this entity is an orb that will have a special effect when on contact
 * with an enemy.
 * @author Callil
 *
 */
public class OrbComponent implements Component, Poolable {

	/** The type of orb. */
	private Orb type;
	
	/** The parent entity. */
	private Entity parent;
		
	
	
	@Override
	public void reset() {
		this.setType(null);
	}
	
	
	
	
	//******************
	// Events
	
	public void onContact(Entity orb, Entity target, Room room) {
		this.type.onContact(this.parent, orb, target, room);
	}

	public void onContactWithAnotherOrb(Entity orb, Entity targetedOrb, Room room) {
		this.type.onContactWithAnotherOrb(this.parent, orb, targetedOrb, room);
	}

	
	//***********************
	// Getters and Setters
	
	public Orb getType() {
		return type;
	}


	public void setType(Orb type) {
		this.type = type;
	}


	public Entity getParent() {
		return parent;
	}


	public void setParent(Entity parent) {
		this.parent = parent;
	}

}
