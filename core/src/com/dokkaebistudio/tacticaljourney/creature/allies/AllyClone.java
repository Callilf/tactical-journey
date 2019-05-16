package com.dokkaebistudio.tacticaljourney.creature.allies;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.creature.Creature;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.dokkaebistudio.tacticaljourney.vfx.VFXUtil;

public class AllyClone extends Creature {


	@Override
	public String title() {
		return "Clone";
	}
	
	@Override
	public void onStartTurn(Entity creature, Room room) {
		AIComponent aiComponent = Mappers.aiComponent.get(creature);
		if (aiComponent.getTarget() != null) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(aiComponent.getTarget());
			if (gridPositionComponent != null) {
				// Check if the current target can still be reached
				boolean canMoveToTarget = TileUtil.canMoveToEnemy(creature, gridPositionComponent.coord(), room);
				if (!canMoveToTarget) aiComponent.setTarget(null);
			}
		}
		
		if (aiComponent.getTarget() == null) {
			// If no target, select a new one
			selectTarget(creature, null, room);
		}
	}
	
	
	@Override
	public void onLoseTarget(Entity creature, Room room) {
		AIComponent aiComponent = Mappers.aiComponent.get(creature);
		Entity previousTarget = aiComponent.getTarget();
		
		super.onLoseTarget(creature, room);
		
		// Switch to another target
		selectTarget(creature, previousTarget, room);
	}


	
	@Override
	public void onRoomCleared(Entity creature, Room room) {
		GridPositionComponent pos = Mappers.gridPositionComponent.get(creature);
		VFXUtil.createSmokeEffect(pos.coord());
		room.removeAlly(creature);
	}
	
	
	// Utils
	

	private void selectTarget(Entity creature, Entity previousTarget, Room room) {
		GridPositionComponent pos = Mappers.gridPositionComponent.get(creature);

		Entity target = null;
		int shortestDistance = -1;
		for (Entity enemy : room.getEnemies()) {
			if (enemy == previousTarget) continue;
			
			GridPositionComponent enemyPos = Mappers.gridPositionComponent.get(enemy);
			int dist = TileUtil.getDistanceBetweenTiles(pos.coord(), enemyPos.coord());
			if (target == null || dist < shortestDistance) {
				
				//Check whether there is a path to this target
				if (!TileUtil.canMoveToEnemy(creature, enemyPos.coord(), room)) continue;
				
				target = enemy;
				shortestDistance = dist;
				
				if (dist == 1) break;
			}
		}
		
		if (target != null) {
			Mappers.aiComponent.get(creature).setAlerted(true, creature, target);
		}
	}
}
