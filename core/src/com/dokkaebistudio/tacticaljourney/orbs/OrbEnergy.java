/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.orbs;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
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
		super("Energy orb", Assets.energy_orb);
	}

	@Override
	public boolean onContact(Entity user, Entity orb, Entity target, Room room) {

		// Deals damage
		HealthComponent healthComponent = Mappers.healthComponent.get(target);
		if (healthComponent != null) {
			Journal.addEntry("Energy orb has be activated");
			healthComponent.hit(10, target, user);
		}
		
		room.removeEntity(orb);
		
		return true;
	}
	
}
