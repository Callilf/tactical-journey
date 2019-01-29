/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room;

import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.room.generation.FloorGenerator;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;

/**
 * Represents a floor that contains a set of rooms.
 * @author Callil
 *
 */
public class Floor {
	
	/** The game screen. */
	private GameScreen gameScreen;
	
	/** The rooms of this floor. */
	private List<Room> rooms;
	
	/** The positions of each rooms. */
	private Map<Room, Vector2> roomPositions;
	
	/** The currently active room. */
	private Room activeRoom;
	
	/**
	 * Constructor.
	 * @param gameScreen the game screen.
	 * @param timeDisplayer the timedisplayer that the current room will update
	 */
	public Floor(GameScreen gameScreen) {
		this.gameScreen = gameScreen;
		
		FloorGenerator.generateFloor(this, gameScreen);
	}
	
	
	/** 
	 * Enter the given room.
	 * @param newRoom the room we are entering
	 */
	public void enterRoom(Room newRoom) {
		Room oldRoom = this.getActiveRoom();
		this.gameScreen.enterRoom(newRoom, oldRoom);
		this.setActiveRoom(newRoom);
		
		//Place the player
		if (newRoom.getNorthNeighbor() == oldRoom) {
			MovementHandler.placeEntity(this.gameScreen.player, new Vector2(GameScreen.GRID_W/2, GameScreen.GRID_H-2), newRoom);
		} else if (newRoom.getSouthNeighbor() == oldRoom) {
			MovementHandler.placeEntity(this.gameScreen.player, new Vector2(GameScreen.GRID_W/2, 1), newRoom);
		} else if (newRoom.getWestNeighbor() == oldRoom) {
			MovementHandler.placeEntity(this.gameScreen.player, new Vector2(1, GameScreen.GRID_H/2), newRoom);
		} else if (newRoom.getEastNeighbor() == oldRoom) {
			MovementHandler.placeEntity(this.gameScreen.player, new Vector2(GameScreen.GRID_W-2, GameScreen.GRID_H/2), newRoom);
		}
	}

	
	
	// Getters & setters 
	
	public GameScreen getGameScreen() {
		return gameScreen;
	}

	public void setGameScreen(GameScreen gameScreen) {
		this.gameScreen = gameScreen;
	}

	public List<Room> getRooms() {
		return rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

	public Room getActiveRoom() {
		return activeRoom;
	}

	public void setActiveRoom(Room activeRoom) {
		this.activeRoom = activeRoom;
		this.activeRoom.setVisited(true);
	}


	public Map<Room, Vector2> getRoomPositions() {
		return roomPositions;
	}


	public void setRoomPositions(Map<Room, Vector2> roomPositions) {
		this.roomPositions = roomPositions;
	}

	
}
