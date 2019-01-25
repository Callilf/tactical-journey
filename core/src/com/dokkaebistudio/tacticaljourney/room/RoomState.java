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
	
	PLAYER_TARGETING_START,
	PLAYER_TARGETING,
	PLAYER_TARGETING_STOP,
	
	PLAYER_WHEEL_START,
	PLAYER_WHEEL_TURNING,
	PLAYER_WHEEL_NEEDLE_STOP,
	PLAYER_WHEEL_FINISHED,
	
	
	ENEMY_COMPUTE_TILES_TO_DISPLAY_TO_PLAYER,
	ENEMY_TURN_INIT,
	ENEMY_COMPUTE_MOVABLE_TILES,
	ENEMY_MOVE_TILES_DISPLAYED,
	ENEMY_MOVE_DESTINATION_SELECTED,
	ENEMY_MOVING,
	ENEMY_END_MOVEMENT,
	ENEMY_ATTACK,
	
	
	LEVEL_UP_POPIN;
	
	
	
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
    			|| this == RoomState.PLAYER_TARGETING_START
    			|| this == RoomState.PLAYER_TARGETING
    			|| this == RoomState.PLAYER_TARGETING_STOP
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
		return this == RoomState.ENEMY_COMPUTE_TILES_TO_DISPLAY_TO_PLAYER
				|| this == RoomState.ENEMY_TURN_INIT 
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
	
	/**
	 * Whether the player can end his turn or not.
	 * @return true if the turn can be ended manually.
	 */
	public boolean canEndTurn() {
		return this == RoomState.PLAYER_MOVE_TILES_DISPLAYED
    			|| this == RoomState.PLAYER_MOVE_DESTINATION_SELECTED;
	}
	
	
	/**
	 * Whether the game is paused or not.
	 * @return true if in a state where the game is paused.
	 */
	public boolean isPaused() {
		return this == RoomState.LEVEL_UP_POPIN;
	}
	
	public boolean isSkillChangeAllowed() {
		return this == PLAYER_MOVE_TILES_DISPLAYED || this == RoomState.PLAYER_MOVE_DESTINATION_SELECTED || this == RoomState.PLAYER_TARGETING;
	}
}
