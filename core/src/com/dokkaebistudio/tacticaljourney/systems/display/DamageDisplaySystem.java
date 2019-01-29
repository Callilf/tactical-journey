package com.dokkaebistudio.tacticaljourney.systems.display;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.dokkaebistudio.tacticaljourney.components.display.DamageDisplayComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TransformComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class DamageDisplaySystem extends IteratingSystem implements RoomSystem {
	    
	/** The current room. */
    private Room room;

    public DamageDisplaySystem(Room r) {
        super(Family.all(DamageDisplayComponent.class, TransformComponent.class, TextComponent.class).get());
        room = r;
    }
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
    	DamageDisplayComponent damageDisplayComponent = Mappers.damageDisplayCompoM.get(entity);
    	
    	TransformComponent transfoCompo = Mappers.transfoComponent.get(entity);
    	transfoCompo.pos.y = transfoCompo.pos.y + 1;
    	
    	if (transfoCompo.pos.y > damageDisplayComponent.getInitialPosition().y + 100) {
    		room.removeEntity(entity);
    	}
    	
    }

}
