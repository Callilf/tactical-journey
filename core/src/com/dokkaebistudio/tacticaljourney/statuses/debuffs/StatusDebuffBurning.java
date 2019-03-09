/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.statuses.debuffs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.enums.DamageType;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * The entity is burning.
 * @author Callil
 *
 */
public class StatusDebuffBurning extends Status {

	/** The entity that inflicted the fire. */
	private Entity parent;
	
	/** The chance for the burning to stop. */
	private int stopChance = 0;
	
	public StatusDebuffBurning() {
		this.setDuration(null);
	}
	
	public StatusDebuffBurning(Entity parent) {
		this.setDuration(null);
		this.parent = parent;
	}
	
	
	@Override
	public String title() {
		return "Burning";
	}

	@Override
	public AtlasRegion texture() {
		return Assets.status_burning;
	}
	
	@Override
	public void onStartTurn(Entity entity, Room room) {
		RandomXS128 unseededRandom = RandomSingleton.getInstance().getUnseededRandom();
		if (unseededRandom.nextInt(100) < stopChance) {
			Mappers.statusReceiverComponent.get(entity).requestAction(StatusActionEnum.REMOVE_STATUS, this);
		} else {
			stopChance += 10;
		}
	}

	@Override
	public void onEndTurn(Entity entity, Room room) {
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
		healthComponent.hit(3, entity, parent, DamageType.FIRE);
	}
	
	
	
	@Override
	public void addUp(Status addedStatus) {
		// Reset the stop chance
		this.stopChance = 0;
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
