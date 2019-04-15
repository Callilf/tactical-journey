/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.statuses.debuffs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.enums.DamageType;
import com.dokkaebistudio.tacticaljourney.enums.HealthChangeEnum;
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
public class StatusDebuffPoison extends Status {

	/** The entity that inflicted the poison. */
	private Entity parent;
	
	public StatusDebuffPoison() {}
	
	public StatusDebuffPoison(int duration) {
		this.setDuration(duration);
	}
	
	public StatusDebuffPoison(int duration, Entity parent) {
		this.setDuration(duration);
		this.parent = parent;
	}
	
	
	@Override
	public String title() {
		return "[PURPLE]Poisoned[]";
	}
	
	@Override
	public String description() {
		return "Receive 2 [PURPLE]poison[] damages each turn. [PURPLE]Poison[] damages ignore the armor.";
	}

	@Override
	public RegionDescriptor texture() {
		return Assets.status_poison;
	}
	@Override
	public RegionDescriptor fullTexture() {
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
		
		animation = new AnimatedImage(AnimationSingleton.getInstance().poisoned, true);
		PoolableVector2 animPos = TileUtil.convertGridPosIntoPixelPos(Mappers.gridPositionComponent.get(entity).coord());
		animation.setPosition(animPos.x, animPos.y);
		animPos.free();
		GameScreen.fxStage.addActor(animation);
		
		return true;
	}
	
	@Override
	public void onEndTurn(Entity entity, Room room) {
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
		healthComponent.hitThroughArmor(2, entity, parent, DamageType.POISON);
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
