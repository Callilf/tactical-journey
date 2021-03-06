package com.dokkaebistudio.tacticaljourney.ces.systems.iteratingsystems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.ces.systems.NamedIteratingSystem;
import com.dokkaebistudio.tacticaljourney.rendering.HUDRenderer;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.vfx.VFXUtil;

public class ExperienceSystem extends NamedIteratingSystem {
	    
	public Stage stage;
	
    public ExperienceSystem(Room r, Stage s) {
        super(Family.all(ExperienceComponent.class).get());
		this.priority = 21;
        room = r;
        this.stage = s;
    }
    

    @Override
    protected void performProcessEntity(final Entity player, float deltaTime) {
    	
    	ExperienceComponent expCompo = Mappers.experienceComponent.get(player);
    	
    	// Display experience gained
    	if (!expCompo.getXpGainedAtCurrentFrame().isEmpty()) {
    		
    		for (int i=0 ; i<expCompo.getXpGainedAtCurrentFrame().size() ; i++) {
    			Integer expGain = expCompo.getXpGainedAtCurrentFrame().get(i);
				GridPositionComponent attackerPosCompo = Mappers.gridPositionComponent.get(player);
				VFXUtil.createExperienceDisplayer(expGain, attackerPosCompo.coord(), 20 * i, room);
    		}
    		
    		expCompo.getXpGainedAtCurrentFrame().clear();
    	}
    	
    	// Display level up popin
    	if (expCompo.getNumberOfNewLevelReached() > 0 && !HUDRenderer.levelUpButtonDisplayed && !expCompo.isLevelUpPopinDisplayed()) {
    		VFXUtil.createStatsUpNotif("LEVEL UP", Mappers.gridPositionComponent.get(player).coord());
    		HUDRenderer.displayLevelUpButton = true;
    	}
    	    
    }

}
