package com.dokkaebistudio.tacticaljourney.components;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;

public class PlayerComponent implements Component {
	
	/** The engine that managed entities.*/
	public PooledEngine engine;
	
	/** The number of tiles the player can move. */
	public int moveSpeed;
	
	/** The tiles where the player can move. */
	public Set<Entity> movableTiles = new HashSet<>();
	/** The selected tile for movement. */
	public Entity selectedTile;
	
	
	/**
	 * Clear the list of movable tiles and remove all entities associated to it.
	 */
	public void clearMovableTiles() {
		for (Entity e : movableTiles) {
			engine.removeEntity(e);
		}
		movableTiles.clear();
	}
	
	/**
	 * Clear the selected tile and remove it from the engine.
	 */
	public void clearSelectedTile() {
		if (selectedTile != null) {
			engine.removeEntity(selectedTile);
			selectedTile = null;
		}
	}
	
}
