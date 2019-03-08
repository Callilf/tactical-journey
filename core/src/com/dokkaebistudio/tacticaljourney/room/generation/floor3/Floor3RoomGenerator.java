/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation.floor3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.generation.GeneratedRoom;
import com.dokkaebistudio.tacticaljourney.room.generation.RoomGenerator;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;

/**
 * @author Callil
 *
 */
public class Floor3RoomGenerator extends RoomGenerator {

	public Floor3RoomGenerator(EntityFactory ef) {
		super(ef);
	}
	
	
	@Override
	public void generateRoomContent(Room room, GeneratedRoom generatedRoom) {
		RandomXS128 random = RandomSingleton.getInstance().getSeededRandom();

		List<PoolableVector2> possibleSpawns = generatedRoom.getPossibleSpawns();
		List<PoolableVector2> spawnPositions = null;
		
		switch(room.type) {
		case COMMON_ENEMY_ROOM :
		case SHOP_ROOM:
		case STATUE_ROOM:
		case ITEM_ROOM:
			super.generateRoomContent(room, generatedRoom);
			return;
			
		case KEY_ROOM:
			
			// TODO change this
			// No key atm, so that its impossible to go deeper
			
			break;
			
		case START_FLOOR_ROOM:
			
			break;
		case END_FLOOR_ROOM:
			if (possibleSpawns.size() == 0) return;
			int nextInt = random.nextInt(possibleSpawns.size());
			Vector2 pos = possibleSpawns.get(nextInt);
			entityFactory.createExit(room, pos, false);
			default:
			break;
		}
		
		placeDestructibles(room, random, generatedRoom.getPossibleDestr());
	
	
		// Release poolable vector2
		generatedRoom.releaseSpawns();
	}

	
	/**
	 * Generate a random number of enemies and place them in a room.
	 * @param room the room
	 * @param random the random
	 * @param spawnPositions the possible spawn positions
	 * @param canBeEmpty true if there can be no enemies
	 */
	protected void placeEnemies(Room room, RandomXS128 random, List<PoolableVector2> spawnPositions, boolean canBeEmpty) {
		if (spawnPositions.size() == 0) return;

		int enemyNb = random.nextInt(Math.min(spawnPositions.size(), 8));
		if (enemyNb == 0 && !canBeEmpty) enemyNb = 1;
		
		Iterator<PoolableVector2> iterator = spawnPositions.iterator();
		for (int i=0 ; i<enemyNb ; i++) {
			if (!iterator.hasNext()) break;
			
			Entity enemy = null;
			int enemyTypeRandom = random.nextInt(9);
			if (enemyTypeRandom == 0) {
				int spiderType = random.nextInt(2);
				if (spiderType == 0) {
					enemy = entityFactory.enemyFactory.createSpider(room, new Vector2(iterator.next()));
				} else {
					enemy = entityFactory.enemyFactory.createVenomSpider(room, new Vector2(iterator.next()));
				}
				iterator.remove();
			} else if (enemyTypeRandom == 1 || enemyTypeRandom == 2) {
				enemy = entityFactory.enemyFactory.createSpiderWeb(room, new Vector2(iterator.next()));
				iterator.remove();
				if (iterator.hasNext()) {
					int spiderType = random.nextInt(2);
					if (spiderType == 0) {
						enemy = entityFactory.enemyFactory.createSpider(room, new Vector2(iterator.next()));
					} else {
						enemy = entityFactory.enemyFactory.createVenomSpider(room, new Vector2(iterator.next()));
					}
					iterator.remove();
				}
			} else if (enemyTypeRandom == 3 || enemyTypeRandom == 4) {
				enemy = entityFactory.enemyFactory.createStinger(room, new Vector2(iterator.next()));
				iterator.remove();
			} else if (enemyTypeRandom == 5 || enemyTypeRandom == 6) {
				enemy = entityFactory.enemyFactory.createPangolinBaby(room, new Vector2(iterator.next()), null);
				iterator.remove();
			} else {
				enemy = entityFactory.enemyFactory.createScorpion(room, new Vector2(iterator.next()));
				iterator.remove();
			}
			
			LootRewardComponent lootRewardComponent = Mappers.lootRewardComponent.get(enemy);
			lootRewardComponent.setDrop( generateEnemyLoot(lootRewardComponent.getItemPool(), lootRewardComponent.getDropRate()));
		}
	}
}
