package com.dokkaebistudio.tacticaljourney.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.room.Room;

public abstract class NamedIteratingSystem extends IteratingSystem implements RoomSystem {
	
	protected Room room;
	
	protected abstract void performProcessEntity(Entity entity, float deltaTime);
	
	public NamedIteratingSystem(Family family) {
		super(family);
	}
	
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }
    
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if (room.getRestartSystem() != null) {
			if (!room.getRestartSystem().equals(this.getClass().getSimpleName())) {
				return;
			}

			room.setRestartSystem(null);
		}
		
		GameScreen.currentSystem = this.getClass().getSimpleName();

		performProcessEntity(entity, deltaTime);
	}

}
