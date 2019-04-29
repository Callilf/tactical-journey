/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room;

public enum RoomCreatureState {

	NONE,
	TURN_INIT,
	COMPUTE_MOVABLE_TILES,
	MOVE_TILES_DISPLAYED,
	MOVE_DESTINATION_SELECTED,
	MOVING,
	END_MOVEMENT,
	ATTACK,
	ATTACK_ANIMATION,
	ATTACK_FINISH;
}
