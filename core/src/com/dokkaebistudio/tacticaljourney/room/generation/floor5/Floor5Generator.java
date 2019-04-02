/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation.floor5;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomType;
import com.dokkaebistudio.tacticaljourney.room.generation.FloorGenerator;

/**
 * @author Callil
 *
 */
public class Floor5Generator extends FloorGenerator {

	private int roomIndex = 500;

	public Floor5Generator(EntityFactory ef) {
		this.setRoomGenerator(new Floor5RoomGenerator(ef));
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

		// Create the boss room
		Room startRoom = new Room(floor, getNextRoomIndex(), gameScreen.engine, gameScreen.entityFactory, RoomType.START_FLOOR_ROOM);
		roomsPerPosition.put(new Vector2(0,0), startRoom);
		positionsPerRoom.put(startRoom, new Vector2(0,0));
		rooms.add(startRoom);
		
		// Generate the content of all rooms
		floor.setRooms(rooms);
		floor.setActiveRoom(startRoom);
		floor.setRoomPositions(roomsPerPosition);

		for (Room r : rooms) {
			r.create();
		}
	}
}
