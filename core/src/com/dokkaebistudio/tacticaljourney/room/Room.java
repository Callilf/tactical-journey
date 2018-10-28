/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.dokkaebistudio.tacticaljourney.room;

import static com.dokkaebistudio.tacticaljourney.GameScreen.GRID_H;
import static com.dokkaebistudio.tacticaljourney.GameScreen.GRID_W;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.Floor;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.GameTimeSingleton;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.TileComponent.TileEnum;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TransformComponent;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;

public class Room extends EntitySystem {
	public Floor floor;
	
	public RoomState state;
	public Entity[][] grid;
	
	public PooledEngine engine;
	public EntityFactory entityFactory;
	
	public TurnManager turnManager;
	public AttackManager attackManager;
	
	/** The entities of this room. */
	private List<Entity> entities;
	
	private Entity timeDisplayer;
	
	
	private Room northNeighboor;
	private Room southNeighboor;
	private Room westNeighboor;
	private Room easthNeighboor;
	

	public Room (Floor f, PooledEngine engine, EntityFactory ef) {
		this.floor = f;
		this.engine = engine;
		this.entityFactory = ef;

	}

	
	public void leaveRoom(Room nextRoom) {
		this.state = RoomState.PLAYER_TURN_INIT;
		this.floor.enterRoom(nextRoom);
	}
	
	
	@Override
	public void update(float deltaTime) {
		GameTimeSingleton gtSingleton = GameTimeSingleton.getInstance();
		gtSingleton.updateElapsedTime(deltaTime);
		
		TextComponent text = timeDisplayer.getComponent(TextComponent.class);
		text.setText("Time: " + String.format("%.1f", gtSingleton.getElapsedTime()));
	}
	
	
	public void create() {
		entities = new ArrayList<>();
		turnManager = new TurnManager(this);
		attackManager = new AttackManager(this);
		createGrid();
		
		this.state = RoomState.PLAYER_TURN_INIT;
		
		createTimeDisplayer();
		
		RandomXS128 random = RandomSingleton.getInstance().getRandom();
		
		int x2 = 1 + random.nextInt(GameScreen.GRID_W - 2);
		int y2 = 3 + random.nextInt(GameScreen.GRID_H - 4);
		Entity spider1 = entityFactory.enemyFactory.createSpider(this, new Vector2(x2,y2), 3);
		
		int x3 = 1 + random.nextInt(GameScreen.GRID_W - 2);
		int y3 = 3 + random.nextInt(GameScreen.GRID_H - 4);
		Entity spider2 = entityFactory.enemyFactory.createSpider(this, new Vector2(x3,y3), 3);
		
		int x4 = 1 + random.nextInt(GameScreen.GRID_W - 2);
		int y4 = 3 + random.nextInt(GameScreen.GRID_H - 4);
		Entity scorpion = entityFactory.enemyFactory.createScorpion(this, new Vector2(x4,y4), 4);
		
		int x5 = 1 + random.nextInt(GameScreen.GRID_W - 2);
		int y5 = 3 + random.nextInt(GameScreen.GRID_H - 4);
		Entity healthUp = entityFactory.createItemHealthUp(this, new Vector2(x5,y5));
	}
//	
//	/** Add an entity to the room. */
//	public void addEntity(Entity e) {
//		entities.add(e);
//		for (Component c : e.getComponents()) {
//			if (c instanceof ContainsEntityInterface) {
//				((ContainsEntityInterface)c).addEntitiesFromRoom(this);
//			}
//		}
//	}
	
//	/**
//	 * Remove an entity from the room (and the engine).
//	 * @param e the entity to remove
//	 */
//	public void removeEntity(Entity e) {
//		entities.remove(e);
//		engine.removeEntity(e);
//		
//		for (Component c : e.getComponents()) {
//			if (c instanceof ContainsEntityInterface) {
//				((ContainsEntityInterface)c).removeEntitiesFromRoom(this);
//			}
//		}
//	}

	/** Create the entity that displays the current game time. */
	private void createTimeDisplayer() {
		//Display time
		timeDisplayer = entityFactory.createText(new Vector3(0,0,100), "Time: ");
		TextComponent text = timeDisplayer.getComponent(TextComponent.class);
		text.setText("Time: " + GameTimeSingleton.getInstance().getElapsedTime());
		TransformComponent transfo = timeDisplayer.getComponent(TransformComponent.class);
		transfo.pos.set(300, 100, 100);
	}

	/**
	 * Create the grid, ie. fille the 2 dimensional array of tile entities.
	 */
	private void createGrid() {
		TileEnum[][] generatedRoom = generateRoom();
		grid = new Entity[GRID_W][GameScreen.GRID_H];
		for (int x = 0; x < GRID_W; x++) {
			for (int y = 0; y < GameScreen.GRID_H; y++) {
				Entity tileEntity = entityFactory.createTile(this, new Vector2(x, y), generatedRoom[x][y]);
				grid[x][y] = tileEntity;
			}
		}
	}
	
	/**
	 * Return the entity for the tile at the given position.
	 * @param x the abciss
	 * @param y the ordinate
	 * @return the tile at the given position
	 */
	public Entity getTileAtGridPosition(int x, int y) {
		return grid[x][y];
	}
	
	/**
	 * Return the entity for the tile at the given position.
	 * @param pos the position
	 * @return the tile at the given position
	 */
	public Entity getTileAtGridPosition(Vector2 pos) {
		return grid[(int) pos.x][(int) pos.y];
	}

	/**
	 * Generates a random room by creating an array of {@link TileEnum}. There are walls on each border of the room.
	 */
	private TileEnum[][] generateRoom() {
		TileEnum[][] tiles = new TileEnum[GRID_W][GRID_H];
		// fill with ground first
		for(TileEnum[] tileCol: tiles){
			Arrays.fill(tileCol, TileEnum.GROUND);
		}
		for (int x = 0; x < GRID_W; x++) {
			for (int y = 0; y < GameScreen.GRID_H; y++) {
				if (x == 0 || x == GRID_W-1 || y == 0 || y == 1 || y == GRID_H - 1) {
					
					//Spaces for doors
					if ( (x == 0 && y== GRID_H/2) ) {
						tiles[x][y] = TileEnum.GROUND;
						Entity door = entityFactory.createDoor(this, new Vector2(x,y), westNeighboor);
					} else if ( x== GRID_W-1 && y== GRID_H/2) {
						tiles[x][y] = TileEnum.GROUND;
						Entity door = entityFactory.createDoor(this, new Vector2(x,y), easthNeighboor);
					} else if ( x == GRID_W/2 && y == 1) {
						tiles[x][y] = TileEnum.GROUND;
						Entity door = entityFactory.createDoor(this, new Vector2(x,y), southNeighboor);
					} else if ( x == GRID_W/2 && y == GRID_H-1) {
						tiles[x][y] = TileEnum.GROUND;
						Entity door = entityFactory.createDoor(this, new Vector2(x,y), northNeighboor);
					} else {
						// walls
						tiles[x][y] = TileEnum.WALL;
					}
				} else {
					// generate some random walls and pits
					RandomXS128 random = RandomSingleton.getInstance().getRandom();
					int r = random.nextInt(15);
					if (r == 0) {
						// PIT
						tiles[x][y] = TileEnum.PIT;
					} else if (r == 1) {
						// WALL
						tiles[x][y] = TileEnum.WALL;
					} else if (r == 2) {
						// WALL
						tiles[x][y] = TileEnum.MUD;
					}
				}
			}
		}
		return tiles;
	}


	
	// Getters and Setters
	
	public List<Entity> getEntities() {
		return entities;
	}

	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
	
	/** Set the neighboors.
	 * 
	 * @param nn
	 * @param sn
	 * @param wn
	 * @param en
	 */
	public void setNeighboors(Room nn, Room sn, Room wn, Room en) {
		this.northNeighboor = nn;
		this.southNeighboor = sn;
		this.westNeighboor = wn;
		this.easthNeighboor = en;
	}


	public Room getNorthNeighboor() {
		return northNeighboor;
	}


	public void setNorthNeighboor(Room northNeighboor) {
		this.northNeighboor = northNeighboor;
	}


	public Room getSouthNeighboor() {
		return southNeighboor;
	}


	public void setSouthNeighboor(Room southNeighboor) {
		this.southNeighboor = southNeighboor;
	}


	public Room getWestNeighboor() {
		return westNeighboor;
	}


	public void setWestNeighboor(Room westNeighboor) {
		this.westNeighboor = westNeighboor;
	}


	public Room getEasthNeighboor() {
		return easthNeighboor;
	}


	public void setEasthNeighboor(Room easthNeighboor) {
		this.easthNeighboor = easthNeighboor;
	}
	
}
