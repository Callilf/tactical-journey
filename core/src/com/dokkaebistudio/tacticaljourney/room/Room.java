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

import java.util.Arrays;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.EntityFactory;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.TileComponent.TileEnum;

public class Room {
	public RoomState state;
	public Entity[][] grid;
	
	public PooledEngine engine;
	public EntityFactory entityFactory;
	
	public TurnManager turnManager;
	public AttackManager attackManager;

	public Room (PooledEngine engine) {
		this.engine = engine;
		this.entityFactory = new EntityFactory(this.engine);
	}
	
	public void create() {
		turnManager = new TurnManager(this);
		attackManager = new AttackManager(this);
		createGrid();
		
		this.state = RoomState.PLAYER_TURN_INIT;
		
		
		RandomXS128 random = RandomSingleton.getInstance().getRandom();
		int x = 1 + random.nextInt(GameScreen.GRID_W - 2);
		int y = 3 + random.nextInt(GameScreen.GRID_H - 4);
		entityFactory.createPlayer(new Vector2(x,y), 5);
		
		int x2 = 1 + random.nextInt(GameScreen.GRID_W - 2);
		int y2 = 3 + random.nextInt(GameScreen.GRID_H - 4);
		entityFactory.createSpider(new Vector2(x2,y2), 3);
		
		int x3 = 1 + random.nextInt(GameScreen.GRID_W - 2);
		int y3 = 3 + random.nextInt(GameScreen.GRID_H - 4);
		entityFactory.createSpider(new Vector2(x3,y3), 3);
		
		int x4 = 1 + random.nextInt(GameScreen.GRID_W - 2);
		int y4 = 3 + random.nextInt(GameScreen.GRID_H - 4);
		entityFactory.createScorpion(new Vector2(x4,y4), 4);
		
		int x5 = 1 + random.nextInt(GameScreen.GRID_W - 2);
		int y5 = 3 + random.nextInt(GameScreen.GRID_H - 4);
		entityFactory.createItemHealthUp(new Vector2(x5,y5));
	}

	/**
	 * Create the grid, ie. fille the 2 dimensional array of tile entities.
	 */
	private void createGrid() {
		TileEnum[][] generatedRoom = generateRoom();
		grid = new Entity[GRID_W][GameScreen.GRID_H];
		for (int x = 0; x < GRID_W; x++) {
			for (int y = 0; y < GameScreen.GRID_H; y++) {
				Entity tileEntity = entityFactory.createTile(new Vector2(x, y), generatedRoom[x][y]);
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
					// walls
					tiles[x][y] = TileEnum.WALL;
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
}
