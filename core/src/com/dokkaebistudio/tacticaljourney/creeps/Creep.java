/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.creeps;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * @author Callil
 *
 */
public abstract class Creep {
	
	public enum CreepType {
		WEB,
		POISON,
		MUD,
		FIRE;
	}
	
	/** The type. */
	protected CreepType type;
	/** The name displayed. */
	private String label;
	/** The name of the image in the assets. */
	private AtlasRegion texture;
	
	/**
	 * Constructor for creep
	 * @param label
	 * @param texture
	 */
	Creep(String label, AtlasRegion texture) {
		this.setLabel(label);
		this.setTexture(texture);
	}
	
	
	// Abstract methods
	
	/** Called when the item is used. */
	public void onWalk(Entity walker, Entity creep, Room room) {};
	
	/** Called when the item is used. */
	public void onStop(Entity walker, Entity creep, Room room) {};
	
	/** Emit the creep. */
	public void onEmit(Entity emitter, Entity emittedCreep, Room room) {};
	
	/** Called when a turn is ended. */
	public void onEndTurn(Entity creep, Room room) {};
	
	/** Called when the creep is added to the game. */
	public void onAppear(Entity creep, Room room) {};
	
	/** Called when the creep disappears from the game. */
	public void onDisappear(Entity creep, Room room) {};
	
	
	
	/**
	 * Whether the given entity is immune to creep.
	 * @param entity
	 * @return
	 */
	public boolean isImmune(Entity entity) {
		return false;
	}
	
	
	
	//********************
	// Movement
	
	/**
	 * Get the movement consumed when walking on this creep.
	 * @param mover the moving entity
	 * @return the number of movement consumed
	 */
	public int getMovementConsumed(Entity mover) {
		return 1;
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
		return 0;
	}
	
	
	
	
	
	//**************************
	// Getters and Setters

	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public AtlasRegion getTexture() {
		return texture;
	}


	public void setTexture(AtlasRegion texture) {
		this.texture = texture;
	}


	public CreepType getType() {
		return type;
	}


	public void setType(CreepType type) {
		this.type = type;
	}
	
	

}
