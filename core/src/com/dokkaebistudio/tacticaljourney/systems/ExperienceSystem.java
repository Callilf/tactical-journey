package com.dokkaebistudio.tacticaljourney.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class ExperienceSystem extends IteratingSystem implements RoomSystem {
	    
	public Stage stage;
	
	/** The current room. */
    private Room room;
    
    //TODO remove this when no longer testing the levelup popin
    private boolean test = true;

    public ExperienceSystem(Room r, Stage s) {
        super(Family.all(ExperienceComponent.class).get());
		this.priority = 21;

        room = r;
        this.stage = s;
    }
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }

    @Override
    protected void processEntity(final Entity player, float deltaTime) {
    	
    	ExperienceComponent expCompo = Mappers.experienceComponent.get(player);
    	
    	//TODO remove
    	if (test) {
    		test = false;
    		expCompo.earnXp(expCompo.getNextLevelXp());
    	}
    	
    	// Display experience gained
    	if (!expCompo.getXpGainedAtCurrentFrame().isEmpty()) {
    		
    		for (int i=0 ; i<expCompo.getXpGainedAtCurrentFrame().size() ; i++) {
    			Integer expGain = expCompo.getXpGainedAtCurrentFrame().get(i);
				GridPositionComponent attackerPosCompo = Mappers.gridPositionComponent.get(player);
				room.entityFactory.createExpDisplayer(expGain, attackerPosCompo.coord(), 15 * i, room);

    		}
    		
    		expCompo.getXpGainedAtCurrentFrame().clear();
    	}
    	
    	// Display level up popin
    	if (expCompo.getNumberOfNewLevelReached() > 0 && !expCompo.isLevelUpPopinDisplayed()) {
    		expCompo.setLevelUpPopinDisplayed(true);
    	}
    	    
    }

}
