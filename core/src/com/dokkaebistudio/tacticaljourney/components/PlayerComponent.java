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
	private Entity selectedTile;
	
	/** The button used to confirm movements. */
	private Entity movementConfirmationButton;
	
	
	/**
	 * Clear the list of movable tiles and remove all entities associated to it.
	 */
	public void clearMovableTiles() {
		for (Entity e : movableTiles) {
			engine.removeEntity(e);
		}
		movableTiles.clear();
		
		if (this.selectedTile != null) {
			engine.removeEntity(this.selectedTile);
		}
		this.selectedTile = null;
		
		if (this.movementConfirmationButton != null) {
			engine.removeEntity(this.movementConfirmationButton);
		}
		this.movementConfirmationButton = null;
	}
	
	

	public Entity getSelectedTile() {
		return selectedTile;
	}

	public void setSelectedTile(Entity selectedTile) {
		if (this.selectedTile != null) {
			engine.removeEntity(this.selectedTile);
		}
		this.selectedTile = selectedTile;
	}



	public Entity getMovementConfirmationButton() {
		return movementConfirmationButton;
	}

	public void setMovementConfirmationButton(Entity movementConfirmationButton) {
		if (this.movementConfirmationButton != null) {
			engine.removeEntity(this.movementConfirmationButton);
		}
		this.movementConfirmationButton = movementConfirmationButton;
	}
	
	
	
}
