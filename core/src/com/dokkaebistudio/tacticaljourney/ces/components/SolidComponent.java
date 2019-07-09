package com.dokkaebistudio.tacticaljourney.ces.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is solid so the tile on which it stands is blocked.
 * @author Callil
 *
 */
public class SolidComponent implements Component {

	
	
	
	
	public static Serializer<SolidComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<SolidComponent>() {

			@Override
			public void write(Kryo kryo, Output output, SolidComponent object) {}

			@Override
			public SolidComponent read(Kryo kryo, Input input, Class<? extends SolidComponent> type) {
				SolidComponent compo = engine.createComponent(SolidComponent.class);
				return compo;
			}
		
		};
	}
}
