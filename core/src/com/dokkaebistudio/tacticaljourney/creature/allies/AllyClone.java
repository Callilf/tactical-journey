package com.dokkaebistudio.tacticaljourney.creature.allies;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.shinobi.BlessingKawarimi;
import com.dokkaebistudio.tacticaljourney.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.creature.Creature;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class AllyClone extends Creature {


	@Override
	public String title() {
		return "Clone";
	}
	
	
	@Override
	public void onLoseTarget(Entity creature, Room room) {
		AIComponent aiComponent = Mappers.aiComponent.get(creature);
		Entity previousTarget = aiComponent.getTarget();
		
		super.onLoseTarget(creature, room);
		
		// Switch to another target

		GridPositionComponent pos = Mappers.gridPositionComponent.get(creature);

		Entity target = null;
		int shortestDistance = -1;
		for (Entity enemy : room.getEnemies()) {
			if (enemy == previousTarget) continue;
			
			GridPositionComponent enemyPos = Mappers.gridPositionComponent.get(enemy);
			int dist = TileUtil.getDistanceBetweenTiles(pos.coord(), enemyPos.coord());
			if (target == null || dist < shortestDistance) {
				target = enemy;
				shortestDistance = dist;
				
				if (dist == 1) break;
			}
		}
		
		if (target != null) {
			Mappers.aiComponent.get(creature).setAlerted(true, creature, target);
		}
	}
	
	@Override
	public void onRoomCleared(Entity creature, Room room) {
		GridPositionComponent pos = Mappers.gridPositionComponent.get(creature);
		BlessingKawarimi.createSmokeEffect(pos.coord());
		room.removeAlly(creature);
	}
}
