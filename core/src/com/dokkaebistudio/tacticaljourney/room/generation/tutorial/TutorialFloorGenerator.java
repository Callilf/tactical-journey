/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation.tutorial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomType;
import com.dokkaebistudio.tacticaljourney.room.generation.FloorGenerator;
import com.dokkaebistudio.tacticaljourney.room.generation.FloorGenerator.GenerationMoveEnum;

/**
 * @author Callil
 *
 */
public class TutorialFloorGenerator extends FloorGenerator {
	
	private int roomIndex = 100;
	
	public TutorialFloorGenerator(EntityFactory ef) {
		this.setRoomGenerator(new TutorialFloorRoomGenerator(ef));
	}
	
	@Override
	protected int getNextRoomIndex() {
		int index = roomIndex;
		roomIndex++;
		return index;
	}

	/**
	 * Generate all the layout of the given floor.
	 * @param floor the floor to generate.
	 */
	public void generateFloor(Floor floor, GameScreen gameScreen) {
		random = RandomSingleton.getInstance();
		List<Room> rooms = new ArrayList<>();

		Room tuto1 = new Room(floor, getNextRoomIndex(), GameScreen.engine, gameScreen.entityFactory, RoomType.TUTORIAL_ROOM_1);
		roomsPerPosition.put(new Vector2(0,0), tuto1);
		positionsPerRoom.put(tuto1, new Vector2(0,0));
		rooms.add(tuto1);

		
		Room tuto2 = new Room(floor, getNextRoomIndex(), GameScreen.engine, gameScreen.entityFactory, RoomType.TUTORIAL_ROOM_2);
		roomsPerPosition.put(new Vector2(-1,0), tuto2);
		positionsPerRoom.put(tuto2, new Vector2(-1,0));
		rooms.add(tuto2);
		setNeighbors(GenerationMoveEnum.WEST, tuto1, tuto2);

		
		// Generate the content of all rooms
		floor.setRooms(rooms);
		floor.setActiveRoom(tuto1);
		floor.setRoomPositions(roomsPerPosition);

		for (Room r : rooms) {
			r.create();
		}
	}
	
	@Override
	protected List<ItemEnum> fillMandatoryItems() {
		return Collections.emptyList();
	}
}
