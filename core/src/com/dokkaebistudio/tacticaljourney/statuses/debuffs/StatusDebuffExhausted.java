/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.statuses.debuffs;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * The entity is poisoned.
 * @author Callil
 *
 */
public class StatusDebuffExhausted extends Status {
	
	public StatusDebuffExhausted() {}
	
	public StatusDebuffExhausted(int duration) {
		this.setDuration(duration);
	}
	
	
	@Override
	public String title() {
		return "Exhausted";
	}
	
	@Override
	public String description() {
		return "Reduces strength and move by 1.";
	}

	@Override
	public RegionDescriptor texture() {
		return Assets.status_exhausted;
	}
	@Override
	public RegionDescriptor fullTexture() {
		return Assets.status_exhausted_full;
	}
	
	
	@Override
	public boolean onReceive(Entity entity, Room room) {
		Mappers.attackComponent.get(entity).increaseStrength(-1);
		Mappers.moveComponent.get(entity).increaseMoveSpeed(-1);
		return true;
	}


	@Override
	public void onRemove(Entity entity, Room room) {
		Mappers.attackComponent.get(entity).increaseStrength(1);
		Mappers.moveComponent.get(entity).increaseMoveSpeed(1);
	}
	
	@Override
	public void onDeath(Entity entity, Room room) {
	}
	
	
}
