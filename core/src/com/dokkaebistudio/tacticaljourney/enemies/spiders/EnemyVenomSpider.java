package com.dokkaebistudio.tacticaljourney.enemies.spiders;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.enemies.Enemy;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffPoison;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class EnemyVenomSpider extends Enemy {
	
	private int poisonChance = 33;
	
	@Override
	public void onAttack(Entity enemy, Entity target, Room room) {
		// 33% chance to poison
		RandomXS128 unseededRandom = RandomSingleton.getInstance().getUnseededRandom();
		int randomPercent = unseededRandom.nextInt(100);
		if (randomPercent < poisonChance) {
			StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(target);
			if (statusReceiverComponent != null) {
				
				if (Mappers.playerComponent.has(target)) {
					Journal.addEntry("[SCARLET]Venom spider poisoned you");
				}
				
				statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffPoison(5));
			}
		}
	}
	
	@Override
	public void onDeath(Entity enemy, Entity attacker, Room room) {
		// Release poison creep
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(enemy);
		room.entityFactory.creepFactory.createPoison(room, gridPositionComponent.coord(), null);
	}

}
