package com.dokkaebistudio.tacticaljourney.ces.components.creep;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.creeps.Creep;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is a creep on the floor and have an effect when an entity
 * walk on it.
 * @author Callil
 *
 */
public class CreepComponent implements Component, Poolable {
	
	public enum CreepReleasedTurnEnum {
		PLAYER,
		ALLY,
		ENEMY;
		
		
		public static CreepReleasedTurnEnum getReleaseTurn(RoomState roomState) {
			if (roomState.isPlayerTurn()) {
				return PLAYER;
			} else if (roomState.isAllyTurn()) {
				return ALLY;
			} else {
				return ENEMY;
			}
		}
	}
	
	/** The type of creep. */
	private Creep type;

	/** The number of turns this creep stays on the floor. 0 means infinite. 
	 * The turn where the creep is generated counts as the first turn ! */
	private int duration;
	
	/** The turn where the creep should disappear. */
	private int durationLastTurn = -1;
	
	/** Whether the creep was released during the player's turn or the enemy turn. */
	private CreepReleasedTurnEnum releasedTurn;
	
	
	
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
		if (!type.isImmune(walker)) {
			type.onWalk(walker, creep, room);
		}
	}
	
	
	/**
	 * Play the effect of this creep when an entity ends it's turn on it.
	 * @param walker the entity
	 * @param creep the creep
	 * @param room the room
	 */
	public void onStop(Entity walker, Entity creep, Room room) {
		if (!type.isImmune(walker)) {
			type.onStop(walker, creep, room);
		}
	}
	
	/**
	 * Play the end turn effect of the creep.
	 * @param creep the creep
	 * @param room the room
	 */
	public void onEndTurn(Entity creep, Room room) {
		type.onEndTurn(creep, room);
	}

	
	public void onAppear(Entity creep, Room room) {
		type.onAppear(creep, room);
	}
	
	public void onDisappear(Entity creep, Room room) {
		type.onDisappear(creep, room);
	}
	
	public void onEmit(Entity emitter, Entity emittedCreep, Room room) {
		type.onEmit(emitter, emittedCreep, room);
	}
	
	/**
	 * Get the movement consumed when walking on this tile.
	 * @param mover the moving entity
	 * @return the cost of movement
	 */
	public int getMovementConsumed(Entity mover) {
		return type.getMovementConsumed(mover);
	}
	
	/**
	 * Get the heuristic influence of walking on this creep.
	 * 0 means no influence
	 * a negative value is a good influence and the pathfinding will tend to use this tile
	 * a positive value is a bad influence and the pathfinding will tend to avoid this tile
	 * @param mover the moving entity
	 * @return the influence of this creep on the heuristic for the pathfinding.
	 */
	public int getHeuristic(Entity mover) {
		return type.getHeuristic(mover);
	}
	
	
	
	@Override
	public void reset() {
		this.durationLastTurn = -1;
		this.removeCreepImage = null;
		this.releasedTurn = null;
	}
	
	
	/**
	 * Set up the disappearance animation.
	 */
	public Image getRemoveCreepImage(Entity creep) {
		GridPositionComponent positionComponent = Mappers.gridPositionComponent.get(creep);
		CreepComponent creepComponent = Mappers.creepComponent.get(creep);

		this.removeCreepImage = new Image(creepComponent.getType().getTexture().getRegion());
		
		Vector2 worldPos = positionComponent.getWorldPos();
		this.removeCreepImage.setPosition(worldPos.x, worldPos.y);
		
		Action removeImageAction = new Action(){
		  @Override
		  public boolean act(float delta){
			  if (removeCreepImage != null) removeCreepImage.remove();
			  return true;
		  }
		};

		this.removeCreepImage.setOrigin(Align.center);
		this.removeCreepImage.addAction(Actions.sequence(Actions.scaleTo(0, 0, 0.5f),removeImageAction));
				
		
		return this.removeCreepImage;
	}	
	
	
	//*************************
	// Getters and Setters

	public Creep getType() {
		return type;
	}

	public void setType(Creep type) {
		this.type = type;
	}
	
	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public CreepReleasedTurnEnum getReleasedTurn() {
		return releasedTurn;
	}


	public void setReleasedTurn(CreepReleasedTurnEnum releasedTurn) {
		this.releasedTurn = releasedTurn;
	}

	public int getDurationLastTurn() {
		return durationLastTurn;
	}

	public void setDurationLastTurn(int durationLastTurn) {
		this.durationLastTurn = durationLastTurn;
	}
	
	
	
	public static Serializer<CreepComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<CreepComponent>() {

			@Override
			public void write(Kryo kryo, Output output, CreepComponent object) {
				
				kryo.writeClassAndObject(output, object.type);
				output.writeInt(object.duration);
				output.writeInt(object.durationLastTurn);
				kryo.writeClassAndObject(output, object.releasedTurn);
			}

			@Override
			public CreepComponent read(Kryo kryo, Input input, Class<? extends CreepComponent> type) {
				CreepComponent compo = engine.createComponent(CreepComponent.class);
				
				compo.type = (Creep) kryo.readClassAndObject(input);
				
				compo.duration = input.readInt();
				compo.durationLastTurn = input.readInt();
				compo.releasedTurn = (CreepReleasedTurnEnum) kryo.readClassAndObject(input);
				
				return compo;
			}
		
		};
	}

}
