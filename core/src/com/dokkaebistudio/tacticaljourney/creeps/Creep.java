/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.creeps;

import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.ces.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.creep.CreepImmunityComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * @author Callil
 *
 */
public abstract class Creep {
	
	public enum CreepType {
		WEB,
		POISON,
		MUD,
		FIRE,
		LAVA,
		BUSH,
		VINES_BUSH,
		BANANA;
	}
	
	/** The type. */
	protected CreepType type;
	/** The name displayed. */
	private String label;
	/** The name of the image in the assets. */
	private RegionDescriptor texture;
	
	/**
	 * Constructor for creep
	 * @param label
	 * @param texture
	 */
	Creep(String label, RegionDescriptor texture) {
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
	public void onAppear(Entity creep, Room room) {
		
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(creep);
		Set<Entity> entitiesWithComponentOnTile = TileUtil.getEntitiesWithComponentOnTile(gridPositionComponent.coord(), CreepComponent.class, room);
		for (Entity c : entitiesWithComponentOnTile) {
			if (creep == c) continue;
			
			//Remove previous creeps
			room.removeEntity(c);
		}
		
	};
	
	/** Called when the creep disappears from the game. */
	public void onDisappear(Entity creep, Room room) {};
	
	
	
	/**
	 * Whether the given entity is immune to creep.
	 * @param entity
	 * @return
	 */
	public boolean isImmune(Entity entity) {
		boolean immune = Mappers.flyComponent.has(entity);
		
		if (!immune) {
			CreepImmunityComponent creepImmunityComponent = Mappers.creepImmunityComponent.get(entity);
			if (creepImmunityComponent != null) {
				immune = creepImmunityComponent.isImmune(this.type);
			}
		}
		
		return immune;
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


	public RegionDescriptor getTexture() {
		return texture;
	}


	public void setTexture(RegionDescriptor texture) {
		this.texture = texture;
	}


	public CreepType getType() {
		return type;
	}


	public void setType(CreepType type) {
		this.type = type;
	}
	
	

}
