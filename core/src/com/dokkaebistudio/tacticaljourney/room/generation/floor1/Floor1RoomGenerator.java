/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation.floor1;

import java.util.Iterator;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.generation.RoomGenerator;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
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
	protected void placeEnemies(Room room, RandomXS128 random, List<PoolableVector2> spawnPositions, boolean canBeEmpty) {
		if (spawnPositions.size() == 0) return;

		int enemyNb = random.nextInt(Math.min(spawnPositions.size(), 7));
		if (enemyNb == 0 && !canBeEmpty) enemyNb = 1;
		
		Iterator<PoolableVector2> iterator = spawnPositions.iterator();
		for (int i=0 ; i<enemyNb ; i++) {
			if (!iterator.hasNext()) break;
			
			Entity enemy = null;
			int enemyTypeRandom = random.nextInt(8);
			if (enemyTypeRandom == 0) {
				enemy = entityFactory.enemyFactory.createScorpion(room, new Vector2(iterator.next()));
				iterator.remove();
			} else if (enemyTypeRandom == 1) {
				enemy = entityFactory.enemyFactory.createPangolinBaby(room, new Vector2(iterator.next()));
				iterator.remove();
			} else if (enemyTypeRandom == 2) {
				enemy = entityFactory.enemyFactory.createSpiderWeb(room, new Vector2(iterator.next()));
				iterator.remove();
				if (iterator.hasNext()) {
					enemy = entityFactory.enemyFactory.createSpider(room, new Vector2(iterator.next()));
				}
			} else if (enemyTypeRandom == 4 || enemyTypeRandom == 3) {
				enemy = entityFactory.enemyFactory.createStinger(room, new Vector2(iterator.next()));
				iterator.remove();
			} else {
				enemy = entityFactory.enemyFactory.createSpider(room, new Vector2(iterator.next()));
				iterator.remove();
			}
			
			LootRewardComponent lootRewardComponent = Mappers.lootRewardComponent.get(enemy);
			lootRewardComponent.setDrop( generateEnemyLoot(lootRewardComponent.getItemPool(), lootRewardComponent.getDropRate()));
		}
	}
}
