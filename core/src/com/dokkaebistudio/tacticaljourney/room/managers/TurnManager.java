/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.managers;

import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;

/**
 * Manages the turns of a room.
 * @author Callil
 *
 */
public class TurnManager {
	
	private int turn;
	private Room room;
	
	public TurnManager(Room r) {
		this.room = r;
		turn = 1;
	}
	
	public int getTurn() {
		return turn;
	}
	
	public void endPlayerTurn() {
		this.room.setNextState(RoomState.ENEMY_TURN_INIT);
	}
	
	public void endEnemyTurn() {
		this.turn ++;
		this.room.setNextState(RoomState.PLAYER_TURN_INIT);
	}

}
