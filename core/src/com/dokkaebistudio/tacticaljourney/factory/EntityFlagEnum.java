package com.dokkaebistudio.tacticaljourney.factory;

public enum EntityFlagEnum {

	PLAYER(0),
	ENEMY_SPIDER(1),
	ENEMY_SCORPION(2),
	
	TILE(100),
	DOOR(101),
	
	END_TURN_BUTTON(500),
	MOVABLE_TILE(501),
	ATTACK_TILE(502),
	DESTINATION_TILE(502),
	WAYPOINT(503),
	TEXT(504),
	TEXT_ON_TILE(505),
	DAMAGE_DISPLAYER(506),
	
	
	
	ITEM_HEALTH_UP(1000);
	
	private int flag;
	
	private EntityFlagEnum(int flag) {
		this.flag = flag;
	}
	
	

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
}