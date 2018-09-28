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

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.components.WheelComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.AnimationSystem;
import com.dokkaebistudio.tacticaljourney.systems.EnemyMoveSystem;
import com.dokkaebistudio.tacticaljourney.systems.KeyInputMovementSystem;
import com.dokkaebistudio.tacticaljourney.systems.PlayerMoveSystem;
import com.dokkaebistudio.tacticaljourney.systems.RenderingSystem;
import com.dokkaebistudio.tacticaljourney.systems.WheelSystem;

import java.util.LinkedList;
import java.util.List;

public class GameScreen extends ScreenAdapter {
	private static final int GAME_RUNNING = 1;
	private static final int GAME_LEVEL_END = 3;
	private static final int GAME_OVER = 4;

	// dimensions
	public static final int SCREEN_H = 1080;
	public static final int SCREEN_W = 1920;
	public static final int GRID_H = 16;
	public static final int GRID_W = 28;
	public static final int GRID_SIZE = 64;

	public static final int BOTTOM_MENU_HEIGHT = 56;
	public static final int LEFT_RIGHT_PADDING = 64;
	private static final Color HIT_COLOR = Color.GREEN;
	private static final Color MISS_COLOR = Color.BLACK;
	private static final Color CRITICAL_COLOR = Color.RED;
	private static final Color GRAZE_COLOR = Color.GRAY;
	private static final int WHEEL_X = SCREEN_W / 2;
	private static final int WHEEL_Y = 128;
	private static final int WHEEL_RADIUS = 64;

	List<WheelComponent.Sector> attackWheel = new LinkedList<WheelComponent.Sector>();

	TacticalJourney game;

	OrthographicCamera guiCam;
	Vector3 touchPoint;
	Room room;
	Rectangle pauseBounds;
	Rectangle resumeBounds;
	Rectangle quitBounds;
	
	PooledEngine engine;	
	private int state;

	public GameScreen (TacticalJourney game) {
		this.game = game;

		// already running
		state = GAME_RUNNING;
		guiCam = new OrthographicCamera(SCREEN_W, SCREEN_H);
		guiCam.position.set(SCREEN_W / 2, SCREEN_H / 2, 0);
		touchPoint = new Vector3();
		
		engine = new PooledEngine();
		
		room = new Room(engine);
		
		engine.addSystem(new AnimationSystem());
		engine.addSystem(new RenderingSystem(game.batcher));
		engine.addSystem(new PlayerMoveSystem(room));
		engine.addSystem(new EnemyMoveSystem(room));
		engine.addSystem(new KeyInputMovementSystem(room));
		engine.addSystem(new WheelSystem(attackWheel));
		
		
		room.create();
		
		pauseBounds = new Rectangle(10, 10, 64, 64);
		resumeBounds = new Rectangle(160 - 96, 240, 192, 36);
		quitBounds = new Rectangle(160 - 96, 240 - 36, 192, 36);
	}

	public void update (float deltaTime) {
		if (deltaTime > 0.1f) deltaTime = 0.1f;

		engine.update(deltaTime);
		
		switch (state) {
		case GAME_RUNNING:
			updateRunning(deltaTime);
			break;
		case GAME_LEVEL_END:
			updateLevelEnd();
			break;
		case GAME_OVER:
			updateGameOver();
			break;
		}
	}

	private void updateRunning (float deltaTime) {
		if (Gdx.input.justTouched()) {
			// TODO do nothing yet
		}
	}


	private void updateLevelEnd () {
		if (Gdx.input.justTouched()) {

		}
	}

	private void updateGameOver () {
		if (Gdx.input.justTouched()) {
			game.setScreen(new MainMenuScreen(game));
		}
	}

	public void drawUI () {
		guiCam.update();
		game.batcher.setProjectionMatrix(guiCam.combined);
		game.batcher.begin();
		switch (state) {
		case GAME_RUNNING:
			presentRunning();
			break;
		case GAME_LEVEL_END:
			presentLevelEnd();
			break;
		case GAME_OVER:
			presentGameOver();
			break;
		}
		game.batcher.end();
	}

	private void presentRunning () {
		// draw the attack wheel
		drawAttackWheel();
	}

	private void drawAttackWheel() {
		// first normalize sector values
		int total = 0;
		List<Float> normalizeRanges = new LinkedList<Float>();
		for(WheelComponent.Sector s: attackWheel){
			total += s.range;

		}
		for(WheelComponent.Sector s: attackWheel){
			normalizeRanges.add(s.range * 360f / (float)total); // the sum of all ranges is 360 now
		}
		// begin render
		game.shapeRenderer.setProjectionMatrix(guiCam.combined);
		game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		int rangeCumul = 0;
		for(int i = 0; i< attackWheel.size(); i++) {
			// color
			switch (attackWheel.get(i).hit){
				case HIT:
					game.shapeRenderer.setColor(HIT_COLOR);
					break;
				case CRITICAL:
					game.shapeRenderer.setColor(CRITICAL_COLOR);
					break;
				case GRAZE:
					game.shapeRenderer.setColor(GRAZE_COLOR);
					break;
				case MISS:
					game.shapeRenderer.setColor(MISS_COLOR);
					break;
			}
			// draw arc
			game.shapeRenderer.arc(WHEEL_X, WHEEL_Y, WHEEL_RADIUS, rangeCumul, normalizeRanges.get(i));
			// next arc starts at the end of previous arc
			rangeCumul += normalizeRanges.get(i);
		}
		game.shapeRenderer.end();



	}

	private void presentLevelEnd () {
	}

	private void presentGameOver () {
	}

	@Override
	public void render (float delta) {
		update(delta);
		drawUI();
	}

	@Override
	public void pause () {
		// TODO nothing yet
	}
}