package com.dokkaebistudio.tacticaljourney.components.interfaces;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.components.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.TransformComponent;

public interface MovableInterface {

	void initiateMovement(Vector2 currentPos);
	void performMovement(float xOffset, float yOffset, ComponentMapper<TransformComponent> transfoCM);
	void endMovement(Vector2 finalPos, ComponentMapper<GridPositionComponent> gridPositionM);
	
}
