/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.statuses.buffs;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * The entity has a buff of strength.
 * @author Callil
 *
 */
public class StatusBuffMighty extends Status {

	
	public StatusBuffMighty() {}
	public StatusBuffMighty(int duration) {
		this.setDuration(duration);
	}
	
	
	@Override
	public String title() {
		return "Mighty";
	}
	
	@Override
	public String description() {
		return "Strength increased by 1.";
	}

	@Override
	public RegionDescriptor texture() {
		return Assets.status_mighty;
	}
	@Override
	public RegionDescriptor fullTexture() {
		return Assets.status_mighty_full;
	}
	
	
	@Override
	public boolean onReceive(Entity entity, Room room) {
		Mappers.attackComponent.get(entity).increaseStrength(1);
		return true;
	}

	
	@Override
	public void onRemove(Entity entity, Room room) {
		Mappers.attackComponent.get(entity).increaseStrength(-1);
	}
	
	@Override
	public void onDeath(Entity entity, Room room) {
	}
	
	
}
