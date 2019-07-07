/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.statuses.debuffs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.enums.DamageType;
import com.dokkaebistudio.tacticaljourney.enums.HealthChangeEnum;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.util.AnimatedImage;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

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
		return "[ORANGE]Burning[]";
	}
	
	
	@Override
	public String description() {
		return "Lose 3 hp each turn. This status effect has an increasing chance to disappear each turn.";
	}

	@Override
	public RegionDescriptor texture() {
		return Assets.status_burning;
	}
	@Override
	public RegionDescriptor fullTexture() {
		return Assets.status_burning_full;
	}
	
	
	@Override
	public boolean onReceive(Entity entity, Room room) {
		this.removeEntangledStatus(entity, room);
		
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
		int resistance = healthComponent.getResistance(DamageType.FIRE);
		if (resistance >= 100) {
			healthComponent.healthChangeMap.put(HealthChangeEnum.RESISTANT, "FIRE IMMUNITY");
			return false;
		}
		
		animation = new AnimatedImage(AnimationSingleton.getInstance().burning, true);
		PoolableVector2 animPos = TileUtil.convertGridPosIntoPixelPos(Mappers.gridPositionComponent.get(entity).coord());
		animation.setPosition(animPos.x, animPos.y);
		animPos.free();
		GameScreen.fxStage.addActor(animation);
		return true;
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
	public void onRemove(Entity entity, Room room) {
		animation.remove();
	}
	
	@Override
	public void onDeath(Entity entity, Room room) {
		animation.remove();
	}
	
	
	
	@Override
	public void addUp(Status addedStatus) {
		// Reset the stop chance
		this.stopChance = 0;
	}
	
	
	public void removeEntangledStatus(Entity e, Room room) {
		StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(e);
		if (statusReceiverComponent != null && statusReceiverComponent.hasStatus(StatusDebuffEntangled.class)) {
			Status status = statusReceiverComponent.getStatus(StatusDebuffEntangled.class);
			Journal.addEntry("[ORANGE]Burning[] destroyed the vines and removed the [FOREST]entangled[] status effect");
			statusReceiverComponent.removeStatus(e, status, room);						
		}
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
