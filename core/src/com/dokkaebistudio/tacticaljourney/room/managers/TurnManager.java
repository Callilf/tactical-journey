/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.managers;

import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.singletons.GameTimeSingleton;

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
	
	public void setTurn(int t) {
		turn = t;
	}

	
	/**
	 * Start a new turn.
	 */
	public void startNewTurn() {
		this.turn ++;
		this.room.floor.setTurns(this.room.floor.getTurns() + 1);
		this.room.setNextState(RoomState.PLAYER_TURN_INIT);
		GameTimeSingleton.getInstance().nextTurn();
	}
	
	
	public void endPlayerTurn() {
		this.room.setNextState(RoomState.PLAYER_END_TURN);
	}
	
	public void endAllyTurn() {
		this.room.setNextState(RoomState.ALLY_END_TURN);
	}
	
	public void endEnemyTurn() {
		this.room.setNextState(RoomState.ENEMY_END_TURN);
	}

}
