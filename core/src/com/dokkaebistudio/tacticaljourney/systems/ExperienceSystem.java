package com.dokkaebistudio.tacticaljourney.systems;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TransformComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.constants.PositionConstants;
import com.dokkaebistudio.tacticaljourney.factory.EntityFlagEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class ExperienceSystem extends IteratingSystem implements RoomSystem {
	    
	/** The current room. */
    private Room room;
    
    /** The list of entities used to display the level up notification. */
    private List<Entity> levelUpEntities = new ArrayList<>();
    
    /** The button to close th levelup page. */
    private Entity okButton;

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
    protected void processEntity(Entity entity, float deltaTime) {
    	
    	ExperienceComponent expCompo = Mappers.experienceComponent.get(entity);
    	if (expCompo.hasLeveledUp()) {
    		expCompo.setLeveledUp(false);
    		previousState = room.state;
    		room.state = RoomState.LEVEL_UP;
    		
    		// Improve stats
    		HealthComponent healthCompo = Mappers.healthComponent.get(entity);
    		healthCompo.increaseMaxHealth(10);

    		AttackComponent attackComponent = Mappers.attackComponent.get(entity);
    		attackComponent.setStrength(attackComponent.getStrength() + 1);


    		
    		
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
    		
    		Entity statsUp = room.entityFactory.createText("Health increased by 10" + "\n" + "Strength increased by 1");
    		TransformComponent statsUpTransfo = Mappers.transfoComponent.get(statsUp);
    		TextComponent statsUpText = Mappers.textComponent.get(statsUp);
    		statsUpTransfo.pos.set(PositionConstants.POS_LVL_UP_STATS.x - statsUpText.getWidth()/2,
    				PositionConstants.POS_LVL_UP_STATS.y, 
    				PositionConstants.Z_LVL_UP_STATS);

    		okButton = room.entityFactory.createOkButton();
    		TransformComponent okTransfo = Mappers.transfoComponent.get(okButton);
    		SpriteComponent okSprite = Mappers.spriteComponent.get(okButton);
    		okTransfo.pos.set(PositionConstants.POS_LVL_UP_BTN.x - okSprite.getSprite().getWidth()/2,
    				PositionConstants.POS_LVL_UP_BTN.y, 
    				PositionConstants.Z_LVL_UP_BTN);
    		
    		levelUpEntities.add(background);
    		levelUpEntities.add(title);
    		levelUpEntities.add(subtitle);
    		levelUpEntities.add(statsUp);
    		levelUpEntities.add(okButton);
    	}
    	
    	
    	if (room.state == RoomState.LEVEL_UP) {
    		// Wait for player
    		if (InputSingleton.getInstance().leftClickJustReleased) {
    			Vector3 touchPoint = InputSingleton.getInstance().getTouchPoint();
    			int x = (int) touchPoint.x;
            	int y = (int) touchPoint.y;
            	
            	//Close the notification
        		SpriteComponent okSprite = Mappers.spriteComponent.get(okButton);
            	if (okSprite.containsPoint(x, y)) {
            		for (Entity e : levelUpEntities) {
            			room.engine.removeEntity(e);
            		}
            		okButton = null;
            		
            		room.state = previousState;
            		previousState = null;
            	}
            	
    		}
    	}
    }

}
