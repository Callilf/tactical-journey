package com.dokkaebistudio.tacticaljourney.components.creep;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.enums.creep.CreepEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Marker to indicate that this entity is a creep on the floor and have an effect when an entity
 * walk on it.
 * @author Callil
 *
 */
public class CreepComponent implements Component, Poolable {
	
	/** The type of creep. */
	private CreepEnum type;

	/** The number of turns this creep stays on the floor. 0 means infinite. */
	private int duration;
	
	/** The current number of turns this creep has been here. */
	private int currentDuration;
	
	
	
	//***********************
	// Images for animations
	
	/** The image for the animation of removing the creep. */
	private Image removeCreepImage;

	
	
	
	/**
	 * Play the effect of this creep when an entity walks on it.
	 * @param walker the entity that walked on it
	 * @param creep the creep entity
	 * @param room the room
	 */
	public void onWalk(Entity walker, Entity creep, Room room) {
		type.onWalk(walker, creep, room);
	}
	
	/**
	 * Get the movement consumed when walking on this tile.
	 * @param mover the moving entity
	 * @return the cost of movement
	 */
	public int getMovementConsumed(Entity mover) {
		return type.getMovementConsumed(mover);
	}
	
	
	
	@Override
	public void reset() {
		this.setCurrentDuration(0);
	}
	
	
	/**
	 * Set up the disappearance animation.
	 */
	public Image getRemoveCreepImage(Entity creep) {
		GridPositionComponent positionComponent = Mappers.gridPositionComponent.get(creep);
		CreepComponent creepComponent = Mappers.creepComponent.get(creep);

		if (removeCreepImage == null) {
			this.removeCreepImage = new Image(Assets.getTexture(creepComponent.getType().getImageName()));
		} else {
			this.removeCreepImage.clear();
		}
		
		Vector2 worldPos = positionComponent.getWorldPos();
		this.removeCreepImage.setPosition(worldPos.x, worldPos.y);
		
		Action removeImageAction = new Action(){
		  @Override
		  public boolean act(float delta){
			  removeCreepImage.remove();
			  return true;
		  }
		};

		this.removeCreepImage.setOrigin(Align.center);
		this.removeCreepImage.addAction(Actions.sequence(Actions.scaleTo(0, 0, 1f),removeImageAction));
				
		
		return this.removeCreepImage;
	}	
	
	
	//*************************
	// Getters and Setters

	public CreepEnum getType() {
		return type;
	}

	public void setType(CreepEnum type) {
		this.type = type;
	}
	
	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getCurrentDuration() {
		return currentDuration;
	}

	public void setCurrentDuration(int currentDuration) {
		this.currentDuration = currentDuration;
	}

}
