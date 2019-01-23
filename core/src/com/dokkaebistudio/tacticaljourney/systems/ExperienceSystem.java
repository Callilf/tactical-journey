package com.dokkaebistudio.tacticaljourney.systems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TransformComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.constants.PositionConstants;
import com.dokkaebistudio.tacticaljourney.factory.EntityFlagEnum;
import com.dokkaebistudio.tacticaljourney.leveling.LevelUpRewardEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class ExperienceSystem extends IteratingSystem implements RoomSystem {
	    
	/** The current room. */
    private Room room;
    
    /** The list of entities used to display the level up notification. */
    private List<Entity> levelUpEntities = new ArrayList<>();
    
//    /** The button to close th levelup page. */
//    private Entity okButton;
    
    /** The buttons to select the level up reward. */
    private Map<Entity, LevelUpRewardEnum> choiceButtons = new HashMap<>();

    /** The state before the level up state. */
    private RoomState previousState;

    public ExperienceSystem(Room r) {
        super(Family.all(ExperienceComponent.class).get());
        room = r;
    }
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }

    @Override
    protected void processEntity(Entity player, float deltaTime) {
    	
    	ExperienceComponent expCompo = Mappers.experienceComponent.get(player);
    	if (expCompo.hasLeveledUp()) {
    		expCompo.setLeveledUp(false);
    		previousState = room.state;
    		room.state = RoomState.LEVEL_UP;
    		
    		
    		// Display notif
    		
    		Entity background = room.entityFactory.createSprite(null, Assets.getTexture(Assets.lvl_up_background), EntityFlagEnum.LVL_UP_BACKGROUND, null);
    		TransformComponent backgroundTransfo = Mappers.transfoComponent.get(background);
    		SpriteComponent backgroundSprite = Mappers.spriteComponent.get(background);
    		backgroundTransfo.pos.set(PositionConstants.POS_LVL_UP_BACKGROUND.x - backgroundSprite.getSprite().getWidth()/2,
    				PositionConstants.POS_LVL_UP_BACKGROUND.y - backgroundSprite.getSprite().getHeight()/2,
    				PositionConstants.Z_LVL_UP_BACKGROUND);
    		
    		Entity title = room.entityFactory.createText("LEVEL UP");
    		TransformComponent titleTransfo = Mappers.transfoComponent.get(title);
    		TextComponent titleText = Mappers.textComponent.get(title);
    		titleTransfo.pos.set(PositionConstants.POS_LVL_UP_TITLE.x - titleText.getWidth()/2, 
    				PositionConstants.POS_LVL_UP_TITLE.y, 
    				PositionConstants.Z_LVL_UP_TITLE); 
    		
    		Entity subtitle = room.entityFactory.createText("Congratulations,\n you reached level " + expCompo.getLevel());
    		TransformComponent subtitleTransfo = Mappers.transfoComponent.get(subtitle);
    		TextComponent subtitleText = Mappers.textComponent.get(subtitle);
    		subtitleTransfo.pos.set(PositionConstants.POS_LVL_UP_SUBTITLE.x - subtitleText.getWidth()/2,
    				PositionConstants.POS_LVL_UP_SUBTITLE.y,
    				PositionConstants.Z_LVL_UP_SUBTITLE);
    		
//    		Entity statsUp = room.entityFactory.createText("Health increased by 10" + "\n" + "Strength increased by 1");
//    		TransformComponent statsUpTransfo = Mappers.transfoComponent.get(statsUp);
//    		TextComponent statsUpText = Mappers.textComponent.get(statsUp);
//    		statsUpTransfo.pos.set(PositionConstants.POS_LVL_UP_STATS.x - statsUpText.getWidth()/2,
//    				PositionConstants.POS_LVL_UP_STATS.y, 
//    				PositionConstants.Z_LVL_UP_STATS);
    		
    		levelUpEntities.add(background);
    		levelUpEntities.add(title);
    		levelUpEntities.add(subtitle);


    		Entity choice1Button = room.entityFactory.createLevelUpRewardButton(LevelUpRewardEnum.HEALTH_UP.getDescription(), PositionConstants.POS_LVL_UP_BTN_1);
    		choiceButtons.put(choice1Button, LevelUpRewardEnum.HEALTH_UP);
    		
    		Entity choice2Button = room.entityFactory.createLevelUpRewardButton(LevelUpRewardEnum.STRENGTH_UP.getDescription(), PositionConstants.POS_LVL_UP_BTN_2);
    		choiceButtons.put(choice2Button, LevelUpRewardEnum.STRENGTH_UP);

    		Entity choice3Button = room.entityFactory.createLevelUpRewardButton(LevelUpRewardEnum.MOVEMENT_UP.getDescription(), PositionConstants.POS_LVL_UP_BTN_3);
    		choiceButtons.put(choice3Button, LevelUpRewardEnum.MOVEMENT_UP);
    		
    		
    		
    	}
    	
    	
    	if (room.state == RoomState.LEVEL_UP) {
    		// Wait for player
    		if (InputSingleton.getInstance().leftClickJustReleased) {
    			Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
    			int x = (int) touchPoint.x;
            	int y = (int) touchPoint.y;
            	
            	// Check if a button was selected
            	Entity selectedButton = null;
            	for (Entity button : choiceButtons.keySet()) {
	        		SpriteComponent okSprite = Mappers.spriteComponent.get(button);
	            	if (okSprite.containsPoint(x, y)) {
	            		selectedButton = button;
	            	}
            	}
            	
            	
            	if (selectedButton != null) {
            		// Choice selected, apply it and close the notification popin
            		LevelUpRewardEnum levelUpRewardEnum = choiceButtons.get(selectedButton);
            		levelUpRewardEnum.select(player);
            		
            		// Clear all entities of the popin
            		for (Entity e : levelUpEntities) {
            			room.engine.removeEntity(e);
            		}
            		levelUpEntities.clear();
            		for (Entity e : choiceButtons.keySet()) {
            			room.engine.removeEntity(e);
            		}
            		choiceButtons.clear();
            		
            		
            		// Restore the previous state
            		room.state = previousState;
            		previousState = null;
            	}
            	
            	
    		}
    	}
    }

}
