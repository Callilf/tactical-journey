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

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.ParentRoomComponent;
import com.dokkaebistudio.tacticaljourney.components.WheelComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TransformComponent;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.rendering.MapRenderer;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.AnimationSystem;
import com.dokkaebistudio.tacticaljourney.systems.EnemyMoveSystem;
import com.dokkaebistudio.tacticaljourney.systems.KeyInputSystem;
import com.dokkaebistudio.tacticaljourney.systems.PlayerAttackSystem;
import com.dokkaebistudio.tacticaljourney.systems.PlayerMoveSystem;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.systems.WheelSystem;
import com.dokkaebistudio.tacticaljourney.systems.display.DamageDisplaySystem;
import com.dokkaebistudio.tacticaljourney.systems.display.RenderingSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class GameScreen extends ScreenAdapter {
	private static final int GAME_RUNNING = 1;
	private static final int GAME_LEVEL_END = 3;
	private static final int GAME_OVER = 4;

	// dimensions
	public static final int SCREEN_H = 1080;
	public static final int SCREEN_W = 1920;
	public static final int GRID_H = 13;
	public static final int GRID_W = 23;
	public static final int GRID_SIZE = 80;

	public static final int BOTTOM_MENU_HEIGHT = 40;
	public static final int LEFT_RIGHT_PADDING = 40;
	private static final Color HIT_COLOR = Color.GREEN;
	private static final Color MISS_COLOR = Color.BLACK;
	private static final Color CRITICAL_COLOR = Color.RED;
	private static final Color GRAZE_COLOR = Color.GRAY;
	private static final int WHEEL_RADIUS = 256;
	public static final int WHEEL_X = SCREEN_W / 2;
	public static final int WHEEL_Y = SCREEN_H/2;
	

	AttackWheel attackWheel = new AttackWheel();

	TacticalJourney game;

	public OrthographicCamera guiCam;
	Vector3 touchPoint;
	Floor floor;
	public EntityFactory entityFactory;
	Rectangle pauseBounds;
	Rectangle resumeBounds;
	Rectangle quitBounds;
	
	public PooledEngine engine;	
	private int state;
		
	private Entity timeDisplayer;
	private MapRenderer mapRenderer;
	
	public Entity player;

	public GameScreen (TacticalJourney game) {
		this.game = game;
		
		//Instanciate the RNG
		RandomSingleton.createInstance();

		// already running
		state = GAME_RUNNING;
		guiCam = new OrthographicCamera(SCREEN_W, SCREEN_H);
		guiCam.position.set(SCREEN_W / 2, SCREEN_H / 2, 0);
		
		//Instanciate the input processor
		InputSingleton.createInstance(guiCam);

		touchPoint = new Vector3();
		
		engine = new PooledEngine();
		this.entityFactory = new EntityFactory(this.engine);
		
		createTimeDisplayer();

		floor = new Floor(this, timeDisplayer);
		Room room = floor.getActiveRoom();
		
		mapRenderer = new MapRenderer(this, game.batcher, game.shapeRenderer, floor);
		mapRenderer.setMapDisplayed(true);
		
//		RandomXS128 random = RandomSingleton.getInstance().getRandom();
//		int x = 1 + random.nextInt(GameScreen.GRID_W - 2);
//		int y = 3 + random.nextInt(GameScreen.GRID_H - 5);
		player = entityFactory.createPlayer(new Vector2(11, 11), 5, room);
		
		engine.addSystem(new AnimationSystem(room));
		engine.addSystem(new RenderingSystem(game.batcher, room));
		engine.addSystem(new WheelSystem(attackWheel, room));
		engine.addSystem(new PlayerMoveSystem(room));
		engine.addSystem(new EnemyMoveSystem(room));
		engine.addSystem(new PlayerAttackSystem(room, attackWheel));
		engine.addSystem(new KeyInputSystem(room));
		engine.addSystem(new DamageDisplaySystem(room));
		
		engine.addSystem(room);
		engine.addSystem(mapRenderer);
		
		
		
		pauseBounds = new Rectangle(10, 10, 64, 64);
		resumeBounds = new Rectangle(160 - 96, 240, 192, 36);
		quitBounds = new Rectangle(160 - 96, 240 - 36, 192, 36);
				
		//Enter the first room
		enterRoom(room, null);
	}
	
	/**
	 * Enter a room.
	 * @param room the room we are entering in
	 */
	public void enterRoom(Room newRoom, Room oldRoom) {
		for (EntitySystem s : engine.getSystems()) {
			if (s instanceof RoomSystem) {
				((RoomSystem)s).enterRoom(newRoom);
			}
		}
		
		engine.removeSystem(oldRoom);
		engine.addSystem(newRoom);
		
		//Set the player in the new room
		ParentRoomComponent prc = Mappers.parentRoomComponent.get(player);
		prc.setParentRoom(newRoom);
	}

	public void update (float deltaTime) {
		if (deltaTime > 0.1f) deltaTime = 0.1f;

		engine.update(deltaTime);
		
		InputSingleton.getInstance().resetEvents();
		
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
	}

	private void presentRunning () {
		// draw the attack wheel
		drawAttackWheel();
		
		//Display map
		mapRenderer.renderMap();
	}
	
	/** Create the entity that displays the current game time. */
	private void createTimeDisplayer() {
		//Display time
		timeDisplayer = entityFactory.createText(new Vector3(0,0,100), "Time: ", null);
		TextComponent text = Mappers.textComponent.get(timeDisplayer);
		text.setText("Time: " + GameTimeSingleton.getInstance().getElapsedTime());
		TransformComponent transfo =  Mappers.transfoComponent.get(timeDisplayer);
		transfo.pos.set(300, 100, 100);
	}

	private void drawAttackWheel() {
		
		if (attackWheel.isDisplayed()) {
			// first normalize sector values
			int total = 0;
			List<Float> normalizeRanges = new LinkedList<Float>();
			for(WheelComponent.Sector s: attackWheel.getSectors()){
				total += s.range;
	
			}
			for(WheelComponent.Sector s: attackWheel.getSectors()){
				normalizeRanges.add(s.range * 360f / (float)total); // the sum of all ranges is 360 now
			}
			
			// begin render
			game.shapeRenderer.setProjectionMatrix(guiCam.combined);
			game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			// Draw a black circle behind (in case of missing sectors)
			game.shapeRenderer.setColor(HIT_COLOR);
			game.shapeRenderer.arc(WHEEL_X, WHEEL_Y, WHEEL_RADIUS-1, 0, 360);
			
			int rangeCumul = 0;
			
			for(int i = 0; i< attackWheel.getSectors().size(); i++) {
				// color
				switch (attackWheel.getSectors().get(i).hit){
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
			

			guiCam.update();
			game.batcher.setProjectionMatrix(guiCam.combined);
			game.batcher.begin();
			
//			int rangeCumul = 0;
//			for (Sector s : attackWheel.getSectors()) {
//				for (int i=rangeCumul ; i < rangeCumul + s.range ; i++) {
//					Sprite sprite = attackWheel.getArcs().get(i);
//					switch (s.hit) {
//					case HIT:
//						sprite.setColor(HIT_COLOR);
//						break;
//					case CRITICAL:
//						sprite.setColor(CRITICAL_COLOR);
//						break;
//					case GRAZE:
//						sprite.setColor(GRAZE_COLOR);
//						break;
//					case MISS:
//						sprite.setColor(MISS_COLOR);
//						break;
//					}
//				}
//				rangeCumul += s.range;
//			}
//			
//			List<Sprite> reversedArcs = new ArrayList<>();
//			reversedArcs.addAll(attackWheel.getArcs());
//			Collections.reverse(reversedArcs);
//			for (Sprite arc : reversedArcs) {
//				arc.draw(game.batcher);
//			}
			
			
			// Render the arrow
			Sprite arrow = attackWheel.getArrow();
			arrow.setPosition(WHEEL_X - arrow.getWidth()/2, WHEEL_Y - arrow.getHeight()/2);
			arrow.draw(game.batcher);
			
			game.batcher.end();
			
		} 


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