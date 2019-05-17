/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.creeps;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepImmunityComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffStunned;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;

/**
 * Bush that slows movements and hide visibility + chance to entangle anything that walks through it.
 * @author Callil
 *
 */
public class CreepBanana extends Creep {
	
	private int chanceToProc = 100;
	
	public CreepBanana() {
		super("banana", Assets.creep_banana);
		type = CreepType.BANANA;
	}
	

	@Override
	public void onWalk(Entity walker, Entity creep, Room room) {
		
		int chance = RandomSingleton.getInstance().nextUnseededInt(100);
		if (chance < chanceToProc) {

			// Entangle
			StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(walker);
			if (statusReceiverComponent != null) {
				Journal.addEntry("[YELLOW]" + Journal.getLabel(walker) + " slipped on a banana peel");
				statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffStunned(2));
			}

			// Stop movement
			GridPositionComponent creepPos = Mappers.gridPositionComponent.get(creep);
			MoveComponent moveComponent = Mappers.moveComponent.get(walker);
			moveComponent.setMoveRemaining(0);
			if (moveComponent.moving) {
				moveComponent.setSelectedTile(creepPos.coord(), room);
				MovementHandler.finishRealMovement(walker, room);
			}
			
			room.removeEntity(creep);
		}
	}
	
	@Override
	public int getMovementConsumed(Entity mover) {
		if (isImmune(mover)) {
			return 0;
		} else {
			return 1;
		}
	}

}
