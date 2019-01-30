package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.room.Room;




public class TileComponent implements Component, Poolable {
	
	/** The room where this tile is. */
	private Room room;
	
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

	/**
	 * The enum referencing all types of tiles.
	 */
	public enum TileEnum {

		GROUND(false, false),
		MUD(false, false),
		WALL(true, false),
		PIT(false, true);
		
		private boolean isWall;
		private boolean isPit;
		
		TileEnum(boolean isW, boolean isP) {
			isWall = isW;
			isPit = isP;
		}
		
		TileEnum(boolean isW, boolean isP, int moveConsumed) {
			isWall = isW;
			isPit = isP;
		}

		public boolean isWall() {
			return isWall;
		}

		public void setWall(boolean isWall) {
			this.isWall = isWall;
		}

		public boolean isPit() {
			return isPit;
		}

		public void setPit(boolean isPit) {
			this.isPit = isPit;
		}
		

		/**
		 * Utility method. This will disappear later
		 * when we will have to handle flight and movement modificators.
		 * @return
		 */
		public boolean isWalkable() {
			return !isWall && !isPit;
		}
		
		/**
		 * Utility method. This will disappear later.
		 * @return
		 */
		public boolean isAttackable() {
			return !isWall && !isPit;
		}
	}
	
	/** The type of the tile. */
    public TileEnum type;
}
