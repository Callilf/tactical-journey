/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation;

import java.util.List;

import com.dokkaebistudio.tacticaljourney.enums.TileEnum;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;

/**
 * @author Callil
 *
 */
public class GeneratedRoom {

	/** The matrix of tile entities.*/
	private Tile[][] tiles;
	
	/** The matrix of tile types. */
	private TileEnum[][] tileTypes;
	
	/** The list of possible enemy/item spawns. */
	private List<PoolableVector2> possibleSpawns;
	
	/** The list of possible destructible spawns. */
	private List<PoolableVector2> possibleDestr;
	
	public void releaseSpawns() {
		if (possibleSpawns != null) {
			for (PoolableVector2 coord : possibleSpawns) {
				coord.free();
			}
		}
		if (possibleDestr != null) {
			for (PoolableVector2 coord : possibleDestr) {
				coord.free();
			}
		}
	}
	
	
	// Getters & Setters

	public Tile[][] getTiles() {
		return tiles;
	}

	public void setTiles(Tile[][] tileEntities) {
		this.tiles = tileEntities;
	}

	public TileEnum[][] getTileTypes() {
		return tileTypes;
	}

	public void setTileTypes(TileEnum[][] tileTypes) {
		this.tileTypes = tileTypes;
	}

	public List<PoolableVector2> getPossibleSpawns() {
		return possibleSpawns;
	}

	public void setPossibleSpawns(List<PoolableVector2> enemySpawns) {
		this.possibleSpawns = enemySpawns;
	}


	public List<PoolableVector2> getPossibleDestr() {
		return possibleDestr;
	}


	public void setPossibleDestr(List<PoolableVector2> possibleDestr) {
		this.possibleDestr = possibleDestr;
	}
	
}
