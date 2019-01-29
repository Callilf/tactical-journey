package com.dokkaebistudio.tacticaljourney.components.display;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.room.Room;

public class GridPositionComponent implements Component {
	
    private final Vector2 coord = new Vector2(0,0);
    
    public int zIndex = 0;
    
    
    public void coord(Vector2 coord) {
    	this.coord.set(coord.x,coord.y);
    }
    public void coord(int x, int y) {
    	this.coord.set(x,y);
    }
    
    public void coord(Entity e, Vector2 coord, Room r) {
    	r.removeEntityAtPosition(e, this.coord);    	
    	this.coord.set(coord.x,coord.y);
    	r.addEntityAtPosition(e, this.coord);
    }
    
    public void coord(Entity e, int x, int y, Room r) {
    	r.removeEntityAtPosition(e, this.coord);    	
    	this.coord.set(x,y);
    	r.addEntityAtPosition(e, this.coord);
    }
    
    public Vector2 coord() {
    	return this.coord;
    }
}
