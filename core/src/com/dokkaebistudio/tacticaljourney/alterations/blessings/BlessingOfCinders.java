/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemOldCrown;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffBurning;
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
		return "On successful melee attack, chance to inflict the [ORANGE]burning[] status effect.";
	}
	
	@Override
	public AtlasRegion texture() {
		return Assets.blessing_cinders;
	}

	@Override
	public void onAttack(Entity attacker, Entity target, Sector sector, Room room) {
		if (sector.hit == Hit.CRITICAL || sector.hit == Hit.HIT || sector.hit == Hit.GRAZE) {
			
			int chanceToProc = this.initialChanceToProc;
			InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(attacker);
			if (inventoryComponent.contains(ItemOldCrown.class)) {
				chanceToProc += this.oldCrownInInventoryAdd;
			}
			
			RandomXS128 unseededRandom = RandomSingleton.getInstance().getUnseededRandom();
			int chance = unseededRandom.nextInt(100);
			if (chance < chanceToProc) {
				StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(target);
				if (statusReceiverComponent != null) {
					statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffBurning(attacker));
				}
			}
		}
	}
	

}
