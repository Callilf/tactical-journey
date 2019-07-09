package com.dokkaebistudio.tacticaljourney.ces.components.neutrals;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is a soul bender.
 * @author Callil
 *
 */
public class CalishkaComponent implements Component, Poolable {
	
	@Override
	public void reset() {
	}



	//*********************************
	// Getters and Setters


	
	public static Serializer<CalishkaComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<CalishkaComponent>() {

			@Override
			public void write(Kryo kryo, Output output, CalishkaComponent object) {
			}

			@Override
			public CalishkaComponent read(Kryo kryo, Input input, Class<? extends CalishkaComponent> type) {
				CalishkaComponent compo = engine.createComponent(CalishkaComponent.class);
				return compo;
			}
		
		};
	}


	
}
