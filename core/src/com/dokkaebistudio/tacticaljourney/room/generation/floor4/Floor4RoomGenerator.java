/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation.floor4;

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
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
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
public class Floor4RoomGenerator extends RoomGenerator {

	public Floor4RoomGenerator(EntityFactory ef) {
		super(ef);
	}
	
	
	@Override
	protected FileHandle chooseRoomPattern(Room currentRoom) {		
		switch(currentRoom.type) {
		case BOSS_ROOM:
			currentRoom.roomPattern = "data/rooms/bossRoomFloor4.csv";
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

			// BOSS FIGHT : Shaman
			
			// place orbs
			PoolableVector2 temp = PoolableVector2.create(8, 4);
			Entity orb1 = room.entityFactory.orbFactory.createDeathOrb(temp, room);
			orb1.add(room.engine.createComponent(SolidComponent.class));
			
			temp.set(9, 5);
			Entity orb2 = room.entityFactory.orbFactory.createDeathOrb(temp, room);
			orb2.add(room.engine.createComponent(SolidComponent.class));

			temp.set(13, 5);
			Entity orb3 = room.entityFactory.orbFactory.createDeathOrb(temp, room);
			orb3.add(room.engine.createComponent(SolidComponent.class));

			temp.set(14, 4);
			Entity orb4 = room.entityFactory.orbFactory.createDeathOrb(temp, room);
			orb4.add(room.engine.createComponent(SolidComponent.class));

			
			
			temp.set(11, 2);
			Entity shaman = room.entityFactory.enemyFactory.createTribesmenShaman(room, temp);
			Mappers.enemyComponent.get(shaman).setAlerted(true);

			temp.free();
			
			
			spawnPositions = new ArrayList<>(possibleSpawns);
			Collections.shuffle(spawnPositions, random);

			Iterator<PoolableVector2> iterator = spawnPositions.iterator();
			for (int i=0 ; i<2 ; i++) {
				if (!iterator.hasNext()) break;
				
				int tribesmanType = random.nextInt(3);
				Entity enemy = null;
				if (tribesmanType == 0) {
					enemy = entityFactory.enemyFactory.createTribesmenSpear(room, iterator.next());
				} else if (tribesmanType == 1){
					enemy = entityFactory.enemyFactory.createTribesmenShield(room, iterator.next());
				} else {
					enemy = entityFactory.enemyFactory.createTribesmenScout(room, iterator.next());
				}
				
				EnemyComponent enemyComponent = Mappers.enemyComponent.get(enemy);
				enemyComponent.setAlerted(true);
				
				LootRewardComponent lootRewardComponent = Mappers.lootRewardComponent.get(enemy);
				lootRewardComponent.setDrop( generateEnemyLoot(lootRewardComponent.getItemPool(), lootRewardComponent.getDropRate()));
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
			// TODO uncomment later
//			entityFactory.createExit(room, pos, true);
			
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