package com.dokkaebistudio.tacticaljourney.ai.pathfinding;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.dokkaebistudio.tacticaljourney.room.Tile;

public class RoomHeuristic implements Heuristic<Tile> {
	
    
    public RoomHeuristic() {}

	@Override
	public float estimate(Tile node, Tile endNode) {
		return Math.abs(endNode.getGridPos().x - node.getGridPos().x) + Math.abs(endNode.getGridPos().y - node.getGridPos().y);
	}

}
