package com.dokkaebistudio.tacticaljourney.creature.enemies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.creature.Creature;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.systems.creatures.subsystems.OrangutanSubSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class EnemyOrangutan extends Creature {
	
	private boolean sleeping = true;
	private RandomXS128 random;
	
	public EnemyOrangutan() {}
	
	public EnemyOrangutan(RandomXS128 random) {
		this.random = random;
	}

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
		
		List<Tile> freeTiles = new ArrayList<>();
		List<Tile> allTiles = TileUtil.getAllTiles(room);
		allTiles.stream().filter(tile -> tile.isWalkable()).forEachOrdered(freeTiles::add);
		
		Collections.shuffle(freeTiles, random);
		
		for (int i=0 ; i<10 ; i++) {
			room.entityFactory.creepFactory.createBananaPeel(room, freeTiles.get(i).getGridPos(), enemy);
		}
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


	public RandomXS128 getRandom() {
		return random;
	}


	public void setRandom(RandomXS128 random) {
		this.random = random;
	}
}
