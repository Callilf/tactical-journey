package com.dokkaebistudio.tacticaljourney.components.display;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class GridPositionComponent implements Component, Poolable {
	
	/** The pixel pos used by the RenderingSystem. NEVER UPDATE THIS VALUE MANUALLY. */
	private final Vector2 worldPos = new Vector2(-100, -100);
	
	/** The tile coordinates. */
    private final Vector2 coord = new Vector2(-100,-100);
    
    /** Whether this entity is using absolute position or tile position. */
    private boolean hasAbsolutePos = false;
    /** The absolute position of the entity (if using it). */
	private Vector2 absolutePos = new Vector2();

    
    public int zIndex = 0;
    
    /** Whether the gridPositionComponent is inactive, meaning that the entity has no real
     * effect on the room at the moment. For example, if an item is in the inventory, it's gridPosCompo
     * is inactive since it is not in the room at this moment.
     */
    private boolean inactive;
    
    
    @Override
    public void reset() {
    	coord.set(-100,-100);
    	hasAbsolutePos = false;
    	absolutePos.set(-100,-100);
    	zIndex = 0;
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
    }
    
    public void coord(Entity e, int x, int y, Room r) {
    	this.hasAbsolutePos = false;
    	if (r != null) r.removeEntityAtPosition(e, this.coord);    	
    	this.coord.set(x,y);
    	if (r != null) r.addEntityAtPosition(e, this.coord);
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
	
	
    
    
}
