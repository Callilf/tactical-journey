package com.dokkaebistudio.tacticaljourney.components.display;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class GridPositionComponent implements Component, Poolable {
	
	/** The pixel pos used by the RenderingSystem. NEVER UPDATE THIS VALUE MANUALLY. */
	private final Vector2 worldPos = new Vector2(0, 0);
	
	/** The tile coordinates. */
    private final Vector2 coord = new Vector2(0,0);
    
    /** Whether this entity is using absolute position or tile position. */
    private boolean hasAbsolutePos = false;
    /** The absolute position of the entity (if using it). */
	private Vector2 absolutePos = new Vector2();

    
    public int zIndex = 0;
    
    
    @Override
    public void reset() {
    	coord.set(0,0);
    	hasAbsolutePos = false;
    	absolutePos.set(0,0);
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
    	r.removeEntityAtPosition(e, this.coord);    	
    	this.coord.set(coord.x,coord.y);
    	r.addEntityAtPosition(e, this.coord);
    }
    
    public void coord(Entity e, int x, int y, Room r) {
    	this.hasAbsolutePos = false;
    	r.removeEntityAtPosition(e, this.coord);    	
    	this.coord.set(x,y);
    	r.addEntityAtPosition(e, this.coord);
    }
    
    public Vector2 coord() {
    	return this.coord;
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
	
	
    
    
}
