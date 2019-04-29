/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.creeps;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingUnfinished;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
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
	public boolean isImmune(Entity entity) {
		AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(entity);
		if (alterationReceiverComponent != null) {
			Blessing blessing = alterationReceiverComponent.getBlessing(BlessingUnfinished.class);
			if (blessing != null) return true;
		}
		
		return Mappers.flyComponent.has(entity);
	}

	@Override
	public void onWalk(Entity walker, Entity creep, Room room) {}
	
	@Override
	public int getMovementConsumed(Entity mover) {
		if (isImmune(mover)) {
			return 0;
		} else {
			return 1;
		}
	}

}
