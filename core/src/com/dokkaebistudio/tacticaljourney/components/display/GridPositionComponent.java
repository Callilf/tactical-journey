package com.dokkaebistudio.tacticaljourney.components.display;

import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class GridPositionComponent implements Component, Poolable {
	
	/** The pixel pos used by the RenderingSystem. NEVER UPDATE THIS VALUE MANUALLY. */
	private final Vector2 worldPos = new Vector2(-100, -100);
	
	/** The tile coordinates. */
    private final Vector2 coord = new Vector2(-100,-100);
    
    /** Whether this entity is using absolute position or tile position. */
    private boolean hasAbsolutePos = false;
    /** The absolute position of the entity (if using it). */
	private Vector2 absolutePos = new Vector2();
	
	public Room room;

    
    public int zIndex = 0;
    public int overlap = 1;
    
    /** Whether the gridPositionComponent is inactive, meaning that the entity has no real
     * effect on the room at the moment. For example, if an item is in the inventory, it's gridPosCompo
     * is inactive since it is not in the room at this moment.
     */
    private boolean inactive;
    
    
    // Orbit
	private int orbitRadius;
	private float orbitSpeed;
	private float orbitCurrentPercentage;
    
    
    
    @Override
    public void reset() {
    	coord.set(-100,-100);
    	hasAbsolutePos = false;
    	absolutePos.set(-100,-100);
    	zIndex = 0;
    	overlap = 1;
    }
    
    
    public void coord(Vector2 coord) {
    	this.coord.set(coord.x,coord.y);
    }
    public void coord(int x, int y) {
    	this.coord.set(x,y);
    }
    
    public void coord(Entity e, Vector2 coord, Room r) {
    	this.hasAbsolutePos = false;
    	if (r != null) r.removeEntityAtPosition(e, this.coord);    	
    	this.coord.set(coord.x,coord.y);
    	if (r != null) r.addEntityAtPosition(e, this.coord);
    	this.room = r;
    }
    
    public void coord(Entity e, int x, int y, Room r) {
    	this.hasAbsolutePos = false;
    	if (r != null) r.removeEntityAtPosition(e, this.coord);    	
    	this.coord.set(x,y);
    	if (r != null) r.addEntityAtPosition(e, this.coord);
    	this.room = r;
    }
    
    public Vector2 coord() {
    	return this.coord;
    }
    
    
	public void setInactive(Entity e, Room r) {
		this.inactive = true;
		r.removeEntityAtPosition(e, this.coord);    	
	}
    
    
	public void setActive(Entity e, Room r) {
		this.inactive = false;
	    r.addEntityAtPosition(e, this.coord);
	}
    
    
    public void absolutePos(float x, float y) {
    	this.hasAbsolutePos = true;
    	this.absolutePos.set(x,y);
    }
    
    public boolean hasAbsolutePos() {
    	return this.hasAbsolutePos;
    }
    
	public Vector2 getAbsolutePos() {
		return absolutePos;
	}


	public Vector2 getWorldPos() {
		TileUtil.convertGridPosIntoPixelPos(coord, worldPos);
		return worldPos;
	}


	public boolean isInactive() {
		return inactive;
	}
	

	public int getOrbitRadius() {
		return orbitRadius;
	}


	public void setOrbitRadius(int orbitRadius) {
		this.orbitRadius = orbitRadius;
	}


	public float getOrbitSpeed() {
		return orbitSpeed;
	}


	public void setOrbitSpeed(float orbitSpeed) {
		this.orbitSpeed = orbitSpeed;
	}


	public float getOrbitCurrentPercentage() {
		return orbitCurrentPercentage;
	}


	public void setOrbitCurrentPercentage(float orbitCurrentPercentage) {
		this.orbitCurrentPercentage = orbitCurrentPercentage;
	}



	
	public static Serializer<GridPositionComponent> getSerializer(final PooledEngine engine,  final Map<Integer, Room> loadedRooms) {
		return new Serializer<GridPositionComponent>() {

			@Override
			public void write(Kryo kryo, Output output, GridPositionComponent object) {
				output.writeInt(object.zIndex);
				output.writeInt(object.overlap);
				output.writeInt(object.room != null ? object.room.getIndex() : -1);
				
				// Coord
				output.writeFloat(object.coord().x);
				output.writeFloat(object.coord().y);
				
				// Absolute pos
				output.writeFloat(object.getAbsolutePos().x);
				output.writeFloat(object.getAbsolutePos().y);
				output.writeBoolean(object.hasAbsolutePos);
				
				// Inactive
				output.writeBoolean(object.inactive);
				
				// Orbit
				output.writeInt(object.orbitRadius);
				output.writeFloat(object.orbitSpeed);

			}

			@Override
			public GridPositionComponent read(Kryo kryo, Input input, Class<GridPositionComponent> type) {
				GridPositionComponent gridPosCompo = engine.createComponent(GridPositionComponent.class);

				gridPosCompo.zIndex = input.readInt(); 
				gridPosCompo.overlap = input.readInt(); 
						
				int roomIndex = input.readInt();
				if (roomIndex != -1) {
					Room roomFromIndex = loadedRooms.get(roomIndex);
					gridPosCompo.room = roomFromIndex;
				}
				
				// Coord
				gridPosCompo.coord((int)input.readFloat(), (int)input.readFloat());
				
				// Absolute pos
				gridPosCompo.absolutePos((int)input.readFloat(), (int)input.readFloat());
				gridPosCompo.hasAbsolutePos = input.readBoolean();

				gridPosCompo.inactive = input.readBoolean();
				
				// Orbit
				gridPosCompo.orbitRadius = input.readInt();
				gridPosCompo.orbitSpeed = input.readFloat();
				
				return gridPosCompo;
			}
		
		};
	}

}
