/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation.floor2;

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

/**
 * @author Callil
 *
 */
public class Floor2Generator extends FloorGenerator {

	private int roomIndex = 200;

	public Floor2Generator(EntityFactory ef) {
		this.setRoomGenerator(new Floor2RoomGenerator(ef));
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
		Room startRoom = new Room(floor, getNextRoomIndex(), GameScreen.engine, gameScreen.entityFactory, RoomType.BOSS_ROOM);
		roomsPerPosition.put(new Vector2(0,0), startRoom);
		positionsPerRoom.put(startRoom, new Vector2(0,0));
		rooms.add(startRoom);
		

		// Create the exit room
		Room exitRoom = new Room(floor, getNextRoomIndex(), GameScreen.engine, gameScreen.entityFactory, RoomType.END_FLOOR_ROOM);
		roomsPerPosition.put(new Vector2(0, 1), exitRoom);
		positionsPerRoom.put(exitRoom, new Vector2(0, 1));
		rooms.add(exitRoom);
		setNeighbors(GenerationMoveEnum.NORTH, startRoom, exitRoom);		
		
		
		// Create the treasure room
		Room treasureRoom = new Room(floor, getNextRoomIndex(), GameScreen.engine, gameScreen.entityFactory, RoomType.TREASURE_ROOM);
		roomsPerPosition.put(new Vector2(0, 2), treasureRoom);
		positionsPerRoom.put(treasureRoom, new Vector2(0, 2));
		rooms.add(treasureRoom);
		setNeighbors(GenerationMoveEnum.NORTH, exitRoom, treasureRoom);		
				
		
		// Generate the content of all rooms
		floor.setRooms(rooms);
		floor.setActiveRoom(startRoom);
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
