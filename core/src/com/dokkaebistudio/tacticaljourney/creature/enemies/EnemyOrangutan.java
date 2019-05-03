package com.dokkaebistudio.tacticaljourney.creature.enemies;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.creature.Creature;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.creatures.subsystems.OrangutanSubSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;

public class EnemyOrangutan extends Creature {
	
	private boolean sleeping = true;

	@Override
	public String title() {
		return "Orangutan alpha male";
	}
	
	
	@Override
	public void onRoomVisited(Entity enemy, Room room) {
		// Place the enemy so that it is far enough from the player
		GridPositionComponent playerPos = Mappers.gridPositionComponent.get(GameScreen.player);
		if (playerPos.coord().x < 11) {
			MovementHandler.placeEntity(enemy, OrangutanSubSystem.RIGHT_CLONE_TILE, room);
		} else if (playerPos.coord().x > 11) {
			MovementHandler.placeEntity(enemy, OrangutanSubSystem.LEFT_CLONE_TILE, room);
		} else if (playerPos.coord().y < 6) {
			MovementHandler.placeEntity(enemy, OrangutanSubSystem.UP_CLONE_TILE, room);
		} else if (playerPos.coord().y > 6) {
			MovementHandler.placeEntity(enemy, OrangutanSubSystem.DOWN_CLONE_TILE, room);
		}
		
		// Orient the sprite towards the player
		Mappers.spriteComponent.get(enemy).orientSprite(enemy, playerPos.coord());
	}
	

	
	
	
	@Override
	public void onAlerted(Entity enemy, Entity target, Room room) {
		sleeping = false;
	}


	public boolean isSleeping() {
		return sleeping;
	}



	public void setSleeping(boolean sleeping) {
		this.sleeping = sleeping;
	}
}
