package com.dokkaebistudio.tacticaljourney.components.interfaces;

import com.badlogic.gdx.math.Vector2;

public interface MovableInterface {

	void initiateMovement(Vector2 currentPos);
	void performMovement(float xOffset, float yOffset);
	void endMovement(Vector2 finalPos);
	
}
