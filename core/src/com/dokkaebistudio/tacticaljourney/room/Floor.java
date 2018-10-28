/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.GameScreen;
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
	
	/** The currently active room. */
	private Room activeRoom;
	
	/**
	 * Constructor.
	 * @param gameScreen the game screen.
	 * @param timeDisplayer the timedisplayer that the current room will update
	 */
	public Floor(GameScreen gameScreen, Entity timeDisplayer) {
		this.gameScreen = gameScreen;
		
		Room room1 = new Room(this, gameScreen.engine, gameScreen.entityFactory, timeDisplayer);
		Room room2 = new Room(this, gameScreen.engine, gameScreen.entityFactory, timeDisplayer);
		
		room1.setNeighboors(room2, null, null, null);
		room2.setNeighboors(null, room1, null, null);
		
		room1.create();
		room2.create();
		
		rooms = new ArrayList<>();
		rooms.add(room1);
		rooms.add(room2);
		
		activeRoom = room1;
	}
	
	
	/** 
	 * Enter the given room.
	 * @param newRoom the room we are entering
	 */
	public void enterRoom(Room newRoom) {
		Room oldRoom = this.activeRoom;
		this.gameScreen.enterRoom(newRoom, oldRoom);
		this.activeRoom = newRoom;
		
		//Place the player
		if (newRoom.getNorthNeighboor() == oldRoom) {
			MovementHandler.placeEntity(this.gameScreen.player, new Vector2(GameScreen.GRID_W/2, GameScreen.GRID_H-1));
		} else if (newRoom.getSouthNeighboor() == oldRoom) {
			MovementHandler.placeEntity(this.gameScreen.player, new Vector2(GameScreen.GRID_W/2, 1));
		} else if (newRoom.getWestNeighboor() == oldRoom) {
			MovementHandler.placeEntity(this.gameScreen.player, new Vector2(0, GameScreen.GRID_H/2));
		} else if (newRoom.getEasthNeighboor() == oldRoom) {
			MovementHandler.placeEntity(this.gameScreen.player, new Vector2(GameScreen.GRID_W-1, GameScreen.GRID_H/2));
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
	}

	
}
