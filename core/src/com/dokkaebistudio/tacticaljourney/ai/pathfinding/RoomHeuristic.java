package com.dokkaebistudio.tacticaljourney.ai.pathfinding;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;

public class RoomHeuristic implements Heuristic<Entity> {
	
    private final ComponentMapper<GridPositionComponent> gridPositionM;
    
    public RoomHeuristic() {
    	this.gridPositionM = ComponentMapper.getFor(GridPositionComponent.class);
	}

	@Override
	public float estimate(Entity node, Entity endNode) {
		GridPositionComponent startPos = gridPositionM.get(node);
		GridPositionComponent endPos = gridPositionM.get(endNode);
		return Math.abs(endPos.coord.x - startPos.coord.x) + Math.abs(endPos.coord.y - startPos.coord.y);
	}

}
