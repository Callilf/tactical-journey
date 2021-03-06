package com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.creatures;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.dokkaebistudio.tacticaljourney.ces.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;

public class EnemySystem extends CreatureSystem implements RoomSystem {

	public EnemySystem(Room r, Stage stage) {
		super(r, stage);
	}
	
	@Override
	public boolean isStateRelevant() {
		return room.getState().isEnemyTurn();
	}
	
	
	@Override
	public boolean computeTilesToDisplayState() {
		return this.room.getState() == RoomState.ENEMY_COMPUTE_TILES_TO_DISPLAY_TO_PLAYER;
	}
	
	@Override
	public void finishComputeTilesToDisplay() {
		this.room.setNextState(RoomState.PLAYER_MOVE_TILES_DISPLAYED);
	}

	@Override
	public void fillEntitiesOfCurrentRoom() {
		allCreaturesOfCurrentRoom.clear();
		room.getEnemies().stream()
			.forEachOrdered(allCreaturesOfCurrentRoom::add);
	}
	
	@Override
	public void endTurn() {
		room.turnManager.endEnemyTurn();		
	}
    
	public static Entity getEnemyCurrentlyPlaying() {
		return CreatureSystem.creatureCurrentyPlaying;
	}
    
}
