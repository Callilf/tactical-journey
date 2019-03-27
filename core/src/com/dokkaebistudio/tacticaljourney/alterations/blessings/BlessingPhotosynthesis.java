/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent.StatusActionEnum;
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
	public void onRoomVisited(Entity entity, Room room) {
		RandomXS128 unseededRandom = RandomSingleton.getInstance().getUnseededRandom();
		for (Entity e : room.getEnemies()) {
			
			if (unseededRandom.nextInt(100) < chanceToProc) {
				StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(e);
				statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffEntangled(10));
				
				EnemyComponent enemyComponent = Mappers.enemyComponent.get(e);
				Journal.addEntry("[FOREST]Photosynthesis entangled " + enemyComponent.getType().title());
			}
			
		}
	}

}
