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
	TREASURE_ROOM("Treasure room"),
	
	// Mandatory rooms
	
	ITEM_ROOM("Item room"),
	KEY_ROOM("Key room"),
	SHOP_ROOM("Shop"),
	STATUE_ROOM("Statue"),
	
	
	// Optional rooms
	
	GIFT_ROOM("Gift room"),
	CHALICE_ROOM("Chalice room"),
	MINI_BOSS_ROOM("Mini-boss room", false, 1, 2),
	
	
	
	BOSS_ROOM("Boss room");
	
	private String label;
	private boolean canBeOnExitPath;
	private int minNeighbors;
	private int maxNeighbors;
	
	private RoomType(String label) {
		this.label = label;
		this.canBeOnExitPath = true;
		this.minNeighbors = 0;
		this.maxNeighbors = 4;

	}
	
	private RoomType(String label, boolean canBeOnExitPath, int minNeightbors, int maxNeighbors) {
		this.label = label;
		this.canBeOnExitPath = canBeOnExitPath;
		this.minNeighbors = minNeightbors;
		this.maxNeighbors = maxNeighbors;
	}
	
	

	public String title() {
		return label;
	}

	public boolean isCanBeOnExitPath() {
		return canBeOnExitPath;
	}

	public void setCanBeOnExitPath(boolean canBeOnExitPath) {
		this.canBeOnExitPath = canBeOnExitPath;
	}

	public int getMinNeighbors() {
		return minNeighbors;
	}

	public void setMinNeighbors(int minNeighbors) {
		this.minNeighbors = minNeighbors;
	}

	public int getMaxNeighbors() {
		return maxNeighbors;
	}

	public void setMaxNeighbors(int maxNeighbors) {
		this.maxNeighbors = maxNeighbors;
	}
	
}
