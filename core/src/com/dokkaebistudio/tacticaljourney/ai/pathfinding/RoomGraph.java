package com.dokkaebistudio.tacticaljourney.ai.pathfinding;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.dokkaebistudio.tacticaljourney.components.TileComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class RoomGraph implements IndexedGraph<Entity> {
	
	/** The moving entity. */
	private Entity mover;
	
	/** the list of tiles where movement is possible. */
	private List<Entity> movableTiles;
	
	public RoomGraph(Entity mover, List<Entity> movableTiles) {
        this.movableTiles = movableTiles;
        this.mover = mover;
	}

	@Override
	public Array<Connection<Entity>> getConnections(Entity fromNode) {
		Array<Connection<Entity>> connections = new Array<Connection<Entity>>();
		GridPositionComponent firstPos = Mappers.gridPositionComponent.get(fromNode);

		for (Entity nodeToTest :  movableTiles) {
			GridPositionComponent secondPos = Mappers.gridPositionComponent.get(nodeToTest);
			if (firstPos.coord().x == secondPos.coord().x && firstPos.coord().y == secondPos.coord().y) continue;

			boolean aboveOrUnder = firstPos.coord().x == secondPos.coord().x 
					&& (firstPos.coord().y == secondPos.coord().y + 1 || firstPos.coord().y == secondPos.coord().y -1);
			boolean beside = firstPos.coord().y == secondPos.coord().y 
					&& (firstPos.coord().x == secondPos.coord().x + 1 || firstPos.coord().x == secondPos.coord().x -1);
			
			if (beside || aboveOrUnder) {
				GridPositionComponent gridPosCompo = Mappers.gridPositionComponent.get(nodeToTest);
				TileComponent tileComponent = Mappers.tileComponent.get(nodeToTest);
				int cost = TileUtil.getCostOfMovementForTilePos(gridPosCompo.coord(), mover, tileComponent.getRoom());
				connections.add(new RoomConnection(fromNode, nodeToTest, cost));
			}
		}
		
		return connections;
	}

	@Override
	public int getIndex(Entity node) {
		return movableTiles.indexOf(node);
	}

	@Override
	public int getNodeCount() {
		return movableTiles.size();
	}
	
	
	/**
	 * RoomConnection internal class.
	 * Specific implementation of Connection to handle movements that cost more than others.
	 */
    public class RoomConnection implements Connection<Entity> {

        private Entity from;
        private Entity to;
        private float cost;

        public RoomConnection(Entity from, Entity to, float cost) {
            this.from = from;
            this.to = to;
            this.cost = cost;
        }

        @Override
        public float getCost() {
            return cost;
        }

        @Override
        public Entity getFromNode() {
            return from;
        }

        @Override
        public Entity getToNode() {
            return to;
        }
    }



	public List<Entity> getMovableTiles() {
		return movableTiles;
	}

	public void setMovableTiles(List<Entity> movableTiles) {
		this.movableTiles = movableTiles;
	}
    
    
    


}
