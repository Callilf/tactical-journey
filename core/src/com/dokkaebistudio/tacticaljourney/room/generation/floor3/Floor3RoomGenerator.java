/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation.floor3;

import java.util.Iterator;
import java.util.List;

import com.badlogic.ashley.core.Entity;
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
		RandomSingleton random = RandomSingleton.getInstance();

		List<PoolableVector2> possibleSpawns = generatedRoom.getPossibleSpawns();
		List<PoolableVector2> spawnPositions = null;
		
		switch(room.type) {
		case COMMON_ENEMY_ROOM :
		case SHOP_ROOM:
		case STATUE_ROOM:
		case ITEM_ROOM:
		case KEY_ROOM:
			super.generateRoomContent(room, generatedRoom);
			return;
			
		case START_FLOOR_ROOM:
			
			break;
		case END_FLOOR_ROOM:
			if (possibleSpawns.size() == 0) return;
			int nextInt = random.nextSeededInt(possibleSpawns.size());
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
	protected void placeEnemies(Room room, RandomSingleton random, List<PoolableVector2> spawnPositions, boolean canBeEmpty) {
		if (spawnPositions.size() == 0) return;

		int enemyNb = random.nextSeededInt(Math.min(spawnPositions.size(), 6));
		if (enemyNb == 0 && !canBeEmpty) enemyNb = 1;
		
		Iterator<PoolableVector2> iterator = spawnPositions.iterator();
		for (int i=0 ; i<enemyNb ; i++) {
			if (!iterator.hasNext()) break;
			
			Entity enemy = null;
			int enemyTypeRandom = random.nextSeededInt(18);
			if (enemyTypeRandom == 0) {
				int spiderType = random.nextSeededInt(2);
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
					int spiderType = random.nextSeededInt(2);
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
			} else if (enemyTypeRandom == 7 || enemyTypeRandom == 8){
				enemy = entityFactory.enemyFactory.createScorpion(room, new Vector2(iterator.next()));
				iterator.remove();
			} else if (enemyTypeRandom >= 9 && enemyTypeRandom <= 12){
				enemy = entityFactory.enemyFactory.createTribesmenSpear(room, new Vector2(iterator.next()));
				iterator.remove();
			} else if (enemyTypeRandom >= 13 && enemyTypeRandom <= 15) {
				enemy = entityFactory.enemyFactory.createTribesmenShield(room, new Vector2(iterator.next()));
				iterator.remove();
			} else if (enemyTypeRandom == 16 || enemyTypeRandom == 17){
				enemy = entityFactory.enemyFactory.createTribesmenScout(room, new Vector2(iterator.next()));
				iterator.remove();
				if (iterator.hasNext()) {
					int tribesmanType = random.nextSeededInt(2);
					if (tribesmanType == 0) {
						enemy = entityFactory.enemyFactory.createTribesmenSpear(room, new Vector2(iterator.next()));
					} else {
						enemy = entityFactory.enemyFactory.createTribesmenShield(room, new Vector2(iterator.next()));
					}
					iterator.remove();
				}
			}
			
			LootRewardComponent lootRewardComponent = Mappers.lootRewardComponent.get(enemy);
			lootRewardComponent.setDrop( generateEnemyLoot(lootRewardComponent.getItemPool(), lootRewardComponent.getDropRate()));
		}
	}
}
