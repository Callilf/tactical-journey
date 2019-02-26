/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.creeps;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.enemies.enums.EnemyFactionEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Spider web that slows player, gives free movement to spiders and alert all spiders of the room.
 * @author Callil
 *
 */
public class CreepWeb extends Creep {
	
	public CreepWeb() {
		super("Spider web", Assets.creep_web);
		type = CreepType.WEB;
	}

	@Override
	public boolean isImmune(Entity entity) {
		return Mappers.flyComponent.has(entity);
	}
	
	@Override
	public void onWalk(Entity walker, Entity creep, Room room) {
		if (isImmune(walker)) return;
		
		// If the player walks on it, all spiders are alerted
		if (Mappers.playerComponent.has(walker)) {
			for(Entity e : room.getEnemies()) {
				EnemyComponent enemyComponent = Mappers.enemyComponent.get(e);
				if (enemyComponent != null && enemyComponent.getFaction() == EnemyFactionEnum.SPIDERS) {
					enemyComponent.setAlerted(true);
				}
			}
		}
	}
	
	@Override
	public int getMovementConsumed(Entity mover) {
		if (Mappers.enemyComponent.has(mover)) {
			if (Mappers.enemyComponent.get(mover).getFaction() == EnemyFactionEnum.SPIDERS) return -1;
		} else if (isImmune(mover)) {
			return 0;
		}
		
		return 100;
	}

}
