/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room;

/**
 * The different states a room can be in.
 * @author Callil
 *
 */
public enum RoomState {
	PLAYER_TURN_INIT,
	PLAYER_MOVE_START,
	PLAYER_MOVE_TILES_DISPLAYED,
	PLAYER_MOVE_DESTINATION_SELECTED,
	PLAYER_MOVING,
	PLAYER_END_MOVEMENT,
	ENEMY_TURN_INIT,
	ENEMY_MOVE_START,
	ENEMY_MOVE_TILES_DISPLAYED,
	ENEMY_MOVE_DESTINATION_SELECTED;
}
