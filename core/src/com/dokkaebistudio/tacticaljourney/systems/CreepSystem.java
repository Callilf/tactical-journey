package com.dokkaebistudio.tacticaljourney.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class CreepSystem extends IteratingSystem implements RoomSystem {
	    
	public GameScreen gameScreen;
	public Stage fxStage;
	
	/** The current room. */
    private Room room;    

    public CreepSystem(GameScreen gameScreen, Room r, Stage s) {
        super(Family.all(CreepComponent.class).get());
		this.priority = 21;

		this.gameScreen = gameScreen;
        this.room = r;
        this.fxStage = s;
    }
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }

    @Override
    protected void processEntity(Entity creep, float deltaTime) {
    	
    	if (room.getState() == RoomState.PLAYER_END_TURN) {
    	
	    	CreepComponent creepComponent = Mappers.creepComponent.get(creep);
	    
    		creepComponent.setCurrentDuration(creepComponent.getCurrentDuration() + 1);
	    	if (creepComponent.getCurrentDuration() >= creepComponent.getDuration()) {
	    		// Duration reached, remove the creep
	    		Image removeCreepImage = creepComponent.getRemoveCreepImage(creep);
	    		fxStage.addActor(removeCreepImage);
	    		
	    		room.removeEntity(creep);
	    	}
    
    	}
    }



}
