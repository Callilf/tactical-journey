package com.dokkaebistudio.tacticaljourney.ces.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is humanoid and therefore will avoid hazards when
 * choosing destination and path.
 * @author Callil
 *
 */
public class HumanoidComponent implements Component {

	
	
	public static Serializer<HumanoidComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<HumanoidComponent>() {

			@Override
			public void write(Kryo kryo, Output output, HumanoidComponent object) {}

			@Override
			public HumanoidComponent read(Kryo kryo, Input input, Class<? extends HumanoidComponent> type) {
				HumanoidComponent compo = engine.createComponent(HumanoidComponent.class);
				return compo;
			}
		
		};
	}

}
