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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.GameTimeSingleton;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.room.generation.GeneratedRoom;
import com.dokkaebistudio.tacticaljourney.room.generation.RoomGenerator;
import com.dokkaebistudio.tacticaljourney.room.managers.AttackManager;
import com.dokkaebistudio.tacticaljourney.room.managers.TurnManager;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class Room extends EntitySystem {
	public Floor floor;
	
	public RoomType type;
	
	public RoomState state;
	public Entity[][] grid;
	public List<Vector2> possibleSpawns;
	
	public PooledEngine engine;
	public EntityFactory entityFactory;
	
	public TurnManager turnManager;

	public AttackManager attackManager;
	
	/** The entities of this room. */
	private List<Entity> enemies;
	
	private Entity timeDisplayer;
	
	/** Whether the player has already entered this room or not. */
	private boolean visited;
	
	
	private Room northNeighbor;
	private Room southNeighbor;
	private Room westNeighbor;
	private Room eastNeighbor;
	

	public Room (Floor f, PooledEngine engine, EntityFactory ef, Entity timeDisplayer, RoomType type) {
		this.floor = f;
		this.engine = engine;
		this.entityFactory = ef;
		this.timeDisplayer = timeDisplayer;
		this.turnManager = new TurnManager(this);
		this.type = type;
		this.visited = false;
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
		this.state = RoomState.PLAYER_TURN_INIT;

		enemies = new ArrayList<>();
		attackManager = new AttackManager(this);
		createLayout();
		createContent();
	}




	/**
	 * Create the grid, ie. fille the 2 dimensional array of tile entities.
	 */
	private void createLayout() {
		RoomGenerator generator = new RoomGenerator(this.entityFactory);
		GeneratedRoom generateRoom = generator.generateRoom(this, this.northNeighbor, this.eastNeighbor, this.southNeighbor, this.westNeighbor);
		grid = generateRoom.getTileEntities();
		possibleSpawns = generateRoom.getPossibleSpawns();
	}
	
	
	private void createContent() {
		switch(type) {
		case COMMON_ENEMY_ROOM :
			RandomXS128 random = RandomSingleton.getInstance().getRandom();
			int enemyNb = random.nextInt(Math.min(possibleSpawns.size(), 5));
			
			// Retrieve the spawn points and shuffle them
			List<Vector2> enemyPositions = new ArrayList<>(possibleSpawns);
			Collections.shuffle(enemyPositions, random);
			
			// Place enemies
			Iterator<Vector2> iterator = enemyPositions.iterator();
			for (int i=0 ; i<enemyNb ; i++) {
				Entity enemy = entityFactory.enemyFactory.createSpider(this, new Vector2(iterator.next()), 3);
				enemies.add(enemy);
				iterator.remove();
			}
			
			// Place health
			if (iterator.hasNext()) {
				entityFactory.createItemHealthUp(this, new Vector2(iterator.next()));
			}
			break;
			
		case START_FLOOR_ROOM:
		case END_FLOOR_ROOM:
			default:
			break;
		}
	}
	
	
	/**
	 * Return the entity for the tile at the given position.
	 * @param x the abscissa
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
	
	/**
	 * Remove an enemy from the room.
	 * @param enemy the enemy to remove
	 */
	public void removeEnemy(Entity enemy) {
		this.engine.removeEntity(enemy);
		this.enemies.remove(enemy);
	}
	
	/**
	 * @return true if there are remaining enemies in this room.
	 */
	public boolean hasEnemies() {
		return this.enemies.size() > 0;
	}
	
	
	
	// Neighbors
	
	/** Set the neighbors.
	 * 
	 * @param nn
	 * @param sn
	 * @param wn
	 * @param en
	 */
	public void setNeighbors(Room nn, Room sn, Room wn, Room en) {
		this.northNeighbor = nn;
		this.southNeighbor = sn;
		this.westNeighbor = wn;
		this.eastNeighbor = en;
	}
	
	/**
	 * @return the number of neighbors for the current room.
	 */
	public int getNumberOfNeighbors() {
		int nb = 0;
		if (northNeighbor != null) nb ++;
		if (southNeighbor != null) nb ++;
		if (westNeighbor != null) nb ++;
		if (eastNeighbor != null) nb ++;
		return nb;
	}


	public Room getNorthNeighbor() {
		return northNeighbor;
	}


	public void setNorthNeighbor(Room northNeighbor) {
		this.northNeighbor = northNeighbor;
	}


	public Room getSouthNeighbor() {
		return southNeighbor;
	}


	public void setSouthNeighbor(Room southNeighbor) {
		this.southNeighbor = southNeighbor;
	}


	public Room getWestNeighbor() {
		return westNeighbor;
	}


	public void setWestNeighbor(Room westNeighbor) {
		this.westNeighbor = westNeighbor;
	}


	public Room getEastNeighbor() {
		return eastNeighbor;
	}


	public void setEastNeighbor(Room easthNeighbor) {
		this.eastNeighbor = easthNeighbor;
	}


	public boolean isVisited() {
		return visited;
	}


	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
}
