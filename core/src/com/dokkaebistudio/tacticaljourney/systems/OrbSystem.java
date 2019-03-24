package com.dokkaebistudio.tacticaljourney.systems;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.OrbUtil;

public class OrbSystem extends EntitySystem implements RoomSystem {
	
	private Stage fxStage;
	private Room room;
	private Entity player;
	
    /** The orbs of the current room that need updating. */
    private List<Entity> allOrbsOfCurrentRoom;

		
	public OrbSystem(Entity player, Room r, Stage stage) {
		this.priority = 21;

		this.fxStage = stage;
		this.player = player;
		this.room = r;
		
		this.allOrbsOfCurrentRoom = new ArrayList<>();
	}
	
	@Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }
    
	
	@Override	
	public void update(float deltaTime) {
		
		fillEntitiesOfCurrentRoom();		
		for(Entity orb : allOrbsOfCurrentRoom) {
			
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(orb);
			if (gridPositionComponent.hasAbsolutePos()) continue;
			
			OrbUtil.checkContact(orb, room);
		}

	}
	
	
	private void fillEntitiesOfCurrentRoom() {
		allOrbsOfCurrentRoom.clear();
		for (Entity e : room.getAllEntities()) {
			if (e != null && Mappers.orbComponent.has(e)) allOrbsOfCurrentRoom.add(e);
		}
	}

}
