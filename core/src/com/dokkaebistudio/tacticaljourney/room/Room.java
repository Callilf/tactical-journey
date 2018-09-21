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
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.EntityFactory;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.TileComponent.TileEnum;

public class Room {
	public RoomState state;
	public Entity[][] grid;
	
	public PooledEngine engine;
	public EntityFactory entityFactory;

	public Room (PooledEngine engine) {
		this.engine = engine;
		this.entityFactory = new EntityFactory(this.engine);
	}
	
	public void create() {
		createGrid();
		entityFactory.createPlayer(new Vector2(4,5), 5);

		this.state = RoomState.PLAYER_MOVE_START;
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
				if (x == 0 || x == GRID_W-1 || y == 0 || y == GRID_H - 1) {
					// walls
					tiles[x][y] = TileEnum.WALL;
				} else {
					// generate some random walls and pits
					int random = MathUtils.random(9);
					if (random == 0) {
						// PIT
						tiles[x][y] = TileEnum.PIT;
					} else if (random == 1) {
						// WALL
						tiles[x][y] = TileEnum.WALL;
					} else if (random == 2) {
						// WALL
						tiles[x][y] = TileEnum.MUD;
					}
				}
			}
		}
		return tiles;
	}
}
