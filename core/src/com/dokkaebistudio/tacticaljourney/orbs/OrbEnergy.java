/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.orbs;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * @author Callil
 *
 */
public class OrbEnergy extends Orb {

	public OrbEnergy() {
		super(Descriptions.ORB_ENERGY_TITLE);
	}

	@Override
	public boolean effectOnContact(Entity user, Entity orb, Entity target, Room room) {

		// Deals damage
		HealthComponent healthComponent = Mappers.healthComponent.get(target);
		if (healthComponent != null) {
			Journal.addEntry("[BLUE]Energy orb has been activated");
			healthComponent.hit(10, target, user);
		}
		
		return true;
	}
	
	public int getHeuristic(Entity mover) {
		return 1;
	}
	
}
