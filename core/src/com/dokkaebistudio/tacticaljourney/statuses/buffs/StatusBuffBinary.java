/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.statuses.buffs;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * The entity has a buff of strength and speed.
 * @author Callil
 *
 */
public class StatusBuffBinary extends Status {

	
	public StatusBuffBinary() {}
	public StatusBuffBinary(int duration) {
		this.setDuration(duration);
	}
	
	
	@Override
	public String title() {
		return "Binary";
	}
	
	@Override
	public String description() {
		return "Strength and move increased by 2.";
	}

	@Override
	public RegionDescriptor texture() {
		return Assets.status_binary;
	}
	@Override
	public RegionDescriptor fullTexture() {
		return Assets.status_binary_full;
	}
	
	
	@Override
	public boolean onReceive(Entity entity, Room room) {
		Mappers.attackComponent.get(entity).increaseStrength(2);
		Mappers.moveComponent.get(entity).increaseMoveSpeed(2);
		if (room.getState().isPlayerTurn()) {
			room.setNextState(RoomState.PLAYER_COMPUTE_MOVABLE_TILES);
		}
		return true;
	}

	
	@Override
	public void onRemove(Entity entity, Room room) {
		Mappers.attackComponent.get(entity).increaseStrength(-2);
		Mappers.moveComponent.get(entity).increaseMoveSpeed(-2);
	}
	
	@Override
	public void onDeath(Entity entity, Room room) {
	}
	
	
}
