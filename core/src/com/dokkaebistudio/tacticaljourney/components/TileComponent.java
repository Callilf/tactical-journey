package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.enums.TileEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;




public class TileComponent implements Component, Poolable {
	
	/** The room where this tile is. */
	private Room room;
	
	/** The type of the tile. */
    public TileEnum type;

	
	@Override
	public void reset() {
		type = null;
		setRoom(null);
	}
	
	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}


}
