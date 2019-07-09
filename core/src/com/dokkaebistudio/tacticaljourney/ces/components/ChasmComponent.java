package com.dokkaebistudio.tacticaljourney.ces.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this is a chasm.
 * @author Callil
 *
 */
public class ChasmComponent implements Component {

	
	
	public static Serializer<ChasmComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<ChasmComponent>() {

			@Override
			public void write(Kryo kryo, Output output, ChasmComponent object) {}

			@Override
			public ChasmComponent read(Kryo kryo, Input input, Class<? extends ChasmComponent> type) {
				ChasmComponent compo = engine.createComponent(ChasmComponent.class);
				return compo;
			}
		
		};
	}
}
