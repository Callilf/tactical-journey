package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;




public class TileComponent implements Component {
	
	/**
	 * The enum referencing all types of tiles.
	 */
	public enum TileEnum {

		GROUND(false, false),
		WALL(true, false),
		PIT(false, true);
		
		private boolean isWall;
		private boolean isPit;
		
		TileEnum(boolean isW, boolean isP) {
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
		
		
	}
	
	/** The type of the tile. */
    public TileEnum type;
}
