/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.statuses.debuffs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.InspectableComponent;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * The entity dies in one shot.
 * @author Callil
 *
 */
public class StatusDebuffDeathDoor extends Status {
	
	public StatusDebuffDeathDoor(int duration) {
		this.setDuration(duration);
	}
	
	
	@Override
	public String title() {
		return "[BLACK]At death's door[]";
	}

	@Override
	public AtlasRegion texture() {
		return Assets.status_death_door;
	}
	@Override
	public AtlasRegion fullTexture() {
		return Assets.status_death_door_full;
	}
	
	
	@Override
	public boolean onReceive(Entity entity, Room room) {
		// TODO handle immunity to death's door
		return true;
	}
	
	@Override
	public void onReceiveDamage(Entity entity, Entity attacker, Room room) {
		
		InspectableComponent inspectableComponent = Mappers.inspectableComponentMapper.get(entity);
		Journal.addEntry(inspectableComponent.getTitle() + " received damage at [BLACK]death's door[]");
		
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
		healthComponent.setHp(0);
	}
	
}
