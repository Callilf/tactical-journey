package com.dokkaebistudio.tacticaljourney.ai.pathfinding;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class RoomGraph implements IndexedGraph<Tile> {
	
	/** The moving entity. */
	private Entity mover;
	
	/** the list of tiles where movement is possible. */
	private List<Tile> movableTiles;
	
	public RoomGraph(Entity mover, List<Tile> movableTiles) {
        this.movableTiles = movableTiles;
        this.mover = mover;
	}

	@Override
	public Array<Connection<Tile>> getConnections(Tile fromTile) {
		Array<Connection<Tile>> connections = new Array<Connection<Tile>>();

		for (Tile tileToTest :  movableTiles) {
			if (fromTile.getGridPos().x == tileToTest.getGridPos().x && fromTile.getGridPos().y == tileToTest.getGridPos().y) continue;

			boolean aboveOrUnder = fromTile.getGridPos().x == tileToTest.getGridPos().x 
					&& (fromTile.getGridPos().y == tileToTest.getGridPos().y + 1 || fromTile.getGridPos().y == tileToTest.getGridPos().y -1);
			boolean beside = fromTile.getGridPos().y == tileToTest.getGridPos().y 
					&& (fromTile.getGridPos().x == tileToTest.getGridPos().x + 1 || fromTile.getGridPos().x == tileToTest.getGridPos().x -1);
			
			if (beside || aboveOrUnder) {
				int cost = TileUtil.getHeuristicCostForTilePos(tileToTest.getGridPos(), mover, tileToTest.getRoom());
				connections.add(new RoomConnection(fromTile, tileToTest, cost));
			}
		}
		
		return connections;
	}

	@Override
	public int getIndex(Tile node) {
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
    public class RoomConnection implements Connection<Tile> {

        private Tile from;
        private Tile to;
        private float cost;

        public RoomConnection(Tile from, Tile to, float cost) {
            this.from = from;
            this.to = to;
            this.cost = cost;
        }

        @Override
        public float getCost() {
            return cost;
        }

        @Override
        public Tile getFromNode() {
            return from;
        }

        @Override
        public Tile getToNode() {
            return to;
        }
    }



	public List<Tile> getMovableTiles() {
		return movableTiles;
	}

	public void setMovableTiles(List<Tile> movableTiles) {
		this.movableTiles = movableTiles;
	}
    
    
    


}
