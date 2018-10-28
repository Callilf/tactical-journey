/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.Floor;
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
	private Floor floor;
	
	public TurnManager(Floor f) {
		this.floor = f;
		turn = 1;
		textDisplay = f.getActiveRoom().entityFactory.createText(new Vector3(300.0f, 1050.0f, 0.0f), "Turn " + turn);
		
		ComponentMapper<TextComponent> textCompoM = ComponentMapper.getFor(TextComponent.class);
		textComponent = textCompoM.get(textDisplay);
	}
	
	public void endPlayerTurn() {
		this.floor.getActiveRoom().state = RoomState.ENEMY_TURN_INIT;
	}
	
	public void endEnemyTurn() {
		this.turn ++;
		textComponent.setText("Turn " + this.turn);
		this.floor.getActiveRoom().state = RoomState.PLAYER_TURN_INIT;
	}

}
