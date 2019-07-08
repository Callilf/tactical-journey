package com.dokkaebistudio.tacticaljourney.dialog;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.room.Room;

public interface DialogEffect {

	/**
	 * @param room the current room
	 */
	public void play(Entity speaker, Room room);
}
