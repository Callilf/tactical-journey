/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.components.TextComponent;

/**
 * Manages the turns of a room.
 * @author Callil
 *
 */
public class TurnManager {
	
	private Entity textDisplay;
	private TextComponent textComponent;
	private int turn;
	
	public TurnManager(Room room) {
		turn = 1;
		textDisplay = room.entityFactory.createText(new Vector3(100.0f, 100.0f, 0.0f), "Turn " + turn);
		
		ComponentMapper<TextComponent> textCompoM = ComponentMapper.getFor(TextComponent.class);
		textComponent = textCompoM.get(textDisplay);
	}
	
	public void endTurn() {
		this.turn ++;
		textComponent.setText("Turn " + this.turn);
	}

}
