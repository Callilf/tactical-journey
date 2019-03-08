package com.dokkaebistudio.tacticaljourney.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.InputSingleton;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.player.WheelModifierComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.wheel.AttackWheel;

/**
 * This system's role is to upadte the attack wheel, considering
 * all entities that can affect the wheel (items, stats, weapon type etc.).
 */
public class WheelSystem extends EntitySystem implements RoomSystem {

	/** The attack wheel. */
    private final AttackWheel wheel;
    
    /** The current room. */
    private Room room;
    private Entity player;

    public WheelSystem(AttackWheel attackWheel, Entity player, Room room) {
		this.priority = 6;

        this.wheel = attackWheel;
        this.player = player;
        this.room = room;
    }
    
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }

    @Override
    public void update(float deltaTime) {
    	
    	if (room.getState().isWheelDisplayed()) {
    		
    		switch(room.getState()) {
    		
    		case PLAYER_WHEEL_START:
    			wheel.setDisplayed(true);
    			RandomXS128 r = RandomSingleton.getInstance().getSeededRandom();
    			int nextInt = r.nextInt(360);
    			wheel.getArrow().setRotation(nextInt);
    			
    			
    			wheel.modifySectors(player, room);
    			
		
		        // get all entities that affect the wheel
		        ImmutableArray<Entity> modifiers = getEngine().getEntitiesFor(Family.all(WheelModifierComponent.class).get());
		        WheelModifierComponent modifier;
		        for (Entity e: modifiers) {
		            modifier = Mappers.wheelModifierComponentMapper.get(e);
		            // TODO modifiy the wheel
		            if (modifier.removeCriticalSectors) {
		                // TODO ...
		            }
		        }
		        
		        
		        room.setNextState(RoomState.PLAYER_WHEEL_TURNING);
    			
    			break;
    			
    			
    		case PLAYER_WHEEL_TURNING:
    			
    			if (InputSingleton.getInstance().leftClickJustPressed) {
	    			//Stop the spinning
	    			room.setNextState(RoomState.PLAYER_WHEEL_NEEDLE_STOP);
	    		} else {
	    			wheel.getArrow().setRotation(wheel.getArrow().getRotation() - 5.0f);
	    		}
    			
    			break;
    			
    			
    		case PLAYER_WHEEL_NEEDLE_STOP:
    			
    			if (InputSingleton.getInstance().leftClickJustReleased) {
	    			//Hide the wheel and perform the action
		        	wheel.setDisplayed(false);
		        	wheel.setPointedSector();
		        	room.setNextState(RoomState.PLAYER_WHEEL_FINISHED);    			
	    		} 
    			
    			
    		default:
    		
    		}
    		
    		
    	}
    	
    }
}
