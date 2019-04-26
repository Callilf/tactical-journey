package com.dokkaebistudio.tacticaljourney.components.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is an ally, meaning that it won't attack the player
 * but will attack enemies. Also, enemies will attack it.
 * @author Callil
 *
 */
public class AllyComponent implements Component {

	
	
	
	
	public static Serializer<AllyComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<AllyComponent>() {

			@Override
			public void write(Kryo kryo, Output output, AllyComponent object) {}

			@Override
			public AllyComponent read(Kryo kryo, Input input, Class<AllyComponent> type) {
				AllyComponent compo = engine.createComponent(AllyComponent.class);
				return compo;
			}
		
		};
	}
}
