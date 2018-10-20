package com.dokkaebistudio.tacticaljourney.systems;

import java.util.Random;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.AttackWheel;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.WheelComponent;
import com.dokkaebistudio.tacticaljourney.components.WheelComponent.Sector;
import com.dokkaebistudio.tacticaljourney.components.WheelModifierComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;

/**
 * This system's role is to upadte the attack wheel, considering
 * all entities that can affect the wheel (items, stats, weapon type etc.).
 */
public class WheelSystem extends EntitySystem {

    private final ComponentMapper<WheelModifierComponent> wheelModifierComponentMapper;
    private final ComponentMapper<WheelComponent> wheelComponentMapper;
    private final AttackWheel wheel;
    private final Room room;

    public WheelSystem(AttackWheel attackWheel, Room room) {
        // TODO get the real wheel from GameScreen
        this.wheelModifierComponentMapper = ComponentMapper.getFor(WheelModifierComponent.class);
        this.wheelComponentMapper = ComponentMapper.getFor(WheelComponent.class);
        this.wheel = attackWheel;
        this.room = room;
    }

    @Override
    public void update(float deltaTime) {
    	
    	if (room.state.isWheelDisplayed()) {
    		
    		switch(room.state) {
    		
    		case PLAYER_WHEEL_START:
    			wheel.setDisplayed(true);
    			RandomXS128 r = RandomSingleton.getInstance().getRandom();
    			int nextInt = r.nextInt(360);
    			wheel.getArrow().setRotation(nextInt);
    			
		        // get the entity that defines the wheel
		        Entity wheelEntity = getEngine().getEntitiesFor(Family.all(WheelComponent.class).get()).first();
		        WheelComponent wheelComponent = wheelComponentMapper.get(wheelEntity);
	
		        // init the real wheel
		        wheel.getSectors().clear();
		        wheel.getSectors().addAll(wheelComponent.sectors);
		
		        // get all entities that affect the wheel
		        ImmutableArray<Entity> modifiers = getEngine().getEntitiesFor(Family.all(WheelModifierComponent.class).get());
		        WheelModifierComponent modifier;
		        for (Entity e: modifiers) {
		            modifier = wheelModifierComponentMapper.get(e);
		            // TODO modifiy the wheel
		            if (modifier.removeCriticalSectors) {
		                // TODO ...
		            }
		        }
		        
		        
		        room.state = RoomState.PLAYER_WHEEL_TURNING;
    			
    			break;
    			
    			
    		case PLAYER_WHEEL_TURNING:
    			
    			if (InputSingleton.getInstance().leftClickJustPressed) {
	    			//Stop the spinning
	    			room.state = RoomState.PLAYER_WHEEL_NEEDLE_STOP;
	    		} else {
	    			wheel.getArrow().setRotation(wheel.getArrow().getRotation() - 5.0f);
	    		}
    			
    			break;
    			
    			
    		case PLAYER_WHEEL_NEEDLE_STOP:
    			
    			if (InputSingleton.getInstance().leftClickJustReleased) {
	    			//Hide the wheel and perform the action
		        	wheel.setDisplayed(false);
		        	wheel.setPointedSector();
		        	room.state = RoomState.PLAYER_WHEEL_FINISHED;    			
	    		} 
    			
    			
    		default:
    		
    		}
    		
    		
    	}
    	
    }
}
