package com.dokkaebistudio.tacticaljourney.components.creep;

import java.util.Optional;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.creeps.Creep.CreepType;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
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
public class CreepEmitterComponent implements Component, Poolable {
	
	/** The type of creep to emit. */
	private CreepType type;
	
		
	
	
	
	@Override
	public void reset() {}
	
	public Entity emit(Entity emitter, Vector2 position, Room room) {
		Entity creep = null;
		
		switch(type) {
		case WEB:
			Optional<Entity> creepAlreadyThere = TileUtil.getEntityWithComponentOnTile(position, CreepComponent.class, room);
			if (creepAlreadyThere.isPresent()) room.removeEntity(creepAlreadyThere.get());
			
			creep = room.entityFactory.creepFactory.createWeb(room, position);
			
			break;
			default:		
		}
		
		CreepComponent creepComponent = Mappers.creepComponent.get(creep);
		creepComponent.onEmit(emitter, creep,room);
		
		return creep;
	}

	
	//*************************
	// Getters and Setters

	public CreepType getType() {
		return type;
	}

	public void setType(CreepType type) {
		this.type = type;
	}
	
	
	
	
	public static Serializer<CreepEmitterComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<CreepEmitterComponent>() {

			@Override
			public void write(Kryo kryo, Output output, CreepEmitterComponent object) {
				output.writeString(object.type.name());
			}

			@Override
			public CreepEmitterComponent read(Kryo kryo, Input input, Class<CreepEmitterComponent> type) {
				CreepEmitterComponent compo = engine.createComponent(CreepEmitterComponent.class);
				compo.type = CreepType.valueOf(input.readString());
				return compo;
			}
		
		};
	}
}
