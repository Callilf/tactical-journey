package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.dokkaebistudio.tacticaljourney.room.Room;

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
