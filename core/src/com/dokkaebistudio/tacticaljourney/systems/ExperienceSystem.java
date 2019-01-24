package com.dokkaebistudio.tacticaljourney.systems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TransformComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.constants.PositionConstants;
import com.dokkaebistudio.tacticaljourney.factory.EntityFlagEnum;
import com.dokkaebistudio.tacticaljourney.leveling.LevelUpRewardChoice;
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
    private List<LevelUpRewardChoice> choiceButtons = new ArrayList<>();

    /** The state before the level up state. */
    private RoomState previousState;
    
    private LevelUpRewardChoice selectedChoice;
    
    /** The continue button. */
    private Entity continueBtn;

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
    	int choicesNumber = expCompo.getChoicesNumber();
    	if (expCompo.hasLeveledUp()) {
    		expCompo.setLeveledUp(false);
    		previousState = room.state;
    		room.state = RoomState.LEVEL_UP;
    		
    		
    		// Display notif
    		
    		Entity backgroundTop = room.entityFactory.createSprite(null, Assets.getTexture(Assets.lvl_up_background_top), EntityFlagEnum.LVL_UP_BACKGROUND, null);
    		TransformComponent backgroundTransfo = Mappers.transfoComponent.get(backgroundTop);
    		SpriteComponent backgroundSprite = Mappers.spriteComponent.get(backgroundTop);
    		backgroundTransfo.pos.set(PositionConstants.POS_LVL_UP_BACKGROUND.x - backgroundSprite.getSprite().getWidth()/2,
    				PositionConstants.POS_LVL_UP_BACKGROUND.y,
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
    		
    		Entity backgroundBottom = room.entityFactory.createSprite(null, Assets.getTexture(Assets.lvl_up_background_bottom), EntityFlagEnum.LVL_UP_BACKGROUND, null);
    		TransformComponent backgroundBottomTransfo = Mappers.transfoComponent.get(backgroundBottom);
    		SpriteComponent backgroundBottomSprite = Mappers.spriteComponent.get(backgroundBottom);
    		backgroundBottomTransfo.pos.set(PositionConstants.POS_LVL_UP_BACKGROUND.x - backgroundBottomSprite.getSprite().getWidth()/2,
    				PositionConstants.POS_LVL_UP_BACKGROUND.y - (88 * choicesNumber) - 63 + choicesNumber + 1,
    				PositionConstants.Z_LVL_UP_BACKGROUND);
    		
    		continueBtn = room.entityFactory.createSprite(null, Assets.getTexture(Assets.lvl_up_continue_btn), EntityFlagEnum.LVL_UP_BACKGROUND, null);
    		TransformComponent continueBtnTransfo = Mappers.transfoComponent.get(continueBtn);
    		SpriteComponent continueBtnSprite = Mappers.spriteComponent.get(continueBtn);
    		continueBtnTransfo.pos.set(PositionConstants.POS_LVL_UP_BACKGROUND.x - continueBtnSprite.getSprite().getWidth()/2,
    				PositionConstants.POS_LVL_UP_BACKGROUND.y - (88 * choicesNumber) - 63 + 10 +  choicesNumber + 1,
    				PositionConstants.Z_LVL_UP_BACKGROUND + 1);
    		
    		levelUpEntities.add(backgroundTop);
    		levelUpEntities.add(backgroundBottom);
    		levelUpEntities.add(title);
    		levelUpEntities.add(subtitle);
    		levelUpEntities.add(continueBtn);

    		
    		
    		List<LevelUpRewardEnum> list = new ArrayList<>(Arrays.asList(LevelUpRewardEnum.values()));
    		RandomXS128 unseededRandom = RandomSingleton.getInstance().getUnseededRandom();
    		
    		
    		// Choices
    		for (int i=1; i<=choicesNumber ; i++) {
	    		
	    		int nextInt = unseededRandom.nextInt(list.size());
	    		LevelUpRewardEnum reward = list.get(nextInt);
	    		list.remove(nextInt);
	
	    		Vector2 choicePos = new Vector2(PositionConstants.POS_LVL_UP_BACKGROUND.x - backgroundSprite.getSprite().getWidth()/2, PositionConstants.POS_LVL_UP_BACKGROUND.y - (88 * i) + i);
	    		LevelUpRewardChoice choice = new LevelUpRewardChoice(choicePos, 
	    				reward, 
	    				i,
	    				room.engine, 
	    				room.entityFactory);
	    		
	    		choiceButtons.add(choice);
    		}

    		
    		
    	}
    	
    	
    	if (room.state == RoomState.LEVEL_UP) {
    		// Wait for player
    		if (InputSingleton.getInstance().leftClickJustPressed) {
    			Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
    			int x = (int) touchPoint.x;
            	int y = (int) touchPoint.y;
            	
            	// Check if a button was selected
            	for (LevelUpRewardChoice choice : choiceButtons) {
	            	if (choice.containsPoint(x, y)) {
	            		choice.pushClaimBtn();
	            		selectedChoice = choice;
	            	}
            	}
            	
    		} else if (InputSingleton.getInstance().leftClickJustReleased) {
    			
    			Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
    			int x = (int) touchPoint.x;
            	int y = (int) touchPoint.y;
            	
            	if (selectedChoice != null && selectedChoice.containsPoint(x, y)) {
            		room.state = RoomState.LEVEL_UP_ANIM;
            		for (LevelUpRewardChoice choice : choiceButtons) {
            			if (choice != selectedChoice) {
            				choice.deactivateClaimBtn();
            			}
            		}
            		
            		
            	} else if (selectedChoice != null){
            		selectedChoice.releaseClaimBtn();
            		selectedChoice = null;
            	}
            	
    		}
    	} else if (room.state == RoomState.LEVEL_UP_ANIM) {
    		
    		boolean opened = selectedChoice.openUpChoice();
    		
    		if (opened) {
	    		// Choice selected, apply it and close the notification popin
	    		LevelUpRewardEnum levelUpRewardEnum = selectedChoice.getLevelUpRewardEnum();
	    		levelUpRewardEnum.select(player);
	    		
    			room.state = RoomState.LEVEL_UP_WAIT;
    		}
    		
    	} 
    	
    	
    	if (room.state == RoomState.LEVEL_UP || room.state == RoomState.LEVEL_UP_ANIM || room.state == RoomState.LEVEL_UP_WAIT) {
	    	// Handle continue button
			if (InputSingleton.getInstance().leftClickJustPressed) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
	        	int y = (int) touchPoint.y;
	        	
	        	SpriteComponent continueBtnSpriteComponent = Mappers.spriteComponent.get(continueBtn);
	        	if (continueBtnSpriteComponent.containsPoint(x, y)) {
	        		continueBtnSpriteComponent.setSprite( new Sprite(Assets.getTexture(Assets.lvl_up_continue_btn_pushed)));
	        	}
			} else if (InputSingleton.getInstance().leftClickJustReleased) {
				Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
				int x = (int) touchPoint.x;
	        	int y = (int) touchPoint.y;
	        	
	        	SpriteComponent continueBtnSpriteComponent = Mappers.spriteComponent.get(continueBtn);
	        	if (continueBtnSpriteComponent.containsPoint(x, y)) {
	
		    		// Clear all entities of the popin
		    		for (Entity e : levelUpEntities) {
		    			room.engine.removeEntity(e);
		    		}
		    		levelUpEntities.clear();
		    		for (LevelUpRewardChoice e : choiceButtons) {
		    			e.clear();
		    		}
		    		choiceButtons.clear();
		    		
		    		
		    		// Restore the previous state
		    		room.state = previousState;
		    		previousState = null;
		    		selectedChoice = null;
		    		
	        	} else {
	        		continueBtnSpriteComponent.setSprite( new Sprite(Assets.getTexture(Assets.lvl_up_continue_btn)));
	        	}
			}
    	}
    
    }

}
