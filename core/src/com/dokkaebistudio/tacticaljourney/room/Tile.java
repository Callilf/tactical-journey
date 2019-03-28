/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.components.BlockExplosionComponent;
import com.dokkaebistudio.tacticaljourney.components.ChasmComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

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
		Entity wall = TileUtil.getEntityWithComponentOnTile(this.gridPos, BlockExplosionComponent.class, room);
		if (wall != null) return false;
		
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
	
	

	
	public static Serializer<Tile> getSerializer(final PooledEngine engine) {
		return new Serializer<Tile>() {

			@Override
			public void write(Kryo kryo, Output output, Tile object) {
				// Coord
				output.writeFloat(object.getGridPos().x);
				output.writeFloat(object.getGridPos().y);
			}

			@Override
			public Tile read(Kryo kryo, Input input, Class<Tile> type) {
				Vector2 gridPos = new Vector2((int)input.readFloat(), (int)input.readFloat());
				Tile t = new Tile(null, gridPos);
				return t;
			}
		
		};
	}
}
