/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.components.ChasmComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * @author Callil
 *
 */
public class Tile {

	/** The room where this tile is. */
	private Room room;
	
	/** The grid position. */
	private Vector2 gridPos;
	
	/** The pixel position of this tile. */
	private Vector2 absolutePos;
	
	
	public Tile(Room room, Vector2 gridPos) {
		this.room = room;
		this.gridPos = gridPos;
		this.absolutePos = new Vector2();
		TileUtil.convertGridPosIntoPixelPos(gridPos, absolutePos);
	}
	
	
	
	/**
	 * Check whether the given entity can walk on this tile.
	 * @param walker the walking entity
	 * @return true if the entity can walk on this tile.
	 */
	public boolean isWalkable(Entity walker) {
		Entity solid = TileUtil.getEntityWithComponentOnTile(this.gridPos, SolidComponent.class, room);
		if (solid != null) return false;
		
		Entity chasm = TileUtil.getEntityWithComponentOnTile(this.gridPos, ChasmComponent.class, room);
		if (chasm != null && !Mappers.flyComponent.has(walker)) return false;
		
		return true;
	}
	
	/**
	 * Check whether the given entity can throw something on this tile.
	 * @param thrower the throwing entity
	 * @return true if possible to throw on this tile.
	 */
	public boolean isThrowable(Entity thrower) {
		Entity solid = TileUtil.getEntityWithComponentOnTile(this.gridPos, SolidComponent.class, room);
		if (solid != null) return false;
		
		Entity chasm = TileUtil.getEntityWithComponentOnTile(this.gridPos, ChasmComponent.class, room);
		if (chasm != null) return false;
		
		return true;
	}
	
	
	//************************
	// Getters and Setters
	
	
	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}


	public Vector2 getGridPos() {
		return gridPos;
	}


	public void setGridPos(Vector2 gridPos) {
		this.gridPos = gridPos;
	}


	public Vector2 getAbsolutePos() {
		return absolutePos;
	}


	public void setAbsolutePos(Vector2 absolutePos) {
		this.absolutePos = absolutePos;
	}
	
	

}
