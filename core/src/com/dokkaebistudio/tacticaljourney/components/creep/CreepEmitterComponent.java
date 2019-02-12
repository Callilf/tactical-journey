package com.dokkaebistudio.tacticaljourney.components.creep;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.enums.creep.CreepEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * Marker to indicate that this entity is a creep on the floor and have an effect when an entity
 * walk on it.
 * @author Callil
 *
 */
public class CreepEmitterComponent implements Component, Poolable {
	
	/** The type of creep to emit. */
	private CreepEnum type;
	
		
	
	
	
	@Override
	public void reset() {}
	
	public Entity emit(Entity emitter, Vector2 position, Room room) {
		Entity creep = null;
		
		switch(type) {
		case WEB:
			Entity creepAlreadyThere = TileUtil.getEntityWithComponentOnTile(position, CreepComponent.class, room);
			if (creepAlreadyThere != null) room.removeEntity(creepAlreadyThere);
			
			creep = room.entityFactory.creepFactory.createWeb(room, position);
			
			break;
			default:		
		}
		
		
		type.onEmit(emitter, creep,room);
		return creep;
	}

	
	//*************************
	// Getters and Setters

	public CreepEnum getType() {
		return type;
	}

	public void setType(CreepEnum type) {
		this.type = type;
	}
	
}
