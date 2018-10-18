package com.dokkaebistudio.tacticaljourney.components.interfaces;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TransformComponent;

public interface MovableInterface {

	void initiateMovement(Vector2 currentPos);
	void performMovement(float xOffset, float yOffset, ComponentMapper<TransformComponent> transfoCM);
	void endMovement(Vector2 finalPos, ComponentMapper<GridPositionComponent> gridPositionM);
	
}
