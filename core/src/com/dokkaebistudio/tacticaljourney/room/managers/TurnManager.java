/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.managers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.constants.PositionConstants;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Manages the turns of a room.
 * @author Callil
 *
 */
public class TurnManager {
	
	private Entity textDisplay;
	private TextComponent textComponent;
	private int turn;
	private Room room;
	
	public TurnManager(Room r) {
		this.room = r;
		turn = 1;
		textDisplay = room.entityFactory.createText(
				new Vector3(PositionConstants.POS_TURN, PositionConstants.Z_TURN),
				"Turn: " + turn,
				r);
		
		textComponent = Mappers.textComponent.get(textDisplay);
	}
	
	public void endPlayerTurn() {
		this.room.state = RoomState.ENEMY_TURN_INIT;
	}
	
	public void endEnemyTurn() {
		this.turn ++;
		textComponent.setText("Turn: " + this.turn);
		this.room.state = RoomState.PLAYER_TURN_INIT;
	}

}
