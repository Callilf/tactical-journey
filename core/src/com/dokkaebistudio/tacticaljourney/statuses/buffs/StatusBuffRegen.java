/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.statuses.buffs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * The entity is poisoned.
 * @author Callil
 *
 */
public class StatusBuffRegen extends Status {

	/** The entity that inflicted the poison. */
	private Entity parent;
	
	public StatusBuffRegen(int duration) {
		this.setDuration(duration);
	}
	
	public StatusBuffRegen(int duration, Entity parent) {
		this.setDuration(duration);
		this.parent = parent;
	}
	
	
	@Override
	public String title() {
		return "Regen";
	}

	@Override
	public AtlasRegion texture() {
		return Assets.status_regen;
	}

	@Override
	public void onEndTurn(Entity entity) {
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
		healthComponent.restoreHealth(1);
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
