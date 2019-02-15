/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.creeps;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * Mud that slows movements.
 * @author Callil
 *
 */
public class CreepMud extends Creep {
	
	public CreepMud() {
		super("mud", Assets.mud);
		type = CreepType.MUD;
	}

	@Override
	public void onWalk(Entity walker, Entity creep, Room room) {}
	
	@Override
	public int getMovementConsumed(Entity mover) {
		return 1;
	}

}
