/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation.floor2;

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
import com.dokkaebistudio.tacticaljourney.components.InspectableComponent;
import com.dokkaebistudio.tacticaljourney.components.transition.DoorComponent;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.generation.GeneratedRoom;
import com.dokkaebistudio.tacticaljourney.room.generation.RoomGenerator;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

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

			// BOSS FIGHT : Pangolin mother
			spawnPositions = new ArrayList<>(possibleSpawns);
			Collections.shuffle(spawnPositions, random.getNextSeededRandom());

			Iterator<PoolableVector2> iterator = spawnPositions.iterator();
			Entity mother = null;
			for (int i=0 ; i<4 ; i++) {
				if (!iterator.hasNext()) break;
				
				if (i == 0) {
					mother = entityFactory.enemyFactory.pangolinFactory.createPangolinMother(room, iterator.next());
				} else {
					Entity baby = entityFactory.enemyFactory.pangolinFactory.createPangolinBaby(room, iterator.next(), mother);
				}
				
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
