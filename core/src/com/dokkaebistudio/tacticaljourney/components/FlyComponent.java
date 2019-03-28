package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is flies.
 * @author Callil
 *
 */
public class FlyComponent implements Component {

	
	
	public static Serializer<FlyComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<FlyComponent>() {

			@Override
			public void write(Kryo kryo, Output output, FlyComponent object) {}

			@Override
			public FlyComponent read(Kryo kryo, Input input, Class<FlyComponent> type) {
				FlyComponent compo = engine.createComponent(FlyComponent.class);
				return compo;
			}
		
		};
	}
}
