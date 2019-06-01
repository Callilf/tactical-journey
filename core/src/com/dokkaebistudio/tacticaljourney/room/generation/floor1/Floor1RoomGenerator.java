/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation.floor1;

import java.util.Iterator;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.creature.enemies.enums.EnemyTypeEnum;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.generation.RoomGenerator;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;

/**
 * @author Callil
 *
 */
public class Floor1RoomGenerator extends RoomGenerator {

	public Floor1RoomGenerator(EntityFactory ef) {
		super(ef);
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

		int enemyNb = random.nextSeededInt(Math.min(spawnPositions.size(), 5));
		if (enemyNb == 0 && !canBeEmpty) enemyNb = 1;
		
		Iterator<PoolableVector2> iterator = spawnPositions.iterator();
		for (int i=0 ; i<enemyNb ; i++) {
			if (!iterator.hasNext()) break;
			
			Entity enemy = null;
			int enemyTypeRandom = random.nextSeededInt(100);
			if (enemyTypeRandom <= 10) {
				enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.SCORPION, room, new Vector2(iterator.next()));
				iterator.remove();
			} else if (enemyTypeRandom <= 20) {
				enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.PANGOLIN_BABY,room, new Vector2(iterator.next()));
				iterator.remove();
			} else if (enemyTypeRandom <= 30) {
				enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.WEB_SPIDER,room, new Vector2(iterator.next()));
				iterator.remove();
				if (iterator.hasNext()) {
					enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.SPIDER,room, new Vector2(iterator.next()));
					iterator.remove();
				}
			} else if (enemyTypeRandom <= 40) {
				enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.VENOM_SPIDER,room, new Vector2(iterator.next()));
				iterator.remove();
			} else if (enemyTypeRandom <= 60) {
				enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.STINGER,room, new Vector2(iterator.next()));
				iterator.remove();
			} else if (enemyTypeRandom <= 65) {
				enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.ORANGUTAN,room, new Vector2(iterator.next()));
				iterator.remove();
			} else {
				enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.SPIDER,room, new Vector2(iterator.next()));
				iterator.remove();
			}
		}
	}
}
