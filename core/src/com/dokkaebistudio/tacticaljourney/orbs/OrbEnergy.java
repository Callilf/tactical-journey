/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.orbs;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.orbs.OrbCarrierComponent;
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
			Journal.addEntry("[BLUE]Energy orb has been activated");
			healthComponent.hit(10, target, user);
		}
		
		if (user != null) {
			OrbCarrierComponent orbCarrierComponent = Mappers.orbCarrierComponent.get(user);
			orbCarrierComponent.clearOrb(orb);
		}
		
		room.removeEntity(orb);
		
		return true;
	}
	
	public int getHeuristic(Entity mover) {
		return 1;
	}
	
}
