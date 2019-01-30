package com.dokkaebistudio.tacticaljourney.systems.display;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.dokkaebistudio.tacticaljourney.components.display.DamageDisplayComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class DamageDisplaySystem extends IteratingSystem implements RoomSystem {
	    
	/** The current room. */
    private Room room;

    public DamageDisplaySystem(Room r) {
        super(Family.all(DamageDisplayComponent.class).get());
		this.priority = 11;

        room = r;
    }
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
    	DamageDisplayComponent damageDisplayComponent = Mappers.damageDisplayCompoM.get(entity);
    	
    	GridPositionComponent gridPosCompo = Mappers.gridPositionComponent.get(entity);
    	gridPosCompo.absolutePos(gridPosCompo.getAbsolutePos().x, gridPosCompo.getAbsolutePos().y + 1);
    	
    	if (gridPosCompo.getAbsolutePos().y > damageDisplayComponent.getInitialPosition().y + 100) {
    		room.removeEntity(entity);
    	}
    	
    }

}
