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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.GameTimeSingleton;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.TileComponent.TileEnum;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.room.generation.GeneratedRoom;
import com.dokkaebistudio.tacticaljourney.room.generation.RoomGenerator;
import com.dokkaebistudio.tacticaljourney.room.managers.AttackManager;
import com.dokkaebistudio.tacticaljourney.room.managers.TurnManager;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class Room extends EntitySystem {
	public Floor floor;
	
	public RoomState state;
	public Entity[][] grid;
	public List<Vector2> possibleSpawns;
	
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
	

	public Room (Floor f, PooledEngine engine, EntityFactory ef, Entity timeDisplayer) {
		this.floor = f;
		this.engine = engine;
		this.entityFactory = ef;
		this.timeDisplayer = timeDisplayer;
		this.turnManager = new TurnManager(this);
	}

	
	public void leaveRoom(Room nextRoom) {
		this.state = RoomState.PLAYER_TURN_INIT;
		this.floor.enterRoom(nextRoom);
	}
	
	
	@Override
	public void update(float deltaTime) {
		GameTimeSingleton gtSingleton = GameTimeSingleton.getInstance();
		gtSingleton.updateElapsedTime(deltaTime);
		
		TextComponent text = Mappers.textComponent.get(timeDisplayer);
		text.setText("Time: " + String.format("%.1f", gtSingleton.getElapsedTime()));
	}
	
	
	public void create() {
		entities = new ArrayList<>();
		attackManager = new AttackManager(this);
		createGrid();
		
		this.state = RoomState.PLAYER_TURN_INIT;
				
		RandomXS128 random = RandomSingleton.getInstance().getRandom();
		int enemyNb = random.nextInt(Math.min(possibleSpawns.size(), 5));
		
		List<Vector2> enemyPositions = new ArrayList<>(possibleSpawns);
		Collections.shuffle(enemyPositions, random);
		
		Iterator<Vector2> iterator = enemyPositions.iterator();
		for (int i=0 ; i<enemyNb ; i++) {
			entityFactory.enemyFactory.createSpider(this, new Vector2(iterator.next()), 3);
			iterator.remove();
		}
		
		
//		int x2 = 1 + random.nextInt(GameScreen.GRID_W - 2);
//		int y2 = 3 + random.nextInt(GameScreen.GRID_H - 5);
//		Entity spider1 = entityFactory.enemyFactory.createSpider(this, new Vector2(x2,y2), 3);
//		
//		int x3 = 1 + random.nextInt(GameScreen.GRID_W - 2);
//		int y3 = 3 + random.nextInt(GameScreen.GRID_H - 5);
//		Entity spider2 = entityFactory.enemyFactory.createSpider(this, new Vector2(x3,y3), 3);
//		
//		int x4 = 1 + random.nextInt(GameScreen.GRID_W - 2);
//		int y4 = 3 + random.nextInt(GameScreen.GRID_H - 5);
//		Entity scorpion = entityFactory.enemyFactory.createScorpion(this, new Vector2(x4,y4), 4);
		
		if (iterator.hasNext()) {
			entityFactory.createItemHealthUp(this, new Vector2(iterator.next()));
		}
	}

	/**
	 * Create the grid, ie. fille the 2 dimensional array of tile entities.
	 */
	private void createGrid() {
		RoomGenerator generator = new RoomGenerator(this.entityFactory);
		GeneratedRoom generateRoom = generator.generateRoom(this, this.northNeighboor, this.easthNeighboor, this.southNeighboor, this.westNeighboor);
		grid = generateRoom.getTileEntities();
		possibleSpawns = generateRoom.getPossibleSpawns();
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

//	/**
//	 * Generates a random room by creating an array of {@link TileEnum}. There are walls on each border of the room.
//	 */
//	private TileEnum[][] generateRoom() {
//		TileEnum[][] tiles = new TileEnum[GRID_W][GRID_H];
//		// fill with ground first
//		for(TileEnum[] tileCol: tiles){
//			Arrays.fill(tileCol, TileEnum.GROUND);
//		}
//		for (int x = 0; x < GRID_W; x++) {
//			for (int y = 0; y < GameScreen.GRID_H; y++) {
//				if (x == 0 || x == GRID_W-1 || y == 0 || y == 1 || y == GRID_H - 2 || y == GRID_H - 1) {
//					
//					//Spaces for doors
//					if ( (x == 0 && y== GRID_H/2) ) {
//						tiles[x][y] = TileEnum.GROUND;
//						Entity door = entityFactory.createDoor(this, new Vector2(x,y), westNeighboor);
//					} else if ( x== GRID_W-1 && y== GRID_H/2) {
//						tiles[x][y] = TileEnum.GROUND;
//						Entity door = entityFactory.createDoor(this, new Vector2(x,y), easthNeighboor);
//					} else if ( x == GRID_W/2 && y == 1) {
//						tiles[x][y] = TileEnum.GROUND;
//						Entity door = entityFactory.createDoor(this, new Vector2(x,y), southNeighboor);
//					} else if ( x == GRID_W/2 && y == GRID_H-2) {
//						tiles[x][y] = TileEnum.GROUND;
//						Entity door = entityFactory.createDoor(this, new Vector2(x,y), northNeighboor);
//					} else {
//						// walls
//						tiles[x][y] = TileEnum.WALL;
//					}
//				} else {
//					// generate some random walls and pits
//					RandomXS128 random = RandomSingleton.getInstance().getRandom();
//					int r = random.nextInt(15);
//					if (r == 0) {
//						// PIT
//						tiles[x][y] = TileEnum.PIT;
//					} else if (r == 1) {
//						// WALL
//						tiles[x][y] = TileEnum.WALL;
//					} else if (r == 2) {
//						// WALL
//						tiles[x][y] = TileEnum.MUD;
//					}
//				}
//			}
//		}
//		return tiles;
//	}


	
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
