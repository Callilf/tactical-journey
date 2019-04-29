package com.dokkaebistudio.tacticaljourney.systems;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent.CreepReleasedTurnEnum;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

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
    	
    	if (room.getState() == RoomState.PLAYER_END_TURN) {
    		// Handle player stop position
    		GridPositionComponent playerPos = Mappers.gridPositionComponent.get(GameScreen.player);
    		Set<Entity> creeps = TileUtil.getEntitiesWithComponentOnTile(playerPos.coord(), CreepComponent.class, room);
    		for (Entity creep : creeps) {
    			Mappers.creepComponent.get(creep).onStop(GameScreen.player, creep, room);
    		}
    		
        	// onEndTurn for enemy creep
        	fillCreepsOfCurrentRoom(CreepReleasedTurnEnum.PLAYER);
        	for (Entity creep : allCreepsOfCurrentRoom) {
		    	CreepComponent creepComponent = Mappers.creepComponent.get(creep);
		    	
		    	creepComponent.onEndTurn(creep, room);
        	}
        	
        	
        	// Handle creep duration
        	fillCreepsOfCurrentRoom(CreepReleasedTurnEnum.PLAYER);
        	for (Entity creep : allCreepsOfCurrentRoom) {
		    	handleDuration(creep);
        	}
    	}
    	
    	
    	if (room.getState() == RoomState.ALLY_END_TURN) {
    		// Handle enemy stop positions
    		for (Entity ally : room.getAllies()) {
    			if (ally == GameScreen.player) continue;
	    		GridPositionComponent enemyPos = Mappers.gridPositionComponent.get(ally);
	    		Set<Entity> creeps = TileUtil.getEntitiesWithComponentOnTile(enemyPos.coord(), CreepComponent.class, room);
	    		for (Entity creep : creeps) {
	    			Mappers.creepComponent.get(creep).onStop(ally, creep, room);
	    		}
    		}
    		
        	// onEndTurn for enemy creep
        	fillCreepsOfCurrentRoom(CreepReleasedTurnEnum.ALLY);
        	for (Entity creep : allCreepsOfCurrentRoom) {
		    	CreepComponent creepComponent = Mappers.creepComponent.get(creep);
		    	
		    	creepComponent.onEndTurn(creep, room);
        	}
        	
        	// Handle creep duration
        	fillCreepsOfCurrentRoom(CreepReleasedTurnEnum.ALLY);
        	for (Entity creep : allCreepsOfCurrentRoom) {
		    	handleDuration(creep);
        	}
    	}
    	
    	
    	if (room.getState() == RoomState.ENEMY_END_TURN) {
    		// Handle enemy stop positions
    		for (Entity enemy : room.getEnemies()) {
	    		GridPositionComponent enemyPos = Mappers.gridPositionComponent.get(enemy);
	    		Set<Entity> creeps = TileUtil.getEntitiesWithComponentOnTile(enemyPos.coord(), CreepComponent.class, room);
	    		for (Entity creep : creeps) {
	    			Mappers.creepComponent.get(creep).onStop(enemy, creep, room);
	    		}
    		}
    		
        	// onEndTurn for enemy creep
        	fillCreepsOfCurrentRoom(CreepReleasedTurnEnum.ENEMY);
        	for (Entity creep : allCreepsOfCurrentRoom) {
		    	CreepComponent creepComponent = Mappers.creepComponent.get(creep);
		    	
		    	creepComponent.onEndTurn(creep, room);
        	}
        	
        	// Handle creep duration
        	fillCreepsOfCurrentRoom(CreepReleasedTurnEnum.ENEMY);
        	for (Entity creep : allCreepsOfCurrentRoom) {
		    	handleDuration(creep);
        	}
    	}
    }

    /**
     * Handle creep duration. Add one to the number of turns the creep has been alive and make it
     * disappear if it has reached its max turns.
     * @param creep the creep
     */
	private void handleDuration(Entity creep) {
		CreepComponent creepComponent = Mappers.creepComponent.get(creep);

		creepComponent.setCurrentDuration(creepComponent.getCurrentDuration() + 1);
		if (creepComponent.getDuration() > 0 && creepComponent.getCurrentDuration() >= creepComponent.getDuration()) {
			// Duration reached, remove the creep
			Image removeCreepImage = creepComponent.getRemoveCreepImage(creep);
			fxStage.addActor(removeCreepImage);
			
			creepComponent.onDisappear(creep, room);
			
			room.removeEntity(creep);
		}
	}

    
    
    
    //*****************
    // Utils methods

	private void fillCreepsOfCurrentRoom(CreepReleasedTurnEnum releasedTurn) {
		allCreepsOfCurrentRoom.clear();
		for (Entity e : room.getAllEntities()) {
			if (Mappers.creepComponent.has(e)) {
				CreepComponent creepComponent = Mappers.creepComponent.get(e);
				if (creepComponent.getReleasedTurn() == releasedTurn) {
					allCreepsOfCurrentRoom.add(e);
				}
			}
		}
	}

}
