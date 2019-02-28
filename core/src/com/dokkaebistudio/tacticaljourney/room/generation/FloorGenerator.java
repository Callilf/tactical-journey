/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.generation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomType;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;

/**
 * Class used to generate a floor.
 * @author Callil
 *
 */
public abstract class FloorGenerator {
	
	private RandomXS128 random;
	private RoomGenerator roomGenerator;
	
	private Map<Room, Vector2> positionsPerRoom = new HashMap<>();
	private Map<Vector2, Room> roomsPerPosition = new HashMap<>();

	
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
	public void generateFloor(Floor floor, GameScreen gameScreen) {
		random = RandomSingleton.getInstance().getSeededRandom();
		List<Room> rooms = new ArrayList<>();		
		
		// 1 - compute distance between start room and end room
		int distanceStartToEnd = 3 + random.nextInt(4);
		
		// 2 - Given that startRoom is 0,0, find where to place endRoom
		int endRoomX = - distanceStartToEnd + random.nextInt((distanceStartToEnd * 2) + 1);
		int endRoomY = distanceStartToEnd - Math.abs(endRoomX);
		endRoomY = random.nextBoolean() ? endRoomY : -endRoomY;
		
		// 3 - Build a path from startRoom to endRoom
		Room startRoom = new Room(floor, gameScreen.engine, gameScreen.entityFactory, RoomType.START_FLOOR_ROOM);
		roomsPerPosition.put(new Vector2(0,0), startRoom);
		positionsPerRoom.put(startRoom, new Vector2(0,0));
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
			Room currentRoom = new Room(floor, gameScreen.engine, gameScreen.entityFactory, chooseRoomType());
			roomsPerPosition.put(new Vector2(currX, currY), currentRoom);
			positionsPerRoom.put(currentRoom, new Vector2(currX, currY));

			rooms.add(currentRoom);
			
			// Set the neighbors
			setNeighbors(currentMove, previousRoom, currentRoom);		
			
			previousRoom = currentRoom;
		}
		previousRoom.type = RoomType.END_FLOOR_ROOM;
		
		
		// 4 - Add rooms to this path
		
		int additionnalRoomsNumber = 6 + random.nextInt(2);
		addAdditionalRooms(floor, gameScreen, rooms, additionnalRoomsNumber);
		
		
		// 5 - Place mandatory rooms
		List<Room> values = new ArrayList<>(roomsPerPosition.values());
		Collections.shuffle(values, random);
		for (Room r : values) {
			if (r.type == RoomType.EMPTY_ROOM || r.type == RoomType.COMMON_ENEMY_ROOM) {
				r.type = RoomType.KEY_ROOM;
				break;
			}
		}
		for (Room r : values) {
			if (r.type == RoomType.EMPTY_ROOM || r.type == RoomType.COMMON_ENEMY_ROOM) {
				r.type = RoomType.SHOP_ROOM;
				break;
			}
		}
		for (Room r : values) {
			if (r.type == RoomType.EMPTY_ROOM || r.type == RoomType.COMMON_ENEMY_ROOM) {
				r.type = RoomType.STATUE_ROOM;
				break;
			}
		}
		
		// 6 - Add corridors between rooms
		addCorridors(rooms, roomsPerPosition);
		
		// 7 - Generate the content of all rooms
		for (Room r : rooms) {
			r.create();
		}
		floor.setRooms(rooms);
		floor.setActiveRoom(startRoom);
		floor.setRoomPositions(roomsPerPosition);

	}


	
	/**
	 * Choose the type of room to create.
	 * @return the type of room
	 */
	protected RoomType chooseRoomType() {
		if (random.nextInt(100) < 10) {
			return RoomType.EMPTY_ROOM;
		} else {
			return RoomType.COMMON_ENEMY_ROOM;
		}
//		return RoomType.SHOP_ROOM;
	}



	/**
	 * Add rooms to the path from the startRoom to the endRoom.
	 * @param floor the current floor which layout we are building
	 * @param gameScreen the gameScreen
	 * @param rooms the list of rooms which are at the moment the path from the start to the end
	 * @param additionalRoomsNumber the number of rooms we want to add to the main path
	 */
	private void addAdditionalRooms(Floor floor, GameScreen gameScreen, List<Room> rooms, int additionalRoomsNumber) {
		int chanceToAddRoom = 100;

		Room previousRoom;
		List<GenerationMoveEnum> possibleMove = new ArrayList<>();

		for (int i=0 ; i < additionalRoomsNumber ; i++) {
			for (int j=0 ; j < rooms.size() ; j++) {
				previousRoom = rooms.get(j);
				if (previousRoom.getNumberOfNeighbors() < 4) {
					int rand = random.nextInt(100);
					if (rand <= chanceToAddRoom) {
						//Add room here
												
						fillPossibleMoves(previousRoom, possibleMove);
						
						if (possibleMove.isEmpty()) {
							continue;
						}
						
						int directionIndex = random.nextInt(possibleMove.size());
						GenerationMoveEnum direction = possibleMove.get(directionIndex);
						Room currentRoom = new Room(floor, gameScreen.engine, gameScreen.entityFactory, chooseRoomType());
						rooms.add(currentRoom);
						
						Vector2 vector2 = getNewRoomPosition(previousRoom, direction);
						roomsPerPosition.put(vector2, currentRoom);
						positionsPerRoom.put(currentRoom, vector2);
						
						setNeighbors(direction, previousRoom, currentRoom);
						
						addAdditionalSubRooms(floor, gameScreen, currentRoom, chanceToAddRoom/2, rooms);
						
						chanceToAddRoom = chanceToAddRoom - 10;
						if (chanceToAddRoom <= 0) {
							chanceToAddRoom = 100;
						}
						
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
	 * @return the new position.
	 */
	private Vector2 getNewRoomPosition(Room previousRoom, GenerationMoveEnum direction) {

		Vector2 vector2 = new Vector2(positionsPerRoom.get(previousRoom));
		
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
	 * @param parentRoom the current room
	 * @param chanceToAddRoom the chance to add a room (on 100)
	 * @param allRooms the list of all rooms of this floor that we might complete
	 */
	private void addAdditionalSubRooms(Floor floor, GameScreen gameScreen, Room parentRoom, int chanceToAddRoom, List<Room> allRooms) {
		List<GenerationMoveEnum> possibleMove = new ArrayList<>();
		fillPossibleMoves(parentRoom, possibleMove);
		if (possibleMove.isEmpty()) {
			return;
		}
		
		Collections.shuffle(possibleMove, random);
		
		for (GenerationMoveEnum direction : possibleMove) {
			int rand = random.nextInt(100);
			if (rand <= chanceToAddRoom) {
				//Add room here
				chanceToAddRoom = chanceToAddRoom/2;
							
				Room currentRoom = new Room(floor, gameScreen.engine, gameScreen.entityFactory, chooseRoomType());
				allRooms.add(currentRoom);
				setNeighbors(direction, parentRoom, currentRoom);
				
				Vector2 vector2 = getNewRoomPosition(parentRoom, direction);
				roomsPerPosition.put(vector2, currentRoom);
				positionsPerRoom.put(currentRoom, vector2);
				
				addAdditionalSubRooms(floor, gameScreen, currentRoom, chanceToAddRoom, allRooms);
				
			}
		}
				
	}
	
	/**
	 * Randomly add corridors between adjacent rooms.
	 * @param rooms the list of rooms
	 * @param roomsPerPosition the map that gives a room given a position.
	 */
	private void addCorridors(List<Room> rooms, Map<Vector2, Room> roomsPerPosition) {
		for (Room currentRoom : rooms) {
			if (currentRoom.getNorthNeighbor() == null) {
				PoolableVector2 vector2 = PoolableVector2.create(positionsPerRoom.get(currentRoom));
				vector2.y += 1;
				Room linkedRoom = roomsPerPosition.get(vector2);
				if (linkedRoom != null) {
					boolean addCorridor = random.nextInt(4) == 0;
					if (addCorridor) {
						currentRoom.setNorthNeighbor(linkedRoom);
						linkedRoom.setSouthNeighbor(currentRoom);
					}
				}
				vector2.free();
			}
			
			if (currentRoom.getSouthNeighbor() == null) {
				PoolableVector2 vector2 = PoolableVector2.create(positionsPerRoom.get(currentRoom));
				vector2.y -= 1;
				Room linkedRoom = roomsPerPosition.get(vector2);
				if (linkedRoom != null) {
					boolean addCorridor = random.nextInt(4) == 0;
					if (addCorridor) {
						currentRoom.setSouthNeighbor(linkedRoom);
						linkedRoom.setNorthNeighbor(currentRoom);
					}
				}
				vector2.free();
			}

			if (currentRoom.getEastNeighbor() == null) {
				PoolableVector2 vector2 = PoolableVector2.create(positionsPerRoom.get(currentRoom));
				vector2.x += 1;
				Room linkedRoom = roomsPerPosition.get(vector2);
				if (linkedRoom != null) {
					boolean addCorridor = random.nextInt(4) == 0;
					if (addCorridor) {
						currentRoom.setEastNeighbor(linkedRoom);
						linkedRoom.setWestNeighbor(currentRoom);
					}
				}
				vector2.free();
			}
			
			if (currentRoom.getWestNeighbor() == null) {
				PoolableVector2 vector2 = PoolableVector2.create(positionsPerRoom.get(currentRoom));
				vector2.x -= 1;
				Room linkedRoom = roomsPerPosition.get(vector2);
				if (linkedRoom != null) {
					boolean addCorridor = random.nextInt(4) == 0;
					if (addCorridor) {
						currentRoom.setWestNeighbor(linkedRoom);
						linkedRoom.setEastNeighbor(currentRoom);
					}
				}
				vector2.free();
			}

		}
	}


	/**
	 * Check for a given room in which direction we can add a new room.
	 * @param room the room to check
	 * @param possibleMove the list of possible move to fill
	 */
	private void fillPossibleMoves(Room room, List<GenerationMoveEnum> possibleMove) {
		possibleMove.clear();
		
		Vector2 vector2 = new Vector2(positionsPerRoom.get(room));
		
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
	private NewRoomPos moveHorizontally(int currX, int endRoomX) {
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
	private NewRoomPos moveVertically(int currY, int endRoomY) {
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
	private void setNeighbors(GenerationMoveEnum currentMove, Room previousRoom, Room currentRoom) {
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



	public RoomGenerator getRoomGenerator() {
		return roomGenerator;
	}



	public void setRoomGenerator(RoomGenerator roomGenerator) {
		this.roomGenerator = roomGenerator;
	}

}
