/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation.floor2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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
public class Floor2RoomGenerator extends RoomGenerator {

	public Floor2RoomGenerator(EntityFactory ef) {
		super(ef);
	}
	
	
	@Override
	protected FileHandle chooseRoomPattern(Room currentRoom) {		
		switch(currentRoom.type) {
		case BOSS_ROOM:
			currentRoom.roomPattern = "data/rooms/bossRoomFloor2.csv";
			break;
			
			default:
				currentRoom.roomPattern = "data/rooms/room1.csv";
		}

		return Gdx.files.internal(currentRoom.roomPattern);
	}
	
	
	@Override
	public void generateRoomContent(Room room, GeneratedRoom generatedRoom) {
		RandomXS128 random = RandomSingleton.getInstance().getSeededRandom();

		List<PoolableVector2> possibleSpawns = generatedRoom.getPossibleSpawns();
		List<PoolableVector2> spawnPositions = null;
		
		switch(room.type) {
			
		case BOSS_ROOM:

			// BOSS FIGHT : Pangolin mother
			spawnPositions = new ArrayList<>(possibleSpawns);
			Collections.shuffle(spawnPositions, random);

			Iterator<PoolableVector2> iterator = spawnPositions.iterator();
			Entity mother = null;
			for (int i=0 ; i<4 ; i++) {
				if (!iterator.hasNext()) break;
				
				if (i == 0) {
					mother = entityFactory.enemyFactory.createPangolinMother(room, iterator.next());
					LootRewardComponent lootRewardComponent = Mappers.lootRewardComponent.get(mother);
					lootRewardComponent.setDrop( generateEnemyLoot(lootRewardComponent.getItemPool(), lootRewardComponent.getDropRate()));
				} else {
					Entity baby = entityFactory.enemyFactory.createPangolinBaby(room, iterator.next(), mother);
					LootRewardComponent lootRewardComponent = Mappers.lootRewardComponent.get(baby);
					lootRewardComponent.setDrop( generateEnemyLoot(lootRewardComponent.getItemPool(), lootRewardComponent.getDropRate()));
				}
				
			}
			
			
			// Close doors
			List<Entity> doors = room.getDoors();
			for (Entity door : doors) {
				Mappers.doorComponent.get(door).close(door);
			}
			
			break;
		case END_FLOOR_ROOM:
			if (possibleSpawns.size() == 0) return;
			Vector2 pos = possibleSpawns.get(0);
			entityFactory.createExit(room, pos, true);
			
			pos = possibleSpawns.get(1);
			Entity personalBelongings = entityFactory.lootableFactory.createPersonalBelongings(room, pos);

			default:
			break;
		}
		
		placeDestructibles(room, random, generatedRoom.getPossibleDestr());
	
	
		// Release poolable vector2
		generatedRoom.releaseSpawns();
	}


	@Override
	protected void placeEnemies(Room room, RandomXS128 random, List<PoolableVector2> spawnPositions, boolean canBeEmpty) {}
}
