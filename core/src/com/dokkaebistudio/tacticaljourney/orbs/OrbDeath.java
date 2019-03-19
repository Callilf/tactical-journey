/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.orbs;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.components.orbs.OrbCarrierComponent;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffDeathDoor;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * @author Callil
 *
 */
public class OrbDeath extends Orb {


	public OrbDeath() {
		super("Death orb", Assets.death_orb);
	}

	@Override
	public boolean onContact(Entity user, Entity orb, Entity target, Room room) {

		// Entangle
		StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(target);
		if (statusReceiverComponent != null) {
			Journal.addEntry("[BLACK]Death orb has been activated");
			statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffDeathDoor(10));
		}

		if (user != null) {
			OrbCarrierComponent orbCarrierComponent = Mappers.orbCarrierComponent.get(user);
			orbCarrierComponent.clearOrb(orb);
		}
		
		room.removeEntity(orb);
		
		return true;
	}
	
	public int getHeuristic(Entity mover) {
		return 100;
	}
}
