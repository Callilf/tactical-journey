/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.curses;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffDeathDoor;
import com.dokkaebistudio.tacticaljourney.systems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Curse of acceptance
 * @author Callil
 *
 */
public class CurseOfAcceptance extends Curse {

	private int chanceToProc = 25;

	@Override
	public String title() {
		return "Curse of acceptance";
	}
	
	@Override
	public String description() {
		return "On killing an enemy afflicted by the [BLACK]Death's door[] status effect, chance to receive the [BLACK]Death's door[] status effect for 3 turns.";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.curse_acceptance;
	}
	
	@Override
	public void onKill(Entity attacker, Entity target, Room room) {
		StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(target);
		if (statusReceiverComponent != null && statusReceiverComponent.hasStatus(StatusDebuffDeathDoor.class)) {
			
			float randomValue = RandomSingleton.getNextChanceWithKarma();			
			if (randomValue > 100 - chanceToProc) {
				StatusReceiverComponent attackerStatusReceiverComponent = Mappers.statusReceiverComponent.get(attacker);
				attackerStatusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffDeathDoor(3));
				
				Journal.addEntry("[PURPLE]Curse of acceptance inflicted [BLACK]Death's door[PURPLE] to you");
				AlterationSystem.addAlterationProc(this);
			}
			
		}
	}


}
