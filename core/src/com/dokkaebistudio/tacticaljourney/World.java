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

package com.dokkaebistudio.tacticaljourney;

import java.util.Arrays;
import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.dokkaebistudio.tacticaljourney.components.*;
import com.dokkaebistudio.tacticaljourney.components.TileComponent.TileEnum;

import static com.dokkaebistudio.tacticaljourney.GameScreen.GRID_H;
import static com.dokkaebistudio.tacticaljourney.GameScreen.GRID_W;

public class World {
	public static final int WORLD_STATE_RUNNING = 0;
	public static final int WORLD_STATE_NEXT_LEVEL = 1;
	public static final int WORLD_STATE_GAME_OVER = 2;

	public int state;
	public Entity[][] grid;
	
	private PooledEngine engine;
	// textures are stored so we don't fetch them from the atlas each time (atlas.findRegion is SLOW)
	private TextureAtlas.AtlasRegion wallTexture;
	private TextureAtlas.AtlasRegion playerTexture;
	private TextureAtlas.AtlasRegion pitTexture;
	private TextureAtlas.AtlasRegion groundTexture;

	public World (PooledEngine engine) {
		this.engine = engine;
		wallTexture = Assets.getTexture(Assets.tile_wall);
		groundTexture = Assets.getTexture(Assets.tile_ground);
		pitTexture = Assets.getTexture(Assets.tile_pit);
		playerTexture = Assets.getTexture(Assets.player);
	}
	
	public void create() {
		createBackground();
		createPlayer();
		generateLevel();

		this.state = WORLD_STATE_RUNNING;
	}

	private void createPlayer() {
		Entity playerEntity = engine.createEntity();

		TransformComponent position = engine.createComponent(TransformComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		WheelComponent baseWheelComponent = engine.createComponent(WheelComponent.class);

		baseWheelComponent.addSector(15, WheelComponent.Hit.MISS);
		baseWheelComponent.addSector(5, WheelComponent.Hit.GRAZE);
		baseWheelComponent.addSector(10, WheelComponent.Hit.HIT);
		baseWheelComponent.addSector(2, WheelComponent.Hit.CRITICAL);
		baseWheelComponent.addSector(10, WheelComponent.Hit.HIT);
		baseWheelComponent.addSector(5, WheelComponent.Hit.GRAZE);

		gridPosition.coord.set(4, 5); // default position

		texture.region = this.playerTexture;

		playerEntity.add(position);
		playerEntity.add(texture);
		playerEntity.add(gridPosition);
		playerEntity.add(baseWheelComponent);
		// he's the player !
		playerEntity.add(engine.createComponent(PlayerComponent.class));

		engine.addEntity(playerEntity);
	}

	private void generateLevel () {
		
	}
	
	
	private void createBackground() {
		TileEnum[][] generatedRoom = generateRoom();
		grid = new Entity[GRID_W][GameScreen.GRID_H];
		for (int x = 0; x < GRID_W; x++) {
			for (int y = 0; y < GameScreen.GRID_H; y++) {
				Entity tileEntity = engine.createEntity();
				TransformComponent position = engine.createComponent(TransformComponent.class);
				TextureComponent texture = engine.createComponent(TextureComponent.class);
				GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
				TileComponent tile = engine.createComponent(TileComponent.class);

				tile.type = generatedRoom[x][y];
				switch (generatedRoom[x][y]) {
					case WALL:
						texture.region = wallTexture;
						break;
					case GROUND:
						texture.region = groundTexture;
						break;
					case PIT:
						texture.region = pitTexture;
						break;
				}

				gridPosition.coord.set(x, y);

				tileEntity.add(position);
				tileEntity.add(texture);
				tileEntity.add(gridPosition);
				tileEntity.add(tile);

				engine.addEntity(tileEntity);
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
					}
				}
			}
		}
		return tiles;
	}
}
