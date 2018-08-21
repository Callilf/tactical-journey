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

import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dokkaebistudio.tacticaljourney.components.TextureComponent;
import com.dokkaebistudio.tacticaljourney.components.TransformComponent;

public class World {
	public static final int WORLD_STATE_RUNNING = 0;
	public static final int WORLD_STATE_NEXT_LEVEL = 1;
	public static final int WORLD_STATE_GAME_OVER = 2;
	public final Random rand;

	public int state;
	
	private PooledEngine engine;

	public World (PooledEngine engine) {
		this.engine = engine;
		this.rand = new Random();
	}
	
	public void create() {
		createBackground();
		generateLevel();

		this.state = WORLD_STATE_RUNNING;
	}

	private void generateLevel () {
		
	}
	
	
	private void createBackground() {
		Entity backgroundEntity = engine.createEntity();
		
		TransformComponent position = engine.createComponent(TransformComponent.class);
		TextureComponent texture = engine.createComponent(TextureComponent.class);
		
		texture.region = new TextureRegion(Assets.getTexture(Assets.background));
		
		backgroundEntity.add(position);
		backgroundEntity.add(texture);
		
		engine.addEntity(backgroundEntity);
	}
}
