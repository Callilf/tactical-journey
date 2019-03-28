/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.orbs;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffPoison;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * @author Callil
 *
 */
public class OrbPoison extends Orb {

	public OrbPoison() {
		super(Descriptions.ORB_POISON_TITLE);
	}

	@Override
	public boolean effectOnContact(Entity user, Entity orb, Entity target, Room room) {

		// Entangle
		StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(target);
		if (statusReceiverComponent != null) {
			Journal.addEntry("[PURPLE]Poison orb has been activated");
			statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffPoison(5, user));
		}
		
		return true;
	}
	
	public int getHeuristic(Entity mover) {
		return 1;
	}
}
