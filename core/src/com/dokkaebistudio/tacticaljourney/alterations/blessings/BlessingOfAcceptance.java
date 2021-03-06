/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.ces.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.ces.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemOldCrown;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffDeathDoor;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Blessing of acceptance. Chance to inflict Death Door status on room entrance.
 * @author Callil
 *
 */
public class BlessingOfAcceptance extends Blessing {

	private int chanceToProc = 5;

	@Override
	public String title() {
		return "Blessing of acceptance";
	}
	
	@Override
	public String description() {
		return "On new room entrance, small chance to inflict the [BLACK]Death's door[] status effect to enemies for 3 turns.";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_acceptance;
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
				statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffDeathDoor(3));
				
				Journal.addEntry("[BLACK]Blessing of acceptance inflicted Death's door to " + Journal.getLabel(e));
				AlterationSystem.addAlterationProc(this);
			}
			
		}
	}

}
