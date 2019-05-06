package com.dokkaebistudio.tacticaljourney.components.display;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is a damage indicator to display on screen.
 * @author Callil
 *
 */
public class DamageDisplayComponent implements Component, Poolable {
	
	private final Vector2 initialPosition = new Vector2(-100, -100);
	
	
	
	@Override
	public void reset() {
		initialPosition.set(-100,-100);		
	}

	
	//***********************
	// Getters and Setters
	
	public Vector2 getInitialPosition() {
		return initialPosition;
	}

	public void setInitialPosition(Vector2 initialPosition) {
		this.initialPosition.set(initialPosition);
	}

	
	
	
	public static Serializer<DamageDisplayComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<DamageDisplayComponent>() {

			@Override
			public void write(Kryo kryo, Output output, DamageDisplayComponent object) {
				output.writeFloat(object.initialPosition.x);
				output.writeFloat(object.initialPosition.y);
			}

			@Override
			public DamageDisplayComponent read(Kryo kryo, Input input, Class<DamageDisplayComponent> type) {
				DamageDisplayComponent compo = engine.createComponent(DamageDisplayComponent.class);
				compo.initialPosition.set((int)input.readFloat(), (int)input.readFloat());
				return compo;
			}
		
		};
	}
}
