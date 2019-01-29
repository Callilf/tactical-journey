package com.dokkaebistudio.tacticaljourney.ai.pathfinding;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class RoomHeuristic implements Heuristic<Entity> {
	
    
    public RoomHeuristic() {}

	@Override
	public float estimate(Entity node, Entity endNode) {
		GridPositionComponent startPos = Mappers.gridPositionComponent.get(node);
		GridPositionComponent endPos = Mappers.gridPositionComponent.get(endNode);
		return Math.abs(endPos.coord().x - startPos.coord().x) + Math.abs(endPos.coord().y - startPos.coord().y);
	}

}
