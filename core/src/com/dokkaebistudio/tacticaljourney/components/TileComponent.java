package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;




public class TileComponent implements Component, Poolable {
	
	@Override
	public void reset() {
		type = null;
	}
	
	/**
	 * The enum referencing all types of tiles.
	 */
	public enum TileEnum {

		GROUND(false, false, 1),
		MUD(false, false, 2),
		WALL(true, false),
		PIT(false, true);
		
		private boolean isWall;
		private boolean isPit;
		private int moveConsumed;
		
		TileEnum(boolean isW, boolean isP) {
			isWall = isW;
			isPit = isP;
			this.moveConsumed = 1;
		}
		
		TileEnum(boolean isW, boolean isP, int moveConsumed) {
			isWall = isW;
			isPit = isP;
			this.moveConsumed = moveConsumed; 
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
		
		public int getMoveConsumed() {
			return moveConsumed;
		}

		public void setMoveConsumed(int moveConsumed) {
			this.moveConsumed = moveConsumed;
		}

		/**
		 * Utility method. This will disappear later
		 * when we will have to handle flight and movement modificators.
		 * @return
		 */
		public boolean isWalkable() {
			return !isWall && !isPit;
		}
	}
	
	/** The type of the tile. */
    public TileEnum type;
}
