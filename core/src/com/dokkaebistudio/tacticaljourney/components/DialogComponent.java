package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * Marker to indicate that this entity is a dialog popin
 * @author Callil
 *
 */
public class DialogComponent implements Component, Poolable {

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
	
}
