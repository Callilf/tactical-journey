/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.creeps;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.creature.enemies.enums.EnemyFactionEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Bush that slows movements and hide visibility.
 * @author Callil
 *
 */
public class CreepBush extends Creep {
	
	public CreepBush() {
		super("bush", Assets.tallGrass);
		type = CreepType.BUSH;
	}


	@Override
	public void onWalk(Entity walker, Entity creep, Room room) {}
	
	@Override
	public int getMovementConsumed(Entity mover) {
		if (Mappers.enemyComponent.has(mover)) {
			if (Mappers.enemyComponent.get(mover).getFaction() == EnemyFactionEnum.ORANGUTANS) return -1;
		}
		
		if (isImmune(mover)) {
			return 0;
		} else {
			return 1;
		}
	}

}
