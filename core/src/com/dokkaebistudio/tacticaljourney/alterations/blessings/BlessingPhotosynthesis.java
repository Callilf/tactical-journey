/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.ces.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffEntangled;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Blessing of the black mamba. Chance to poison the enemy.
 * @author Callil
 *
 */
public class BlessingPhotosynthesis extends Blessing {

	private int chanceToProc = 20;

	@Override
	public String title() {
		return "Photosynthesis";
	}
	
	@Override
	public String description() {
		return "On new room entrance, chance to give the [FOREST]entangled[] status effect to enemies for 10 turns.";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_photosynthesis;
	}
	
	@Override
	public Integer getCurrentProcChance(Entity user) {
		return chanceToProc;
	}

	@Override
	public void onRoomVisited(Entity entity, Room room) {
		for (Entity e : room.getEnemies()) {
			float randomValue = RandomSingleton.getNextChanceWithKarma();
			if (randomValue < getCurrentProcChance(entity)) {
				StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(e);
				statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffEntangled(10));
				
				AIComponent aiCompo = Mappers.aiComponent.get(e);
				Journal.addEntry("[FOREST]Photosynthesis entangled " + aiCompo.getType().title());
				AlterationSystem.addAlterationProc(this);
			}
			
		}
	}

}
