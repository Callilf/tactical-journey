package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

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
		Mappers.spriteComponent.get(door).getSprite().setRegion(Assets.door_opened);
	}
	
	public void close(Entity door) {
		this.opened = false;
		Mappers.spriteComponent.get(door).getSprite().setRegion(Assets.door_closed);
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
	
}
