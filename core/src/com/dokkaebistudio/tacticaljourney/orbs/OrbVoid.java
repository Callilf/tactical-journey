/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.orbs;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.ces.components.orbs.OrbCarrierComponent;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * @author Callil
 *
 */
public class OrbVoid extends Orb {

	public OrbVoid() {
		super(Descriptions.ORB_VOID_TITLE);
	}

	@Override
	public boolean effectOnContact(Entity user, Entity orb, Entity target, Room room) {
		return false;
	}
	
	@Override
	public boolean onContactWithAnotherOrb(Entity user, Entity orb, Entity targetedOrb, Room room) {
		Journal.addEntry("Void has consumed " + Mappers.inspectableComponent.get(targetedOrb).getTitle());
		Entity parent = Mappers.orbComponent.get(targetedOrb).getParent();
		if (parent != null) {
			// This orb is linked to an entity
			OrbCarrierComponent orbCarrierComponent = Mappers.orbCarrierComponent.get(parent);
			orbCarrierComponent.clearOrb(targetedOrb);
		} else {
			// Lonely orb
			room.removeEntity(targetedOrb);
		}

		if (user != null) {
			OrbCarrierComponent orbCarrierComponent = Mappers.orbCarrierComponent.get(user);
			orbCarrierComponent.clearOrb(orb);
		}
		
		room.removeEntity(orb);
		return true;
	}
	
	public int getHeuristic(Entity mover) {
		return 0;
	}
}
