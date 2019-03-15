/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.statuses.debuffs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.enums.DamageType;
import com.dokkaebistudio.tacticaljourney.enums.HealthChangeEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * The entity is poisoned.
 * @author Callil
 *
 */
public class StatusDebuffPoison extends Status {

	/** The entity that inflicted the poison. */
	private Entity parent;
	
	public StatusDebuffPoison(int duration) {
		this.setDuration(duration);
	}
	
	public StatusDebuffPoison(int duration, Entity parent) {
		this.setDuration(duration);
		this.parent = parent;
	}
	
	
	@Override
	public String title() {
		return "Poisoned";
	}

	@Override
	public AtlasRegion texture() {
		return Assets.status_poison;
	}
	@Override
	public AtlasRegion fullTexture() {
		return Assets.status_poison_full;
	}
	
	
	@Override
	public boolean onReceive(Entity entity, Room room) {
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
		int resistance = healthComponent.getResistance(DamageType.POISON);
		if (resistance == 100) {
			healthComponent.healthChangeMap.put(HealthChangeEnum.RESISTANT, "POISON IMMUNITY");
			return false;
		}
		return true;
	}
	
	@Override
	public void onEndTurn(Entity entity, Room room) {
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
		healthComponent.hitThroughArmor(2, entity, parent, DamageType.POISON);
	}

	
	
	
	//********************
	// Getters and setters

	public Entity getParent() {
		return parent;
	}

	public void setParent(Entity parent) {
		this.parent = parent;
	}
	
	
}
