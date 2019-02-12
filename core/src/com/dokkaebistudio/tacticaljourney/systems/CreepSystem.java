package com.dokkaebistudio.tacticaljourney.systems;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class CreepSystem extends EntitySystem implements RoomSystem {
	    
	public GameScreen gameScreen;
	public Stage fxStage;
	
	/** The current room. */
    private Room room;    
    
    /** The creeps of the current room that need updating. */
    private List<Entity> allCreepsOfCurrentRoom;


    public CreepSystem(GameScreen gameScreen, Room r, Stage s) {
		this.priority = 21;

		this.gameScreen = gameScreen;
        this.room = r;
        this.fxStage = s;
		this.allCreepsOfCurrentRoom = new ArrayList<>();

    }
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }

    @Override
	public void update(float deltaTime) {
    	
    	
    	if (room.getState() == RoomState.PLAYER_TURN_INIT) {
        	fillEntitiesOfCurrentRoom();

        	for (Entity creep : allCreepsOfCurrentRoom) {
		    	CreepComponent creepComponent = Mappers.creepComponent.get(creep);
		    
	    		creepComponent.setCurrentDuration(creepComponent.getCurrentDuration() + 1);
		    	if (creepComponent.getDuration() > 0 && creepComponent.getCurrentDuration() >= creepComponent.getDuration()) {
		    		// Duration reached, remove the creep
		    		Image removeCreepImage = creepComponent.getRemoveCreepImage(creep);
		    		fxStage.addActor(removeCreepImage);
		    		
		    		room.removeEntity(creep);
		    	}
        	}
        	
    	}
    }

    
    
    
    //*****************
    // Utils methods

	private void fillEntitiesOfCurrentRoom() {
		allCreepsOfCurrentRoom.clear();
		for (Entity e : room.getAllEntities()) {
			if (Mappers.creepComponent.has(e)) allCreepsOfCurrentRoom.add(e);
		}
	}

}
