package com.dokkaebistudio.tacticaljourney.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class ExperienceSystem extends IteratingSystem implements RoomSystem {
	    
	/** The current room. */
    private Room room;

    public ExperienceSystem(Room r) {
        super(Family.all(ExperienceComponent.class).get());
        room = r;
    }
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
    	
    	ExperienceComponent expCompo = Mappers.experienceComponent.get(entity);
    	if (expCompo.hasLeveledUp()) {
    		expCompo.setLeveledUp(false);
    		
    		room.entityFactory.createExpDisplayer(134351351, new Vector2(1,1));
    		//Display stats improvement
    		//TODO
    	}
    	
    }

}
