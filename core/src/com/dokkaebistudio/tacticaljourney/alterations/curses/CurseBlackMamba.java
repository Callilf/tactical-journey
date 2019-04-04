/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.curses;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffPoison;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.wheel.Sector;
import com.dokkaebistudio.tacticaljourney.wheel.Sector.Hit;

/**
 * Curse of the black mamba. Receive the poison status effect on any missed attack.
 * @author Callil
 *
 */
public class CurseBlackMamba extends Curse {

	private final int chanceToProc = 75;
	
	@Override
	public String title() {
		return "Curse of the black mamba";
	}
	
	@Override
	public String description() {
		return "On missed attack, high chance of receiving the [PURPLE]poison[] status effect";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.curse_black_mamba;
	}

	@Override
	public void onAttack(Entity attacker, Entity target, Sector sector, AttackComponent attackCompo, Room room) {
		if (sector != null && sector.hit == Hit.MISS) {
			int randomValue = RandomSingleton.getInstance().getUnseededRandom().nextInt(100);
			if (randomValue < chanceToProc) {
				StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(attacker);
				if (statusReceiverComponent != null) {
					Journal.addEntry("Curse of the black mamba [PURPLE]poisoned[] you");
					statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffPoison(5));
				}
			}
		}
	}

}
