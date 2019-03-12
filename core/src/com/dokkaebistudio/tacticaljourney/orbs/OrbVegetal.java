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
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffEntangled;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * @author Callil
 *
 */
public class OrbVegetal extends Orb {

	public OrbVegetal() {
		super("Vegetal orb", Assets.vegetal_orb);
	}

	@Override
	public boolean onContact(Entity user, Entity orb, Entity target, Room room) {

		// Entangle
		StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(target);
		if (statusReceiverComponent != null) {
			Journal.addEntry("[FOREST]Vegetal orb has been activated");
			statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffEntangled(5));
		}

		if (user != null) {
			OrbCarrierComponent orbCarrierComponent = Mappers.orbCarrierComponent.get(user);
			orbCarrierComponent.clearOrb(orb);
		}
		
		room.removeEntity(orb);
		
		return true;
	}
	
}
