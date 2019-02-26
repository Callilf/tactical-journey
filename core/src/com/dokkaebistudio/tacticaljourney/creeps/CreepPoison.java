/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.creeps;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffPoison;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Mud that slows movements.
 * @author Callil
 *
 */
public class CreepPoison extends Creep {
	
	public CreepPoison() {
		super("poison", Assets.creep_poison);
		type = CreepType.POISON;
	}
	
	@Override
	public boolean isImmune(Entity entity) {
		return Mappers.flyComponent.has(entity);
	}

	@Override
	public void onWalk(Entity walker, Entity creep, Room room) {
		if (isImmune(walker)) return;

		StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(walker);
		statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffPoison(3));
	}
	
	@Override
	public int getHeuristic(Entity mover) {
		if (isImmune(mover)) {
			return 0;
		} else {
			return 100;
		}
	}

}
