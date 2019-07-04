package com.dokkaebistudio.tacticaljourney.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.player.WheelModifierComponent;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.singletons.InputSingleton;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffStunned;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.wheel.AttackWheel;

/**
 * This system's role is to update the attack wheel, considering
 * all entities that can affect the wheel (items, stats, weapon type etc.).
 */
public class WheelSystem extends EntitySystem implements RoomSystem {

//	public static boolean attackButtonPressed;
//	public static boolean attackButtonReleased;

	
	/** The attack wheel. */
    private final AttackWheel wheel;
    
    /** The current room. */
    private Room room;

    public WheelSystem(AttackWheel attackWheel, Room room) {
		this.priority = 6;

        this.wheel = attackWheel;
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
    			RandomXS128 r = RandomSingleton.getInstance().getUnseededRandom();
    			int nextInt = r.nextInt(360);
    			wheel.getArrow().setRotation(nextInt);
    			
    			
    			wheel.modifySectors(GameScreen.player, room);
    			
		
		        // get all entities that affect the wheel
		        ImmutableArray<Entity> modifiers = getEngine().getEntitiesFor(Family.all(WheelModifierComponent.class).get());
		        WheelModifierComponent modifier;
		        for (Entity e: modifiers) {
		            modifier = Mappers.wheelModifierComponent.get(e);
		            // TODO modifiy the wheel
		            if (modifier.removeCriticalSectors) {
		                // TODO ...
		            }
		        }
		        
		        
		        // Compute current accuracy
    			int accuracy = wheel.getAttackComponent().getAccuracy();
    			Entity target = wheel.getAttackComponent().getTarget();
    			if (target != null) {
    				MoveComponent moveComponent = Mappers.moveComponent.get(target);
    				if (moveComponent != null && moveComponent.isFrozen()) {
    					// Frozen target, increase accuracy
    					accuracy += 2;
    				}
    				StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(target);
    				if (statusReceiverComponent != null && statusReceiverComponent.hasStatus(StatusDebuffStunned.class)) {
    					accuracy += 2;
    				}
    			}
		        wheel.setCurrentAccuracy(accuracy);
		        
		        room.setNextState(RoomState.PLAYER_WHEEL_TURNING);
    			
    			break;
    			
    			
    		case PLAYER_WHEEL_TURNING:
    			
    			if (InputSingleton.getInstance().leftClickJustPressed) {
//    				attackButtonPressed = false;
    				
	    			//Stop the spinning
	    			room.setNextState(RoomState.PLAYER_WHEEL_NEEDLE_STOP);
	    		} else {
	    			
	    			// Make the arrow spin	    			
	    			wheel.getArrow().setRotation(wheel.getArrow().getRotation() - 5.25f + (wheel.getCurrentAccuracy() * 0.25f));
	    		}
    			
    			break;
    			
    			
    		case PLAYER_WHEEL_NEEDLE_STOP:
    			
    			if (InputSingleton.getInstance().leftClickJustReleased) {
//    				attackButtonReleased = false;
    				
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
