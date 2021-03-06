package com.dokkaebistudio.tacticaljourney.ces.components.transition;

import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Represents a door to another room.
 * @author Callil
 *
 */
public class DoorComponent implements Component {

	/** Whether the door is opened or closed. */
	private boolean opened;
	
	/** The room on the other side of this door. */
	private Room targetedRoom;

	
	
	public void open(Entity door) {
		this.opened = true;
		
		Mappers.spriteComponent.get(door).updateSprite(Assets.door_opened);
	}
	
	public void close(Entity door) {
		this.opened = false;
		Mappers.spriteComponent.get(door).updateSprite(Assets.door_closed);
	}
	
	// Getters and Setters
	
	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public Room getTargetedRoom() {
		return targetedRoom;
	}

	public void setTargetedRoom(Room targetedRoom) {
		this.targetedRoom = targetedRoom;
	}

	
	
	
	public static Serializer<DoorComponent> getSerializer(final PooledEngine engine,  final Map<Integer, Room> loadedRooms) {
		return new Serializer<DoorComponent>() {

			@Override
			public void write(Kryo kryo, Output output, DoorComponent object) {
				output.writeBoolean(object.opened);
				kryo.writeClassAndObject(output, object.targetedRoom);
			}

			@Override
			public DoorComponent read(Kryo kryo, Input input, Class<? extends DoorComponent> type) {
				DoorComponent compo = engine.createComponent(DoorComponent.class);
				compo.opened = input.readBoolean();
				compo.targetedRoom = (Room) kryo.readClassAndObject(input);				
				return compo;
			}
		
		};
	}
}
