/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemOldCrown;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffBurning;
import com.dokkaebistudio.tacticaljourney.systems.entitysystems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.wheel.Sector;
import com.dokkaebistudio.tacticaljourney.wheel.Sector.Hit;

/**
 * Blessing of cinders. Chance to set ablaze the enemy.
 * @author Callil
 *
 */
public class BlessingOfCinders extends Blessing {

	private int initialChanceToProc = 20;
	private int oldCrownInInventoryAdd = 10;
	
	@Override
	public String title() {
		return "Blessing of cinders";
	}
	
	@Override
	public String description() {
		return "On successful melee attack, chance to inflict the [ORANGE]burning[] status effect. Chance increased while holding the Old crown";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_cinders;
	}
	
	
	@Override
	public Integer getCurrentProcChance(Entity user) {
		int chanceToProc = this.initialChanceToProc;
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(user);
		if (inventoryComponent.contains(ItemOldCrown.class)) {
			chanceToProc += this.oldCrownInInventoryAdd;
		}
		
		return chanceToProc;
	}

	@Override
	public void onAttack(Entity attacker, Entity target, Sector sector, AttackComponent attackCompo, Room room) {
		if (sector == null || sector.hit == Hit.CRITICAL || sector.hit == Hit.HIT || sector.hit == Hit.GRAZE) {
			
			float randomValue = RandomSingleton.getNextChanceWithKarma();
			if (randomValue < getCurrentProcChance(attacker)) {
				Journal.addEntry("Blessing of cinders inflicted [ORANGE]burning[] to " + Mappers.inspectableComponent.get(target).getTitle());
				AlterationSystem.addAlterationProc(this);

				StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(target);
				if (statusReceiverComponent != null) {
					statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffBurning(attacker));
				}
			}
		}
	}
	

}
