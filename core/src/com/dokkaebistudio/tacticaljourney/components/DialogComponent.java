package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is a dialog popin
 * @author Callil
 *
 */
public class DialogComponent implements Component, Poolable, RoomSystem {

	private String speaker;
	private String text;
	private float currentDuration;
	private float duration;
	private Room room;
	
	@Override
	public void reset() {
		currentDuration = 0f;
		duration = 0f;
	}
	
	@Override
	public void enterRoom(Room newRoom) {
		this.room = newRoom;
	}
	
	
	//***************************
	// Getters and Setters
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public float getDuration() {
		return duration;
	}
	public void setDuration(float duration) {
		this.duration = duration;
	}

	public float getCurrentDuration() {
		return currentDuration;
	}
	public void setCurrentDuration(float currentDuration) {
		this.currentDuration = currentDuration;
	}


	public Room getRoom() {
		return room;
	}


	public void setRoom(Room room) {
		this.room = room;
	}


	public String getSpeaker() {
		return speaker;
	}


	public void setSpeaker(String speaker) {
		this.speaker = speaker;
	}
	
	
	
	public static Serializer<DialogComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<DialogComponent>() {

			@Override
			public void write(Kryo kryo, Output output, DialogComponent object) {
				output.writeString(object.speaker);
				output.writeString(object.text);
				output.writeFloat(object.currentDuration);
				output.writeFloat(object.duration);
			}

			@Override
			public DialogComponent read(Kryo kryo, Input input, Class<? extends DialogComponent> type) {
				DialogComponent compo = engine.createComponent(DialogComponent.class);
				compo.speaker = input.readString();
				compo.text = input.readString();
				compo.currentDuration = input.readFloat();
				compo.duration = input.readFloat();
				return compo;
			}
		
		};
	}
}
