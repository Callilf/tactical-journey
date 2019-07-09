/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation.floor4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.ces.components.InspectableComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.transition.DoorComponent;
import com.dokkaebistudio.tacticaljourney.creature.enemies.enums.EnemyTypeEnum;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.generation.GeneratedRoom;
import com.dokkaebistudio.tacticaljourney.room.generation.RoomGenerator;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

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
		case TREASURE_ROOM:
			currentRoom.roomPattern = "data/rooms/treasureRoom.csv";
			break;
			
			default:
				currentRoom.roomPattern = "data/rooms/room1.csv";
		}

		return Gdx.files.internal(currentRoom.roomPattern);
	}
	
	
	@Override
	public void generateRoomContent(Room room, GeneratedRoom generatedRoom) {
		RandomSingleton random = RandomSingleton.getInstance();

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
			Entity shaman = room.entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.TRIBESMAN_SHAMAN,room, temp);
			Mappers.aiComponent.get(shaman).setAlerted(true, shaman, GameScreen.player);

			temp.free();
			
			
			spawnPositions = new ArrayList<>(possibleSpawns);
			Collections.shuffle(spawnPositions, random.getNextSeededRandom());

			Iterator<PoolableVector2> iterator = spawnPositions.iterator();
			for (int i=0 ; i<2 ; i++) {
				if (!iterator.hasNext()) break;
				
				int tribesmanType = random.nextSeededInt(3);
				Entity enemy = null;
				if (tribesmanType == 0) {
					enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.TRIBESMAN_SPEAR,room, iterator.next());
				} else if (tribesmanType == 1){
					enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.TRIBESMAN_SHIELD,room, iterator.next());
				} else {
					enemy = entityFactory.enemyFactory.createEnemy(EnemyTypeEnum.TRIBESMAN_SCOUT,room, iterator.next());
				}
				
				Mappers.aiComponent.get(enemy).setAlerted(true, enemy, GameScreen.player);
			}		
			
			room.closeDoors();
			
			break;
		case END_FLOOR_ROOM:
			
			List<Floor> floors = room.floor.getGameScreen().floors;
			Floor previousFloor = floors.get(floors.indexOf(room.floor) - 1);
			
			Vector2 northDoorPos = new Vector2(11, 12);
			Optional<Entity> door = TileUtil.getEntityWithComponentOnTile(northDoorPos, DoorComponent.class, room);
			if (door.isPresent()) {
				door.get().remove(InspectableComponent.class);
				entityFactory.createWallGate(room, new Vector2(11, 12), previousFloor.getTurns() <= previousFloor.getTurnThreshold());
			}
			
			if (possibleSpawns.size() == 0) return;
			Vector2 pos = possibleSpawns.get(0);
			entityFactory.createExit(room, pos, true);
			
//			pos = possibleSpawns.get(1);
//			Entity personalBelongings = entityFactory.lootableFactory.createPersonalBelongings(room, pos);
			break;
			
			default:
				super.generateRoomContent(room, generatedRoom);
				return;
		}
		
		placeDestructibles(room, random, generatedRoom.getPossibleDestr());
	
	
		// Release poolable vector2
		generatedRoom.releaseSpawns();
	}


	@Override
	protected void placeEnemies(Room room, RandomSingleton random, List<PoolableVector2> spawnPositions, boolean canBeEmpty) {}
}
