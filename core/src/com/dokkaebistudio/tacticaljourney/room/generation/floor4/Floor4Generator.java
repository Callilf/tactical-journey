/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation.floor4;

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
public class Floor4Generator extends FloorGenerator {

	private int roomIndex = 400;

	public Floor4Generator(EntityFactory ef) {
		this.setRoomGenerator(new Floor4RoomGenerator(ef));
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
		Room startRoom = new Room(floor, getNextRoomIndex(), gameScreen.engine, gameScreen.entityFactory, RoomType.BOSS_ROOM);
		roomsPerPosition.put(new Vector2(0,0), startRoom);
		positionsPerRoom.put(startRoom, new Vector2(0,0));
		rooms.add(startRoom);
		

		// Create the exit room
		Room currentRoom = new Room(floor, getNextRoomIndex(), gameScreen.engine, gameScreen.entityFactory, RoomType.END_FLOOR_ROOM);
		roomsPerPosition.put(new Vector2(0, 1), currentRoom);
		positionsPerRoom.put(currentRoom, new Vector2(0, 1));
		rooms.add(currentRoom);
			
		// Set the neighbors
		setNeighbors(GenerationMoveEnum.NORTH, startRoom, currentRoom);		
				
		// Generate the content of all rooms
		floor.setRooms(rooms);
		floor.setActiveRoom(startRoom);
		floor.setRoomPositions(roomsPerPosition);

		for (Room r : rooms) {
			r.create();
		}
	}
}
