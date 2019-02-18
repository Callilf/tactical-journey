/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomType;

/**
 * Class used to generate a floor.
 * @author Callil
 *
 */
public class FloorGenerator {
	
	public enum GenerationMoveEnum {
		NORTH,
		SOUTH,
		WEST,
		EAST
	}
	
	/**
	 * Generate all the layout of the given floor.
	 * @param floor the floor to generate.
	 */
	public static void generateFloor(Floor floor, GameScreen gameScreen) {
		RandomXS128 random = RandomSingleton.getInstance().getSeededRandom();
		List<Room> rooms = new ArrayList<>();
		Map<Vector2, Room> roomsPerPosition = new HashMap<>();
		
		
		// 1 - compute distance between start room and end room
		int distanceStartToEnd = 3 + random.nextInt(4);
		
		// 2 - Given that startRoom is 0,0, find where to place endRoom
		int endRoomX = - distanceStartToEnd + random.nextInt((distanceStartToEnd * 2) + 1);
		int endRoomY = distanceStartToEnd - Math.abs(endRoomX);
		endRoomY = random.nextBoolean() ? endRoomY : -endRoomY;
		
		// 3 - Build a path from startRoom to endRoom
		Room startRoom = new Room(floor, gameScreen.engine, gameScreen.entityFactory, RoomType.START_FLOOR_ROOM);
		roomsPerPosition.put(new Vector2(0,0), startRoom);
		rooms.add(startRoom);
		
		int currX = 0;
		int currY = 0;
		GenerationMoveEnum currentMove = null;
		Room previousRoom = startRoom;
		while (currX != endRoomX || currY != endRoomY) {
			if (currX != endRoomX && currY != endRoomY) {
				if (random.nextBoolean()) {
					NewRoomPos newRoomPos = moveHorizontally(currX, endRoomX);
					currX = newRoomPos.newCoord;
					currentMove = newRoomPos.direction;
				} else {
					NewRoomPos newRoomPos = moveVertically(currY, endRoomY);
					currY = newRoomPos.newCoord;
					currentMove = newRoomPos.direction;
				}
			} else if (currX != endRoomX) {
				NewRoomPos newRoomPos = moveHorizontally(currX, endRoomX);
				currX = newRoomPos.newCoord;
				currentMove = newRoomPos.direction;
			} else {
				NewRoomPos newRoomPos = moveVertically(currY, endRoomY);
				currY = newRoomPos.newCoord;
				currentMove = newRoomPos.direction;
			}
			
			// Create the room
			Room currentRoom = new Room(floor, gameScreen.engine, gameScreen.entityFactory,RoomType.COMMON_ENEMY_ROOM);
			roomsPerPosition.put(new Vector2(currX, currY), currentRoom);
			rooms.add(currentRoom);
			
			// Set the neighbors
			setNeighbors(currentMove, previousRoom, currentRoom);		
			
			previousRoom = currentRoom;
		}
		previousRoom.type = RoomType.END_FLOOR_ROOM;
		
		
		// 4 - Add rooms to this path
		
		int additionnalRoomsNumber = 5 + random.nextInt(6);
		addAdditionalRooms(floor, gameScreen,random, rooms, roomsPerPosition, additionnalRoomsNumber);
		
		
		// 5 - Generate the content of all rooms
		for (Room r : rooms) {
			r.create();
		}
		floor.setRooms(rooms);
		floor.setActiveRoom(rooms.get(0));
		floor.setRoomPositions(roomsPerPosition);

	}



	/**
	 * Add rooms to the path from the startRoom to the endRoom.
	 * @param floor the current floor which layout we are building
	 * @param gameScreen the gameScreen
	 * @param random the random
	 * @param rooms the list of rooms which are at the moment the path from the start to the end
	 * @param additionalRoomsNumber the number of rooms we want to add to the main path
	 */
	private static void addAdditionalRooms(Floor floor, GameScreen gameScreen,
			RandomXS128 random, List<Room> rooms, Map<Vector2, Room> roomsPerPosition, int additionalRoomsNumber) {
		int chanceToAddRoom = 100;
		boolean shopPlaced = false;

		Room previousRoom;
		List<GenerationMoveEnum> possibleMove = new ArrayList<>();

		for (int i=0 ; i < additionalRoomsNumber ; i++) {
			for (int j=0 ; j < rooms.size() ; j++) {
				previousRoom = rooms.get(j);
				if (previousRoom.getNumberOfNeighbors() < 4) {
					int rand = random.nextInt(100);
					if (rand <= chanceToAddRoom) {
						//Add room here
												
						fillPossibleMoves(previousRoom, possibleMove, roomsPerPosition);
						
						if (possibleMove.isEmpty()) {
							continue;
						}
						
						int directionIndex = random.nextInt(possibleMove.size());
						GenerationMoveEnum direction = possibleMove.get(directionIndex);
						Room currentRoom = new Room(floor, gameScreen.engine, gameScreen.entityFactory, shopPlaced ? RoomType.COMMON_ENEMY_ROOM : RoomType.SHOP_ROOM);
						rooms.add(currentRoom);
						
						Vector2 vector2 = getNewRoomPosition(previousRoom, direction, roomsPerPosition);
						roomsPerPosition.put(vector2, currentRoom);
						
						setNeighbors(direction, previousRoom, currentRoom);
						
						addAdditionalSubRooms(floor, gameScreen, random, currentRoom, chanceToAddRoom/2, rooms, roomsPerPosition);
						
						chanceToAddRoom = chanceToAddRoom - 10;
						if (chanceToAddRoom <= 0) {
							chanceToAddRoom = 100;
						}
						
						shopPlaced = true;
						break;
					}
				}
				
			}
		}
	}


	/**
	 * Get the x,y coordinates given a start room and a direction.
	 * @param previousRoom the start room
	 * @param direction the move direction
	 * @param roomsPerPosition the map containing the position of all rooms.
	 * @return the new position.
	 */
	private static Vector2 getNewRoomPosition(Room previousRoom, GenerationMoveEnum direction,
			Map<Vector2, Room> roomsPerPosition) {
		Vector2 vector2 = null;
		for (Entry<Vector2, Room> entry : roomsPerPosition.entrySet()) {
			if (entry.getValue() == previousRoom) {
				vector2 = new Vector2(entry.getKey());
				break;
			}
		}
		
		int xOffset = 0;
		int yOffset = 0;
		if (direction == GenerationMoveEnum.NORTH) yOffset = 1;
		if (direction == GenerationMoveEnum.SOUTH) yOffset = -1;
		if (direction == GenerationMoveEnum.WEST) xOffset = -1;
		if (direction == GenerationMoveEnum.EAST) xOffset = 1;
		vector2.add(xOffset, yOffset);
		return vector2;
	}
	
	/**
	 * Add new rooms to an additional room, which is a room that is not on the main path.
	 * @param floor the current floor
	 * @param gameScreen the gameScreen
	 * @param random the random
	 * @param parentRoom the current room
	 * @param chanceToAddRoom the chance to add a room (on 100)
	 * @param allRooms the list of all rooms of this floor that we might complete
	 */
	private static void addAdditionalSubRooms(Floor floor, GameScreen gameScreen,
			RandomXS128 random, Room parentRoom, int chanceToAddRoom, List<Room> allRooms, Map<Vector2, Room> roomsPerPosition) {
		List<GenerationMoveEnum> possibleMove = new ArrayList<>();
		fillPossibleMoves(parentRoom, possibleMove, roomsPerPosition);
		if (possibleMove.isEmpty()) {
			return;
		}
		
		Collections.shuffle(possibleMove, random);
		
		for (GenerationMoveEnum direction : possibleMove) {
			int rand = random.nextInt(100);
			if (rand <= chanceToAddRoom) {
				//Add room here
				chanceToAddRoom = chanceToAddRoom/2;
							
				Room currentRoom = new Room(floor, gameScreen.engine, gameScreen.entityFactory, RoomType.COMMON_ENEMY_ROOM);
				allRooms.add(currentRoom);
				setNeighbors(direction, parentRoom, currentRoom);
				
				Vector2 vector2 = getNewRoomPosition(parentRoom, direction, roomsPerPosition);
				roomsPerPosition.put(vector2, currentRoom);
				
				addAdditionalSubRooms(floor, gameScreen, random, currentRoom, chanceToAddRoom, allRooms, roomsPerPosition);
				
			}
		}
				
	}


	/**
	 * Check for a given room in which direction we can add a new room.
	 * @param room the room to check
	 * @param possibleMove the list of possible move to fill
	 */
	private static void fillPossibleMoves(Room room, List<GenerationMoveEnum> possibleMove, Map<Vector2, Room> roomsPerPosition) {
		possibleMove.clear();
		
		Vector2 vector2 = null;
		for (Entry<Vector2, Room> entry : roomsPerPosition.entrySet()) {
			if (entry.getValue() == room) {
				vector2 = new Vector2(entry.getKey());
				break;
			}
		}
		
		if (room.getNorthNeighbor() == null) {
			vector2.add(0, 1);
			if (!roomsPerPosition.containsKey(vector2)) {
				possibleMove.add(GenerationMoveEnum.NORTH);
			}
			vector2.add(0, -1);
		}
		if (room.getSouthNeighbor() == null) {
			vector2.add(0, -1);
			if (!roomsPerPosition.containsKey(vector2)) {
				possibleMove.add(GenerationMoveEnum.SOUTH);
			}
			vector2.add(0, 1);
		}
		if (room.getWestNeighbor() == null) {
			vector2.add(-1, 0);
			if (!roomsPerPosition.containsKey(vector2)) {
				possibleMove.add(GenerationMoveEnum.WEST);
			}
			vector2.add(1, 0);
		}
		if (room.getEastNeighbor() == null) {
			vector2.add(1, 0);
			if (!roomsPerPosition.containsKey(vector2)) {
				possibleMove.add(GenerationMoveEnum.EAST);
			}
			vector2.add(-1, 0);
		}
	}
	
	/**
	 * Move by one horizontally.
	 * @param currX the current X
	 * @param endRoomX the X of the end room
	 * @return the new currX and the modified currentMove
	 */
	private static NewRoomPos moveHorizontally(int currX, int endRoomX) {
		GenerationMoveEnum currentMove = null;
		if (endRoomX > currX) {
			currX ++;
			currentMove = GenerationMoveEnum.EAST;
		} else {
			currX --;
			currentMove = GenerationMoveEnum.WEST;
		}
		
		return new NewRoomPos(currX, currentMove);
	}
	
	/**
	 * Move by one vertically.
	 * @param currY the current Y
	 * @param endRoomY the Y of the end room
	 * @param currentMove the currentMove that will be updated by this method.
	 * @return the new curry and the modified currentMove
	 */
	private static NewRoomPos moveVertically(int currY, int endRoomY) {
		GenerationMoveEnum currentMove = null;
		if (endRoomY > currY) {
			currY ++;
			currentMove = GenerationMoveEnum.NORTH;
		} else {
			currY --;
			currentMove = GenerationMoveEnum.SOUTH;
		}
		return new NewRoomPos(currY, currentMove);
	}

	/**
	 * Set the neighbors of the previous room and the current room.
	 * @param currentMove the move we just did that allows knowing how previousRoom and currentRoom are connected
	 * @param previousRoom the previous room
	 * @param currentRoom the current room
	 */
	private static void setNeighbors(GenerationMoveEnum currentMove, Room previousRoom, Room currentRoom) {
		switch (currentMove) {
			case NORTH:
				previousRoom.setNorthNeighbor(currentRoom);
				currentRoom.setSouthNeighbor(previousRoom);
				break;
			case SOUTH:
				previousRoom.setSouthNeighbor(currentRoom);
				currentRoom.setNorthNeighbor(previousRoom);
				break;
			case WEST:
				previousRoom.setWestNeighbor(currentRoom);
				currentRoom.setEastNeighbor(previousRoom);
				break;
			case EAST:
				previousRoom.setEastNeighbor(currentRoom);
				currentRoom.setWestNeighbor(previousRoom);
				break;
		}
	}

}
