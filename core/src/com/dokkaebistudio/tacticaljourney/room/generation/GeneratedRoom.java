/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.components.TileComponent.TileEnum;

/**
 * @author Callil
 *
 */
public class GeneratedRoom {

	/** The matrix of tile entities.*/
	private Entity[][] tileEntities;
	
	/** The matrix of tile types. */
	private TileEnum[][] tileTypes;
	
	/** The list of possible enemy/item spawns. */
	private List<Vector2> possibleSpawns;
	
	
	// Getters & Setters

	public Entity[][] getTileEntities() {
		return tileEntities;
	}

	public void setTileEntities(Entity[][] tileEntities) {
		this.tileEntities = tileEntities;
	}

	public TileEnum[][] getTileTypes() {
		return tileTypes;
	}

	public void setTileTypes(TileEnum[][] tileTypes) {
		this.tileTypes = tileTypes;
	}

	public List<Vector2> getPossibleSpawns() {
		return possibleSpawns;
	}

	public void setPossibleSpawns(List<Vector2> enemySpawns) {
		this.possibleSpawns = enemySpawns;
	}
	
}