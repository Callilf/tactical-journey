package com.dokkaebistudio.tacticaljourney.components.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is a child entity of a parent one.
 * @author Callil
 *
 */
public class ParentEntityComponent implements Component, Poolable {
	
	/** The parent entity. */
	private Entity parent;
	
	

	@Override
	public void reset() {
		parent = null;
	}


	public Entity getParent() {
		return parent;
	}


	public void setParent(Entity parent) {
		this.parent = parent;
	}


	
	
	
	
	public static Serializer<ParentEntityComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<ParentEntityComponent>() {

			@Override
			public void write(Kryo kryo, Output output, ParentEntityComponent object) {
				kryo.writeClassAndObject(output, object.parent);
			}

			@Override
			public ParentEntityComponent read(Kryo kryo, Input input, Class<? extends ParentEntityComponent> type) {
				ParentEntityComponent compo = engine.createComponent(ParentEntityComponent.class);
				compo.parent = (Entity) kryo.readClassAndObject(input);
				return compo;
			}
		
		};
	}


}
