package com.dokkaebistudio.tacticaljourney.ces.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is subject to gravity.
 * @author Callil
 *
 */
public class GravityComponent implements Component {

	
	
	
	
	public static Serializer<GravityComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<GravityComponent>() {

			@Override
			public void write(Kryo kryo, Output output, GravityComponent object) {}

			@Override
			public GravityComponent read(Kryo kryo, Input input, Class<? extends GravityComponent> type) {
				GravityComponent compo = engine.createComponent(GravityComponent.class);
				return compo;
			}
		
		};
	}
}
