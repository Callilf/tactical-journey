package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is a wormhole and allows traveling to another tile in the room.
 * @author Callil
 *
 */
public class WormholeComponent implements Component {

	private Vector2 destination = new Vector2(1,1);
	
	
	

	// Getters and Setters
	
	public Vector2 getDestination() {
		return destination;
	}

	public void setDestination(Vector2 destination) {
		this.destination.set(destination);
	}


	

	public static Serializer<WormholeComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<WormholeComponent>() {

			@Override
			public void write(Kryo kryo, Output output, WormholeComponent object) {
				kryo.writeClassAndObject(output, object.destination);
			}

			@Override
			public WormholeComponent read(Kryo kryo, Input input, Class<? extends WormholeComponent> type) {
				WormholeComponent compo = engine.createComponent(WormholeComponent.class);
				compo.destination = (Vector2) kryo.readClassAndObject(input);
				return compo;
			}
		
		};
	}
}
