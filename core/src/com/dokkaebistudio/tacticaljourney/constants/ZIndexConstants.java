package com.dokkaebistudio.tacticaljourney.constants;

/**
 * Contains constants for positioning the UI
 * @author Callil
 *
 */
public final class ZIndexConstants {
	
	// TILE
	public static final int TILE = 1;
	
	// ENTITIES ON TILE
	public static final int WALL = 2;
	public static final int MUD = 2;
	public static final int DOOR = 2;
	public static final int EXIT = 2;
	public static final int CREEP = 3;
	public static final int LOOTABLE = 3;

	// INDICATORS
	public static final int MOVABLE_TILE = 5;
	public static final int ATTACKABLE_TILE = 5;
	public static final int DESTINATION_TILE = 11;
	public static final int WAYPOINT = 11;
	
	// ITEMS
	public static final int ITEM = 8;
	public static final int BOMB = 9;
	
	// ENEMIES
	public static final int ENEMY = 10;
	public static final int HEALTH_DISPLAYER = 10;
	
	// PLAYER
	public static final int PLAYER = 15;
	public static final int STATUE = 16;
	
	// DESTRUCTIBLE
	public static final int DESTRUCTIBLE = 17;


	
	// VISUAL EFFECTS
	public static final int EXPLOSION = 50;

	// Damage & xp displayers
	public static final int DAMAGE_DISPLAYER = 100;
	public static final int EXP_DISPLAYER = 100;

}
