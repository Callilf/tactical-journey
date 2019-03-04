/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room;

/**
 * The different types of rooms.
 * @author Callil
 *
 */
public enum RoomType {
	START_FLOOR_ROOM ("Entrance"),
	END_FLOOR_ROOM("Exit"),
	
	EMPTY_ROOM("Room"),
	COMMON_ENEMY_ROOM("Room"),
	
	KEY_ROOM("Key room"),
	SHOP_ROOM("Shop"),
	STATUE_ROOM("Statue"),
	
	
	BOSS_ROOM("Boss room");
	
	private String label;
	
	private RoomType(String label) {
		this.setLabel(label);
	}
	
	

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
}
