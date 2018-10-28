/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;

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
	
	public TurnManager(Room room) {
		this.room = room;
		turn = 1;
		textDisplay = room.entityFactory.createText(new Vector3(300.0f, 1050.0f, 0.0f), "Turn " + turn);
		
		ComponentMapper<TextComponent> textCompoM = ComponentMapper.getFor(TextComponent.class);
		textComponent = textCompoM.get(textDisplay);
	}
	
	public void endPlayerTurn() {
		this.room.state = RoomState.ENEMY_TURN_INIT;
	}
	
	public void endEnemyTurn() {
		this.turn ++;
		textComponent.setText("Turn " + this.turn);
		this.room.state = RoomState.PLAYER_TURN_INIT;
	}

}
