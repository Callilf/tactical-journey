/**
 * 
 */
package com.dokkaebistudio.tacticaljourney;

import java.util.ArrayList;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;

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
	
	public Floor(GameScreen gameScreen) {
		this.gameScreen = gameScreen;
		
		Room room1 = new Room(this, gameScreen.engine, gameScreen.entityFactory);
		Room room2 = new Room(this, gameScreen.engine, gameScreen.entityFactory);
		
		room1.setNeighboors(room2, null, null, null);
		room2.setNeighboors(null, room1, null, null);
		
		room1.create();
		room2.create();
		
		rooms = new ArrayList<>();
		rooms.add(room1);
		rooms.add(room2);
		
		activeRoom = room1;
	}
	
	
	
	public void enterRoom(Room newRoom) {
		Room oldRoom = this.activeRoom;
		this.gameScreen.enterRoom(newRoom, oldRoom);
		this.activeRoom = newRoom;
		
		//Place the player
		GridPositionComponent compo = this.gameScreen.player.getComponent(GridPositionComponent.class);
		if (newRoom.getNorthNeighboor() == oldRoom) {
			compo.coord.set(GameScreen.GRID_W/2, GameScreen.GRID_H-1);
		} else if (newRoom.getSouthNeighboor() == oldRoom) {
			compo.coord.set(GameScreen.GRID_W/2, 1);
		} else if (newRoom.getWestNeighboor() == oldRoom) {
			compo.coord.set(0, GameScreen.GRID_H/2);
		} else if (newRoom.getEasthNeighboor() == oldRoom) {
			compo.coord.set(GameScreen.GRID_W-1, GameScreen.GRID_H/2);
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
