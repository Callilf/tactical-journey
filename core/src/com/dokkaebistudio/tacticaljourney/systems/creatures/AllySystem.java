package com.dokkaebistudio.tacticaljourney.systems.creatures;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;

public class AllySystem extends CreatureSystem implements RoomSystem {


	public AllySystem(Room r, Stage stage) {
		super(r, stage);
	}

	@Override
	public boolean isStateRelevant() {
		return room.getState().isAllyTurn();
	}
	
	@Override
	public boolean computeTilesToDisplayState() {
		return this.room.getState() == RoomState.ALLY_COMPUTE_TILES_TO_DISPLAY_TO_PLAYER;
	}
	
	@Override
	public void finishComputeTilesToDisplay() {
		this.room.setNextState(RoomState.ENEMY_COMPUTE_TILES_TO_DISPLAY_TO_PLAYER);
	}
	
	@Override
	public void fillEntitiesOfCurrentRoom() {
		allCreaturesOfCurrentRoom.clear();
		room.getAllies().stream()
			.filter(e -> e != GameScreen.player)
			.forEachOrdered(allCreaturesOfCurrentRoom::add);
	}
	
	@Override
	public void endTurn() {
		room.turnManager.endAllyTurn();		
	}
	
	public static Entity getAllyCurrentlyPlaying() {
		return CreatureSystem.creatureCurrentyPlaying;
	}
    
}
