/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation.floor3;

import java.util.Iterator;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.creature.enemies.enums.EnemyTypeEnum;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.generation.GeneratedRoom;
import com.dokkaebistudio.tacticaljourney.room.generation.RoomGenerator;
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
			default:
				super.generateRoomContent(room, generatedRoom);
				return;
		}
//		
//		placeDestructibles(room, random, generatedRoom.getPossibleDestr());
//	
//	
//		// Release poolable vector2
//		generatedRoom.releaseSpawns();
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
			int enemyTypeRandom = random.nextSeededInt(100);
			if (enemyTypeRandom <= 3) {
				int spiderType = random.nextSeededInt(2);
				if (spiderType == 0) {
					enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.SPIDER,room, new Vector2(iterator.next()));
				} else {
					enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.VENOM_SPIDER,room, new Vector2(iterator.next()));
				}
				iterator.remove();
			} else if (enemyTypeRandom <= 10) {
				enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.WEB_SPIDER,room, new Vector2(iterator.next()));
				iterator.remove();
				if (iterator.hasNext()) {
					int spiderType = random.nextSeededInt(2);
					if (spiderType == 0) {
						enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.SPIDER,room, new Vector2(iterator.next()));
					} else {
						enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.VENOM_SPIDER,room, new Vector2(iterator.next()));
					}
					iterator.remove();
				}
			} else if (enemyTypeRandom <= 20) {
				enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.ORANGUTAN,room, new Vector2(iterator.next()));
				iterator.remove();
			} else if (enemyTypeRandom <= 25) {
				enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.STINGER,room, new Vector2(iterator.next()));
				iterator.remove();
			} else if (enemyTypeRandom <= 35) {
				enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.PANGOLIN_BABY,room, new Vector2(iterator.next()));
				iterator.remove();
			} else if (enemyTypeRandom <= 45){
				enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.SCORPION,room, new Vector2(iterator.next()));
				iterator.remove();
			} else if (enemyTypeRandom <= 65){
				enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.TRIBESMAN_SPEAR,room, new Vector2(iterator.next()));
				iterator.remove();
			} else if (enemyTypeRandom <= 80) {
				enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.TRIBESMAN_SHIELD,room, new Vector2(iterator.next()));
				iterator.remove();
			} else {
				enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.TRIBESMAN_SCOUT,room, new Vector2(iterator.next()));
				iterator.remove();
				if (iterator.hasNext()) {
					int tribesmanType = random.nextSeededInt(2);
					if (tribesmanType == 0) {
						enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.TRIBESMAN_SPEAR,room, new Vector2(iterator.next()));
					} else {
						enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.TRIBESMAN_SHIELD,room, new Vector2(iterator.next()));
					}
					iterator.remove();
				}
			}
		}
	}
}
