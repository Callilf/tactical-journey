package com.dokkaebistudio.tacticaljourney.ces.systems;

import com.dokkaebistudio.tacticaljourney.room.Room;

/** Interface that indicates that the system is related to a room and
 * needs to be updated when the active room changes.
 * @author Callil
 *
 */
public interface RoomSystem {

	/** Enter a new room.
	 * 
	 * @param newRoom the room we just entered
	 */
	public abstract void enterRoom(Room newRoom);

}
