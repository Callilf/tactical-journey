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

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.rendering.HUDRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.LevelUpPopinRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.MapRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.Renderer;
import com.dokkaebistudio.tacticaljourney.rendering.RoomRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.WheelRenderer;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.AnimationSystem;
import com.dokkaebistudio.tacticaljourney.systems.EnemySystem;
import com.dokkaebistudio.tacticaljourney.systems.ExperienceSystem;
import com.dokkaebistudio.tacticaljourney.systems.ExplosionSystem;
import com.dokkaebistudio.tacticaljourney.systems.PlayerAttackSystem;
import com.dokkaebistudio.tacticaljourney.systems.PlayerMoveSystem;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.systems.StateSystem;
import com.dokkaebistudio.tacticaljourney.systems.TurnSystem;
import com.dokkaebistudio.tacticaljourney.systems.WheelSystem;
import com.dokkaebistudio.tacticaljourney.systems.display.DamageDisplaySystem;
import com.dokkaebistudio.tacticaljourney.systems.display.VisualEffectSystem;
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
	

	TacticalJourney game;

	public FitViewport viewport;
	public OrthographicCamera guiCam;
	public Stage stage;
	
	public FitViewport hudViewport;
	public Stage hudStage;


	Vector3 touchPoint;
	
	Floor floor;
	public EntityFactory entityFactory;
	Rectangle pauseBounds;
	Rectangle resumeBounds;
	Rectangle quitBounds;
	
	public PooledEngine engine;	
	private int state;
	
	AttackWheel attackWheel = new AttackWheel();
	
	/** The list of renderers. */
	private List<Renderer> renderers = new ArrayList<>();
	
	public Entity player;

	public GameScreen (TacticalJourney game) {
		this.game = game;
		
		//Instanciate the RNG
		RandomSingleton.createInstance();

		// already running
		state = GAME_RUNNING;
		guiCam = new OrthographicCamera(SCREEN_W, SCREEN_H);
		guiCam.position.set(SCREEN_W / 2, SCREEN_H / 2, 0);
		viewport = new FitViewport(SCREEN_W, SCREEN_H, guiCam);
		hudViewport = new FitViewport(SCREEN_W, SCREEN_H, guiCam);
		
		/// create stage and set it as input processor
		stage = new Stage(viewport);
		hudStage = new Stage(hudViewport);
		
		//Instanciate the input processor
		InputSingleton.createInstance(guiCam, viewport);
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(stage);
		inputMultiplexer.addProcessor(hudStage);
		inputMultiplexer.addProcessor(InputSingleton.getInstance());
		Gdx.input.setInputProcessor(inputMultiplexer);

		touchPoint = new Vector3();
		
		engine = new PooledEngine();
		this.entityFactory = new EntityFactory(this.engine);
		
		floor = new Floor(this);
		Room room = floor.getActiveRoom();
		player = entityFactory.playerFactory.createPlayer(new Vector2(11, 11), 5, room);

		
		renderers.add(new RoomRenderer(game.batcher, room, guiCam));
		renderers.add(new HUDRenderer(hudStage, player));
		renderers.add(new MapRenderer(this, hudStage,game.batcher, game.shapeRenderer, floor));
		renderers.add(new WheelRenderer(attackWheel, this, game.batcher, game.shapeRenderer));
		renderers.add(new LevelUpPopinRenderer(room, stage, player));
		
		engine.addSystem(room);
		engine.addSystem(new StateSystem());
		engine.addSystem(new AnimationSystem(room));
		engine.addSystem(new VisualEffectSystem(room));
		engine.addSystem(new TurnSystem(room));
		engine.addSystem(new WheelSystem(attackWheel, room));
		engine.addSystem(new ExplosionSystem(room));
		engine.addSystem(new PlayerMoveSystem(room));
		engine.addSystem(new EnemySystem(room));
		engine.addSystem(new PlayerAttackSystem(room, attackWheel));
		engine.addSystem(new DamageDisplaySystem(room));
		engine.addSystem(new ExperienceSystem(room, stage));
		

		
		
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
		for (Renderer r : renderers) {
			if (r instanceof RoomSystem) {
				((RoomSystem)r).enterRoom(newRoom);
			}
		}
		
		for (EntitySystem s : engine.getSystems()) {
			if (s instanceof RoomSystem) {
				((RoomSystem)s).enterRoom(newRoom);
			}
		}
		
		engine.removeSystem(oldRoom);
		engine.addSystem(newRoom);
		
		
		//TODO : probably improve this code, especially if any other entity than the player can travel between rooms
		//Set the player in the new room	
		updateRoomForComponents(player, newRoom);
		
		PlayerComponent playerComponent = Mappers.playerComponent.get(player);
		updateRoomForComponents(playerComponent.getSkillMelee(), newRoom);
		updateRoomForComponents(playerComponent.getSkillRange(), newRoom);
		updateRoomForComponents(playerComponent.getSkillBomb(), newRoom);
	}

	private void updateRoomForComponents(Entity e, Room newRoom) {
		for (Component compo : e.getComponents()) {
			if (compo instanceof RoomSystem) {
				((RoomSystem)compo).enterRoom(newRoom);
			}
		}
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

	public void draw (float delta) {
		switch (state) {
		case GAME_RUNNING:
			presentRunning(delta);
			break;
		case GAME_LEVEL_END:
			presentLevelEnd();
			break;
		case GAME_OVER:
			presentGameOver();
			break;
		}
	}

	private void presentRunning (float delta) {
		for (Renderer r : renderers) {
			r.render(delta);
		}
	}


	private void presentLevelEnd () {
	}

	private void presentGameOver () {
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
		hudViewport.update(width, height);
	}

	
	@Override
	public void render (float delta) {
		//draw black background
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// 1 - Update all entities and systems
		update(delta);
		
		// 2 - Render on screen
		draw(delta);
	}

	@Override
	public void pause () {
		// TODO nothing yet
	}
	
	@Override
	public void dispose() {
		Assets.getInstance().dispose();
		stage.dispose();
		hudStage.dispose();
		game.dispose();
		super.dispose();
	}
}