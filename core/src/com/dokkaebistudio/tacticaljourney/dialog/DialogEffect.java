package com.dokkaebistudio.tacticaljourney.dialog;

import com.dokkaebistudio.tacticaljourney.room.Room;

public interface DialogEffect {

	/**
	 * @param room the current room
	 */
	public void play( Room room);
}
