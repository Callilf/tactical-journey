package com.dokkaebistudio.tacticaljourney.components.creep;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.creeps.Creep.CreepType;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is immune to one or several creeps on the floor.
 * @author Callil
 *
 */
public class CreepImmunityComponent implements Component, Poolable {
	
	/** The types of creep the entity is immune to. */
	private List<CreepType> types = new ArrayList<>();
	
		
	
	@Override
	public void reset() {
		types.clear();
	}
	
	
	public boolean isImmune(CreepType creepType) {
		return types.contains(creepType);
	}

	
	//*************************
	// Getters and Setters

	public List<CreepType> getTypes() {
		return types;
	}
	
	
	
	
	public static Serializer<CreepImmunityComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<CreepImmunityComponent>() {

			@Override
			public void write(Kryo kryo, Output output, CreepImmunityComponent object) {
				kryo.writeClassAndObject(output, object.types);
			}

			@Override
			public CreepImmunityComponent read(Kryo kryo, Input input, Class<? extends CreepImmunityComponent> type) {
				CreepImmunityComponent compo = engine.createComponent(CreepImmunityComponent.class);
				compo.types = (List<CreepType>) kryo.readClassAndObject(input);
				return compo;
			}
		
		};
	}
}
