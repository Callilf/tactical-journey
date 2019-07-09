/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.orbs;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.ces.components.orbs.OrbCarrierComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * @author Callil
 *
 */
public abstract class Orb {
	
	/** The name displayed. */
	private String label;	

	
	/**
	 * Constructor for basic items without random values
	 * @param label
	 * @param texture
	 */
	protected Orb(String label) {
		this.setLabel(label);
	}
	
	
	// Abstract methods
	
	/** Called when the item is used. */
	public boolean onContact(Entity user, Entity orb, Entity target, Room room) {
		boolean result = effectOnContact(user, orb, target, room);
				
		if (result) {
			if (user != null) {
				OrbCarrierComponent orbCarrierComponent = Mappers.orbCarrierComponent.get(user);
				orbCarrierComponent.clearOrb(orb);
			}
			
			room.removeEntity(orb);
		}
		
		return result;
	}
	
	/** Called when the item is used. */
	public abstract boolean effectOnContact(Entity user, Entity orb, Entity target, Room room);

	
	public boolean onContactWithAnotherOrb(Entity user, Entity orb, Entity targetedOrb, Room room) { return true; };

	
	/**
	 * Get the heuristic influence of walking on this orb.
	 * 0 means no influence
	 * a negative value is a good influence and the pathfinding will tend to use this tile
	 * a positive value is a bad influence and the pathfinding will tend to avoid this tile
	 * @param mover the moving entity
	 * @return the influence of this creep on the heuristic for the pathfinding.
	 */
	public int getHeuristic(Entity mover) {
		return 0;
	}
	

	//***********************
	// Getters and Setters
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
