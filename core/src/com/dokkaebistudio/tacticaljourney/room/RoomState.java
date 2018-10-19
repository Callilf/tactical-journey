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
	PLAYER_COMPUTE_MOVABLE_TILES,
	PLAYER_MOVE_TILES_DISPLAYED,
	PLAYER_MOVE_DESTINATION_SELECTED,
	PLAYER_MOVING,
	PLAYER_END_MOVEMENT,
	PLAYER_WHEEL_START,
	PLAYER_WHEEL_TURNING,
	PLAYER_WHEEL_NEEDLE_STOP,
	PLAYER_WHEEL_FINISHED,
	ENEMY_TURN_INIT,
	ENEMY_COMPUTE_MOVABLE_TILES,
	ENEMY_MOVE_TILES_DISPLAYED,
	ENEMY_MOVE_DESTINATION_SELECTED,
	ENEMY_MOVING,
	ENEMY_END_MOVEMENT,
	ENEMY_ATTACK;
	
	
	
	/**
	 * Return true if it's the player's turn, false otherwise.
	 * @return true if it's the player's turn, false otherwise.
	 */
	public boolean isPlayerTurn() {
		return this == RoomState.PLAYER_TURN_INIT 
				|| this == RoomState.PLAYER_COMPUTE_MOVABLE_TILES 
				|| this == RoomState.PLAYER_MOVE_TILES_DISPLAYED
    			|| this == RoomState.PLAYER_MOVE_DESTINATION_SELECTED 
    			|| this == RoomState.PLAYER_MOVING 
    			|| this == RoomState.PLAYER_END_MOVEMENT
    	    	|| this == RoomState.PLAYER_WHEEL_START    			
    			|| this == RoomState.PLAYER_WHEEL_TURNING    			
    			|| this == RoomState.PLAYER_WHEEL_NEEDLE_STOP
    			|| this == RoomState.PLAYER_WHEEL_FINISHED;
	}
	
	/**
	 * Return true if it's the enemies' turn, false otherwise.
	 * @return true if it's the enemies' turn, false otherwise.
	 */
	public boolean isEnemyTurn() {
		return this == RoomState.ENEMY_TURN_INIT 
				|| this == RoomState.ENEMY_COMPUTE_MOVABLE_TILES 
				|| this == RoomState.ENEMY_MOVE_TILES_DISPLAYED
    			|| this == RoomState.ENEMY_MOVE_DESTINATION_SELECTED 
    			|| this == RoomState.ENEMY_MOVING 
    			|| this == RoomState.ENEMY_END_MOVEMENT
    			|| this == RoomState.ENEMY_ATTACK;
	}
	
	/**
	 * Return true if the wheel is displayed on screen, false otherwise.
	 * @return true if wheel is displayed, false otherwise.
	 */
	public boolean isWheelDisplayed() {
		return this == RoomState.PLAYER_WHEEL_START    			
    			|| this == RoomState.PLAYER_WHEEL_TURNING    			
    			|| this == RoomState.PLAYER_WHEEL_NEEDLE_STOP;
	}
}
