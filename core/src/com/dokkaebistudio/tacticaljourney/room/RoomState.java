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
	PLAYER_PAUSE_MOVEMENT,
	
	PLAYER_TARGETING_START,
	PLAYER_TARGETING,
	PLAYER_TARGETING_STOP,
	
	PLAYER_WHEEL_START,
	PLAYER_WHEEL_TURNING,
	PLAYER_WHEEL_NEEDLE_STOP,
	PLAYER_WHEEL_FINISHED,
	
	PLAYER_ATTACK_ANIMATION,
	
	PLAYER_THROWING,
	
	PLAYER_STUNNED,
	
	PLAYER_END_TURN,
	
	
	ALLY_COMPUTE_TILES_TO_DISPLAY_TO_PLAYER,
	ALLY_TURN,
	ALLY_END_TURN,
	
	ENEMY_COMPUTE_TILES_TO_DISPLAY_TO_PLAYER,
	ENEMY_TURN,
	ENEMY_END_TURN,
	
	
	ITEM_DROP_ANIM,
	
	INSPECT_MODE_INIT,
	INSPECT_MODE,
	INSPECT_POPIN,
	
	TELEPORT_POPIN,
	
	PROFILE_POPIN,
	LEVEL_UP_POPIN,
	INVENTORY_POPIN,
	LOOT_POPIN,
	ITEM_POPIN,
	CONTEXTUAL_ACTION_POPIN,
	STATUS_POPIN,
	CHARACTERISTICS_POPIN,
	
	DEBUG_POPIN,
	
	WAITING;
	
	
	
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
    			|| this == RoomState.PLAYER_PAUSE_MOVEMENT
    			|| this == RoomState.PLAYER_END_MOVEMENT
    			|| this == RoomState.PLAYER_TARGETING_START
    			|| this == RoomState.PLAYER_TARGETING
    			|| this == RoomState.PLAYER_TARGETING_STOP
    	    	|| this == RoomState.PLAYER_WHEEL_START    			
    			|| this == RoomState.PLAYER_WHEEL_TURNING    			
    			|| this == RoomState.PLAYER_WHEEL_NEEDLE_STOP
    			|| this == RoomState.PLAYER_WHEEL_FINISHED
    			|| this == RoomState.PLAYER_ATTACK_ANIMATION
    			|| this == RoomState.PLAYER_THROWING
    			|| this == RoomState.PLAYER_STUNNED
    			|| this == RoomState.PLAYER_END_TURN;
	}
	
	/**
	 * Return true if it's the allies' turn, false otherwise.
	 * @return true if it's the allies' turn, false otherwise.
	 */
	public boolean isAllyTurn() {
		return this == RoomState.ALLY_COMPUTE_TILES_TO_DISPLAY_TO_PLAYER
				|| this == RoomState.ALLY_TURN;
	}
	
	/**
	 * Return true if it's the enemies' turn, false otherwise.
	 * @return true if it's the enemies' turn, false otherwise.
	 */
	public boolean isEnemyTurn() {
		return this == RoomState.ENEMY_COMPUTE_TILES_TO_DISPLAY_TO_PLAYER
				|| this == RoomState.ENEMY_TURN;
	}
	
	/**
	 * Whether this state is an in game state.
	 * @return true if the current state is an in game state
	 */
	public boolean isInGameState() {
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
    			|| this == RoomState.PLAYER_WHEEL_FINISHED
    			|| this == RoomState.PLAYER_ATTACK_ANIMATION
    			|| this == RoomState.PLAYER_THROWING
    			|| this == RoomState.PLAYER_STUNNED
    			|| this == RoomState.PLAYER_END_TURN
    			|| this == RoomState.ALLY_COMPUTE_TILES_TO_DISPLAY_TO_PLAYER
    			|| this == RoomState.ALLY_TURN
				|| this == RoomState.ENEMY_COMPUTE_TILES_TO_DISPLAY_TO_PLAYER
				|| this == RoomState.ENEMY_TURN;
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
	 * Whether the player can open its inventory or not.
	 * @return true if the player can open the inventory.
	 */
	public boolean canOpenInventory() {
		return this == RoomState.PLAYER_MOVE_TILES_DISPLAYED
    			|| this == RoomState.PLAYER_MOVE_DESTINATION_SELECTED
    			|| this == RoomState.INSPECT_MODE
    			|| this == RoomState.PROFILE_POPIN;
	}
	
	/**
	 * Whether the player can end his turn or not.
	 * @return true if the turn can be ended manually.
	 */
	public boolean canEndTurn() {
		return this == RoomState.PLAYER_MOVE_TILES_DISPLAYED
    			|| this == RoomState.PLAYER_MOVE_DESTINATION_SELECTED
    			|| this == RoomState.PLAYER_STUNNED;
    			
	}
	
	
	/**
	 * Whether the game is paused or not.
	 * @return true if in a state where the game is paused.
	 */
	public boolean isPaused() {
		return this == RoomState.LEVEL_UP_POPIN;
	}
	
	/**
	 * Whether the room is partially hidden by a popin or not.
	 * @return true if a popin is opened.
	 */
	public boolean isPopinDisplayed() {
		return this == RoomState.PROFILE_POPIN
				|| this == RoomState.LEVEL_UP_POPIN
				|| this == RoomState.INVENTORY_POPIN
				|| this == RoomState.LOOT_POPIN
				|| this == RoomState.ITEM_POPIN
				|| this == RoomState.CONTEXTUAL_ACTION_POPIN
				|| this == RoomState.STATUS_POPIN
				|| this == RoomState.CHARACTERISTICS_POPIN
				|| this == RoomState.INSPECT_POPIN
				|| this == RoomState.TELEPORT_POPIN
				
				|| this == RoomState.DEBUG_POPIN;
	}
	
	public boolean isSkillChangeAllowed() {
		return this == PLAYER_MOVE_TILES_DISPLAYED || this == RoomState.PLAYER_MOVE_DESTINATION_SELECTED || this == RoomState.PLAYER_TARGETING;
	}

	public boolean isInspectMode() {
		return this == INSPECT_MODE_INIT || this == RoomState.INSPECT_MODE || this == RoomState.INSPECT_POPIN;
	}
	
	/**
	 * Whether the game needs to update or not.
	 * @return true is th game needs to update.
	 */
	public boolean updateNeeded() {
		return !isWheelDisplayed() && !isPaused();
	}
}
