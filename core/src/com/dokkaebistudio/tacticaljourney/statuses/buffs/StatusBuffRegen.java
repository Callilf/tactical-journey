/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.statuses.buffs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ces.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.util.AnimatedImage;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * The entity is poisoned.
 * @author Callil
 *
 */
public class StatusBuffRegen extends Status {

	/** The entity that inflicted the poison. */
	private Entity parent;
	
	public StatusBuffRegen() {}
	public StatusBuffRegen(int duration) {
		this.setDuration(duration);
	}
	
	public StatusBuffRegen(int duration, Entity parent) {
		this.setDuration(duration);
		this.parent = parent;
	}
	
	
	@Override
	public String title() {
		return "[GREEN]Regen[]";
	}
	
	@Override
	public String description() {
		return "Recover 1 hp each turn.";
	}

	@Override
	public RegionDescriptor texture() {
		return Assets.status_regen;
	}
	@Override
	public RegionDescriptor fullTexture() {
		return Assets.status_regen_full;
	}
	
	
	@Override
	public boolean onReceive(Entity entity, Room room) {

		animation = new AnimatedImage(AnimationSingleton.getInstance().healing, true);
		animation.setOrigin(Align.center);
		animation.addAction(Actions.scaleTo(1.3f, 1.3f));
		
		PoolableVector2 animPos = TileUtil.convertGridPosIntoPixelPos(Mappers.gridPositionComponent.get(entity).coord());
		animation.setPosition(animPos.x, animPos.y);
		animPos.free();
		GameScreen.fxStage.addActor(animation);
		
		return true;
	}
	
	
	
	

	@Override
	public void onEndTurn(Entity entity, Room room) {
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
		healthComponent.restoreHealth(1);
	}

	
	
	@Override
	public void onRemove(Entity entity, Room room) {
		animation.remove();
	}
	
	@Override
	public void onDeath(Entity entity, Room room) {
		animation.remove();
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
