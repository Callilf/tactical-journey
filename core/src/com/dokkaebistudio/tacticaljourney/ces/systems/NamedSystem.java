package com.dokkaebistudio.tacticaljourney.ces.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.room.Room;

public abstract class NamedSystem extends EntitySystem implements RoomSystem {
	
	protected Room room;
	
	protected abstract void performUpdate(float deltaTime);
	
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }
	
	@Override
	public void update(float deltaTime) {
		if (room.isPauseState()) {
			return;
		}
		
		if (!room.isPauseState() && room.getRestartSystem() != null) {
			if (!room.getRestartSystem().equals(this.getClass().getSimpleName())) {
				return;
			}

			room.setRestartSystem(null);
		}
		
		GameScreen.currentSystem = this.getClass().getSimpleName();

		performUpdate(deltaTime);
	}
}
