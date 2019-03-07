/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.components.player.WheelComponent.Hit;
import com.dokkaebistudio.tacticaljourney.components.player.WheelComponent.Sector;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffPoison;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Blessing of the black mamba. Chance to poison the enemy.
 * @author Callil
 *
 */
public class BlessingBlackMamba extends Blessing {

	private final int chanceToProcOnHit = 25;
	private final int chanceToProcOnCrit = 100;

	@Override
	public String title() {
		return "Blessing of the black mamba";
	}
	
	@Override
	public String description() {
		return "On successful attack, chance of inflicting the [PURPLE]poison[] status effect. 100% chance on critical.";
	}
	
	@Override
	public AtlasRegion texture() {
		return Assets.blessing_black_mamba;
	}

	@Override
	public void onAttack(Entity attacker, Entity target, Sector sector, Room room) {
		int chanceToProc = 0;
		if (sector != null && sector.hit == Hit.HIT) chanceToProc = chanceToProcOnHit;
		else if (sector != null && sector.hit == Hit.CRITICAL) chanceToProc = chanceToProcOnCrit;
		
		int randomValue = RandomSingleton.getInstance().getUnseededRandom().nextInt(100);
		if (randomValue < chanceToProc) {
			StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(target);
			if (statusReceiverComponent != null) {
				Journal.addEntry("Blessing of the black mamba inflicted [PURPLE]poison[]");
				statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffPoison(5, attacker));
			}
		}
	}

}
