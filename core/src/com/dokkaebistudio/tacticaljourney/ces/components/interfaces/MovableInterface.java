package com.dokkaebistudio.tacticaljourney.ces.components.interfaces;

import com.badlogic.gdx.math.Vector2;

public interface MovableInterface {

	//*****************************************************
	// Fluent movement
	void initiateMovement(Vector2 currentPos);
	void performMovement(float xOffset, float yOffset);
	void endMovement(Vector2 finalPos);
	//
	//*****************************************************
	
	/** Place on a given tile without fluent movement.
	 * 
	 * @param tilePos the destination tile.
	 */
	void place(Vector2 tilePos);
}
