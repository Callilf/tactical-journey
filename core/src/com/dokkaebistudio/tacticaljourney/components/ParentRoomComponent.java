package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * Component idicating that this entity belongs to a given room and should not be
 * processed if this room is not active.
 * @author Callil
 *
 */
public class ParentRoomComponent implements Component {

	/** The room where this entity belongs. */
	private Room parentRoom;
	
	
	

	public Room getParentRoom() {
		return parentRoom;
	}

	public void setParentRoom(Room parentRoom) {
		this.parentRoom = parentRoom;
	}
	
}
