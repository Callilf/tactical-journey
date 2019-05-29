package com.dokkaebistudio.tacticaljourney.components.transition;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Represents a teleporter.
 * @author Callil
 *
 */
public class SecretDoorComponent implements Component {

	/** Whether the door is opened or closed. */
	private boolean opened;
	
	
	
	public void open(Entity door) {
		this.opened = true;
		Mappers.spriteComponent.get(door).updateSprite(Assets.secret_door_opened);
	}
	
	public void close(Entity door) {
		this.opened = false;
		Mappers.spriteComponent.get(door).updateSprite(Assets.secret_door_closed);
	}
	
	// Getters and Setters
	
	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	
	
	
	public static Serializer<SecretDoorComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<SecretDoorComponent>() {

			@Override
			public void write(Kryo kryo, Output output, SecretDoorComponent object) {
				output.writeBoolean(object.opened);
			}

			@Override
			public SecretDoorComponent read(Kryo kryo, Input input, Class<? extends SecretDoorComponent> type) {
				SecretDoorComponent compo = engine.createComponent(SecretDoorComponent.class);
				compo.opened = input.readBoolean();
				return compo;
			}
		
		};
	}
}
