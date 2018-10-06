package com.dokkaebistudio.tacticaljourney.components;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;

public class AttackComponent implements Component, Poolable {
	
	private final ComponentMapper<SpriteComponent> spriteCM = ComponentMapper.getFor(SpriteComponent.class);
	
	/** The engine that managed entities.*/
	public PooledEngine engine;
	
	/** The tiles where the player can attack. */
	public Set<Entity> allAttackableTiles;
	
	/** The entities used to display the red tiles where the entity can attack. */
	public Set<Entity> attackableTiles = new HashSet<>();
		
	/** The selected tile for attack. */
	private Entity selectedTile;
	
	/** The button used to confirm movements. */
	private Entity attackConfirmationButton;
	
	
	@Override
	public void reset() {
		engine = null;
		clearAttackableTiles();
	}
	

	/**
	 * Clear the list of movable tiles and remove all entities associated to it.
	 */
	public void clearAttackableTiles() {
		for (Entity e : attackableTiles) {
			engine.removeEntity(e);
		}
		attackableTiles.clear();
		
		if (allAttackableTiles != null) {
			allAttackableTiles.clear();
		}
		
		if (this.selectedTile != null) {
			engine.removeEntity(this.selectedTile);
		}
		this.selectedTile = null;
		
		if (this.attackConfirmationButton != null) {
			engine.removeEntity(this.attackConfirmationButton);
		}
		this.attackConfirmationButton = null;
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
		return attackConfirmationButton;
	}

	public void setMovementConfirmationButton(Entity movementConfirmationButton) {
		if (this.attackConfirmationButton != null) {
			engine.removeEntity(this.attackConfirmationButton);
		}
		this.attackConfirmationButton = movementConfirmationButton;
	}
	
	
	public void hideAttackableTiles() {
		for (Entity e : attackableTiles) {
			SpriteComponent spriteComponent = spriteCM.get(e);
			spriteComponent.hide = true;
		}
	}
	
	public void hideAttackEntities() {
		if (this.selectedTile != null) {
			SpriteComponent spriteComponent = spriteCM.get(this.selectedTile);
			spriteComponent.hide = true;
		}
		if (this.attackConfirmationButton != null) {
			SpriteComponent spriteComponent = spriteCM.get(this.attackConfirmationButton);
			spriteComponent.hide = true;
		}
	}
	
	
}
