package com.dokkaebistudio.tacticaljourney.ai.pathfinding;

import java.util.List;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.dokkaebistudio.tacticaljourney.components.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent;

public class RoomGraph implements IndexedGraph<Entity> {
	
    private final ComponentMapper<GridPositionComponent> gridPositionM;
    private final ComponentMapper<TileComponent> tileCompoM;
	private List<Entity> movableTiles;
	
	public RoomGraph(List<Entity> movableTiles) {
        this.gridPositionM = ComponentMapper.getFor(GridPositionComponent.class);
        this.tileCompoM = ComponentMapper.getFor(TileComponent.class);
        this.movableTiles = movableTiles;
	}

	@Override
	public Array<Connection<Entity>> getConnections(Entity fromNode) {
		Array<Connection<Entity>> connections = new Array<Connection<Entity>>();
		GridPositionComponent firstPos = gridPositionM.get(fromNode);

		for (Entity nodeToTest :  movableTiles) {
			GridPositionComponent secondPos = gridPositionM.get(nodeToTest);
			if (firstPos.coord.x == secondPos.coord.x && firstPos.coord.y == secondPos.coord.y) continue;

			boolean aboveOrUnder = firstPos.coord.x == secondPos.coord.x 
					&& (firstPos.coord.y == secondPos.coord.y + 1 || firstPos.coord.y == secondPos.coord.y -1);
			boolean beside = firstPos.coord.y == secondPos.coord.y 
					&& (firstPos.coord.x == secondPos.coord.x + 1 || firstPos.coord.x == secondPos.coord.x -1);
			
			if (beside || aboveOrUnder) {
				TileComponent tileComponent = tileCompoM.get(nodeToTest);
				connections.add(new RoomConnection(fromNode, nodeToTest, tileComponent.type.getMoveConsumed()));
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
