package com.dokkaebistudio.tacticaljourney.components;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Marker to indicate that this entity can explode, dealing AoE damages.
 * @author Callil
 *
 */
public class ExplosiveComponent implements Component, Poolable, RoomSystem {
	public Room room;
	
	/** The number of turns for the entity to explode. */
	private int turnsToExplode;
	
	/** The turn at which the entity will explode. */
	private Integer explosionTurn;

	
	/** The radius of the explosion.
	 * 0 meaning that only the tile where the explosive object is will be caught into the explosion. */
	private int radius;
	
	/** The amount of damage dealt to any entity caught in the explosion. */
	private int damage;
	
	
	
	//**************************************
	// Attack tiles selection and display
	
	/** The tiles affected by the explosion. */
	public Set<Tile> allAttackableTiles;
	
	/** The entities used to display the red tiles where the explosion will be. */
	public Set<Entity> attackableTiles = new HashSet<>();
	
	
	public void showAttackableTiles() {
		for (Entity e : attackableTiles) {
			SpriteComponent spriteComponent = Mappers.spriteComponent.get(e);
			spriteComponent.hide = false;
		}
	}
	public void hideAttackableTiles() {
		for (Entity e : attackableTiles) {
			SpriteComponent spriteComponent = Mappers.spriteComponent.get(e);
			spriteComponent.hide = true;
		}
	}
	
	
	/**
	 * Clear the list of explosion tiles and remove all entities associated to it.
	 */
	public void clearExplosionTiles() {
		for (Entity e : attackableTiles) {
			room.removeEntity(e);
		}
		attackableTiles.clear();
		
		if (allAttackableTiles != null) {
			allAttackableTiles.clear();
		}
	}
	
	@Override
	public void enterRoom(Room newRoom) {
		this.room = newRoom;
	}
	
	@Override
	public void reset() {
		clearExplosionTiles();
		explosionTurn = null;
		room = null;
	}
	
	
	// Getters and Setters


	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getTurnsToExplode() {
		return turnsToExplode;
	}

	public void setTurnsToExplode(int turnsToExplode) {
		this.turnsToExplode = turnsToExplode;
	}

	public Integer getExplosionTurn() {
		return explosionTurn;
	}

	public void setExplosionTurn(Integer explosionTurn) {
		this.explosionTurn = explosionTurn;
	}
	
	

}
