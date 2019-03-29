package com.dokkaebistudio.tacticaljourney.enums;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * The enum referencing all types of tiles.
 */
public enum TileEnum {

	GROUND(false, false),
	MUD(false, false),
	H_WALL(true, false),
	WALL(true, false),
	PIT(false, true),
	BUSH(false, false),
	VINES_BUSH(false, false);
	
	
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
	public boolean isWalkable(Entity walker) {
		return !isWall && !(isPit && !Mappers.flyComponent.has(walker));
	}
	
	/** Whether it is possible to throw something on this tile or not. */
	public boolean isThrowable() {
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
