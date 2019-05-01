/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.interfaces.MarkerInterface;
import com.dokkaebistudio.tacticaljourney.components.orbs.OrbCarrierComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.rendering.MapRenderer;
import com.dokkaebistudio.tacticaljourney.room.generation.FloorGenerator;
import com.dokkaebistudio.tacticaljourney.room.generation.floor1.Floor1Generator;
import com.dokkaebistudio.tacticaljourney.room.generation.floor2.Floor2Generator;
import com.dokkaebistudio.tacticaljourney.room.generation.floor3.Floor3Generator;
import com.dokkaebistudio.tacticaljourney.room.generation.floor4.Floor4Generator;
import com.dokkaebistudio.tacticaljourney.room.generation.floor5.Floor5Generator;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;

/**
 * Represents a floor that contains a set of rooms.
 * @author Callil
 *
 */
public class Floor {
	
	/** The game screen. */
	private GameScreen gameScreen;
	
	/** The level. */
	private int level;
	
	/** The rooms of this floor. */
	private List<Room> rooms;
	
	/** The positions of each rooms. */
	private Map<Vector2, Room> roomPositions;
	
	/** The room grid for this floor. */
	private RegionDescriptor gridDescriptor;
	private Sprite grid;
	
	/** The currently active room. */
	private Room activeRoom;
	
	private FloorGenerator floorGenerator;
	
	/**
	 * Constructor.
	 * @param gameScreen the game screen.
	 * @param timeDisplayer the time displayer that the current room will update
	 */
	public Floor(GameScreen gameScreen, int level) {
		this.gameScreen = gameScreen;
		this.setLevel(level);
		
		if (level == 1) {
			this.gridDescriptor = Assets.grid1;
			this.grid = new Sprite(Assets.grid1.getRegion());
			floorGenerator = new Floor1Generator(this.gameScreen.entityFactory);
		} else if (level == 2) {
			this.gridDescriptor = Assets.grid1;
			this.grid = new Sprite(Assets.grid1.getRegion());
			floorGenerator = new Floor2Generator(this.gameScreen.entityFactory);
		} else if (level == 3) {
			this.gridDescriptor = Assets.grid2;
			this.grid = new Sprite(Assets.grid2.getRegion());
			floorGenerator = new Floor3Generator(this.gameScreen.entityFactory);
		} else if (level == 4) {
			this.gridDescriptor = Assets.grid2;
			this.grid = new Sprite(Assets.grid2.getRegion());
			floorGenerator = new Floor4Generator(this.gameScreen.entityFactory);
		} else if (level == 5) {
			this.gridDescriptor = Assets.grid3;
			this.grid = new Sprite(Assets.grid3.getRegion());
			floorGenerator = new Floor5Generator(this.gameScreen.entityFactory);
		}
		
		 
		this.grid.setPosition(GameScreen.LEFT_RIGHT_PADDING, GameScreen.BOTTOM_MENU_HEIGHT);

		
	}
	
	/**
	 * Generate the floor's layout, rooms layouts and contents.
	 */
	public void generate() {
		this.floorGenerator.generateFloor(this, gameScreen);
	}
	
	
	
	/** 
	 * Enter the given room.
	 * @param newRoom the room we are entering
	 */
	public void enterRoom(Room newRoom) {
		Room oldRoom = this.getActiveRoom();
		enterRoom(newRoom, oldRoom);
	}
	
	/** 
	 * Enter the given room.
	 * @param newRoom the room we are entering
	 */
	public void enterRoom(Room newRoom, Room oldRoom) {
		if (oldRoom == newRoom) oldRoom = null;
		
		if (oldRoom != null) {
			Mappers.moveComponent.get(GameScreen.player).clearMovableTiles();
			Mappers.attackComponent.get(GameScreen.player).clearAttackableTiles();
		}
		
		this.gameScreen.enterRoom(newRoom, oldRoom);
		this.setActiveRoom(newRoom);
		
		// Hide markers for each entities of the old room
		if (oldRoom != null) {
			for (Entity e : oldRoom.getAllEntities()) {
				if (e == GameScreen.player) continue;
				
				for (Component c : e.getComponents()) {
					if (c instanceof MarkerInterface) {
						((MarkerInterface) c).hideMarker();
					}
				}
			}
		}
		
		// Display markers for each entities of the new room
		for (Entity e : newRoom.getAllEntities()) {
			if (e == GameScreen.player) continue;
			
			for (Component c : e.getComponents()) {
				if (c instanceof MarkerInterface) {
					((MarkerInterface) c).showMarker(e);
				}
			}
		}
		
		if (oldRoom != null) {
			this.removePlayerFromRoom(oldRoom);

			//Place the player
			if (newRoom.getNorthNeighbor() == oldRoom) {
				MovementHandler.placeEntity(GameScreen.player, new Vector2(GameScreen.GRID_W/2, GameScreen.GRID_H-2), newRoom);
			} else if (newRoom.getSouthNeighbor() == oldRoom) {
				MovementHandler.placeEntity(GameScreen.player, new Vector2(GameScreen.GRID_W/2, 1), newRoom);
			} else if (newRoom.getWestNeighbor() == oldRoom) {
				MovementHandler.placeEntity(GameScreen.player, new Vector2(1, GameScreen.GRID_H/2), newRoom);
			} else if (newRoom.getEastNeighbor() == oldRoom) {
				MovementHandler.placeEntity(GameScreen.player, new Vector2(GameScreen.GRID_W-2, GameScreen.GRID_H/2), newRoom);
			} else {
				// TP case
				MovementHandler.placeEntity(GameScreen.player, new Vector2(GameScreen.GRID_W/2, GameScreen.GRID_H-2), newRoom);
			}
		}
		
		
		newRoom.setNextState(RoomState.PLAYER_TURN_INIT);
		
		MapRenderer.requireRefresh();
	}
	
	public void removePlayerFromRoom(Room room) {
		GridPositionComponent playerPos = Mappers.gridPositionComponent.get(GameScreen.player);
		room.removeEntityAtPosition(GameScreen.player, playerPos.coord());
		
		OrbCarrierComponent orbCarrierComponent = Mappers.orbCarrierComponent.get(GameScreen.player);
		for (Entity orb : orbCarrierComponent.getOrbs()) {
			GridPositionComponent orbPos = Mappers.gridPositionComponent.get(orb);
			room.removeEntityAtPosition(orb, orbPos.coord());
		}
	}

	
	/**
	 * Return all the rooms of the given type.
	 * @param type the type
	 * @return a list of room with the given type
	 */
	public List<Room> getRooms(RoomType type) {
		List<Room> result = new ArrayList<>();
		for (Room r : rooms) {
			if (r.type == type) {
				result.add(r);
			}
		}
		return result;
	}
	
	
	/**
	 * Get a room given an index.
	 * @param roomIndex the room index
	 * @return the room with the given index
	 */
	public Room getRoomFromIndex(int roomIndex) {
		for (Room r : rooms) {
			if (r.getIndex() == roomIndex) return r;
		}
		return null;
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

	public Map<Vector2, Room> getRoomPositions() {
		return roomPositions;
	}
	public void setRoomPositions(Map<Vector2, Room> roomPositions) {
		this.roomPositions = roomPositions;
	}

	public Sprite getGrid() {
		return grid;
	}

	public void setGrid(Sprite grid) {
		this.grid = grid;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public FloorGenerator getFloorGenerator() {
		return this.floorGenerator;
	}
	
}
