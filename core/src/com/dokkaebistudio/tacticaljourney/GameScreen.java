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
import com.dokkaebistudio.tacticaljourney.ashley.PublicPooledEngine;
import com.dokkaebistudio.tacticaljourney.assets.SceneAssets;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.mainmenu.MainMenuScreen;
import com.dokkaebistudio.tacticaljourney.persistence.Persister;
import com.dokkaebistudio.tacticaljourney.rendering.ContextualActionPopinRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.DebugPopinRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.DialogRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.GameOverPopinRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.HUDRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.InspectPopinRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.InventoryPopinRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.ItemPopinRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.JournalRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.LevelUpPopinRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.LootPopinRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.MapRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.MenuPopinRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.ProfilePopinRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.RoomRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.StatusPopinRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.WheelRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.WinPopinRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.singletons.GameTimeSingleton;
import com.dokkaebistudio.tacticaljourney.singletons.InputSingleton;
import com.dokkaebistudio.tacticaljourney.systems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.systems.AnimationSystem;
import com.dokkaebistudio.tacticaljourney.systems.ContextualActionSystem;
import com.dokkaebistudio.tacticaljourney.systems.CreepSystem;
import com.dokkaebistudio.tacticaljourney.systems.DialogSystem;
import com.dokkaebistudio.tacticaljourney.systems.EnemySystem;
import com.dokkaebistudio.tacticaljourney.systems.ExperienceSystem;
import com.dokkaebistudio.tacticaljourney.systems.ExplosionSystem;
import com.dokkaebistudio.tacticaljourney.systems.HealthSystem;
import com.dokkaebistudio.tacticaljourney.systems.InspectSystem;
import com.dokkaebistudio.tacticaljourney.systems.ItemSystem;
import com.dokkaebistudio.tacticaljourney.systems.OrbSystem;
import com.dokkaebistudio.tacticaljourney.systems.PlayerAttackSystem;
import com.dokkaebistudio.tacticaljourney.systems.PlayerMoveSystem;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.systems.ShopSystem;
import com.dokkaebistudio.tacticaljourney.systems.SoulbenderSystem;
import com.dokkaebistudio.tacticaljourney.systems.StateSystem;
import com.dokkaebistudio.tacticaljourney.systems.StatusSystem;
import com.dokkaebistudio.tacticaljourney.systems.TurnSystem;
import com.dokkaebistudio.tacticaljourney.systems.WheelSystem;
import com.dokkaebistudio.tacticaljourney.systems.display.VisualEffectSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.wheel.AttackWheel;

public class GameScreen extends ScreenAdapter {
	public static final int GAME_RUNNING = 1;
	public static final int GAME_PAUSED = 2;
	public static final int GAME_COMPLETED = 3;
	public static final int GAME_OVER = 4;

	// dimensions
	public static final int SCREEN_H = 1080;
	public static final int SCREEN_W = 1920;
	public static final int GRID_H = 13;
	public static final int GRID_W = 23;
	public static final int GRID_SIZE = 80;

	public static final int BOTTOM_MENU_HEIGHT = 40;
	public static final int LEFT_RIGHT_PADDING = 40;
	
	public static final boolean debugMode = true;
	

	public TacticalJourney game;

	public FitViewport viewport;
	public OrthographicCamera guiCam;
	public Stage stage;
	public Stage inventoryStage;
	public Stage fxStage;
	public Stage foregroundFxStage;

	
	public FitViewport hudViewport;
	public Stage hudStage;
	public Stage miniMapStage;
	public Stage journalStage;
	public Stage menuStage;



	Vector3 touchPoint;
	
	public List<Floor> floors;
	public Floor activeFloor;
	Floor requestedFloor;
	
	public EntityFactory entityFactory;
	Rectangle pauseBounds;
	Rectangle resumeBounds;
	Rectangle quitBounds;
	
	public PublicPooledEngine engine;	
	public int state;
	
	AttackWheel attackWheel = new AttackWheel();
	
	/** The list of renderers. */
	private List<Renderer> renderers = new ArrayList<>();
	private MapRenderer mapRenderer;
	
	public Entity player;
	
	/** The name of the entity that killed the player. */
	public String killerStr;
	
	public GameScreen (TacticalJourney game, boolean newGame, String playerName) {
		this.game = game;
		
		Gdx.input.setCatchBackKey(true);

		// already running
		state = GAME_RUNNING;
		guiCam = new OrthographicCamera(SCREEN_W, SCREEN_H);
		guiCam.position.set(SCREEN_W / 2, SCREEN_H / 2, 0);
		viewport = new FitViewport(SCREEN_W, SCREEN_H, guiCam);
		hudViewport = new FitViewport(SCREEN_W, SCREEN_H, guiCam);
		
		/// create stage and set it as input processor
		stage = new Stage(viewport);
		fxStage = new Stage(viewport);
		inventoryStage = new Stage(hudViewport);
		hudStage = new Stage(hudViewport);
		menuStage = new Stage(hudViewport);
		miniMapStage = new Stage(hudViewport);
		journalStage = new Stage(hudViewport);
		foregroundFxStage = new Stage(viewport);

		
		//Instanciate the input processor
		InputSingleton.createInstance(this,guiCam, viewport);
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(menuStage);
		inputMultiplexer.addProcessor(stage);
		inputMultiplexer.addProcessor(inventoryStage);
		inputMultiplexer.addProcessor(hudStage);
		inputMultiplexer.addProcessor(miniMapStage);
		inputMultiplexer.addProcessor(journalStage);
		inputMultiplexer.addProcessor(InputSingleton.getInstance());
		Gdx.input.setInputProcessor(inputMultiplexer);

		touchPoint = new Vector3();
		
		engine = new PublicPooledEngine();
		this.entityFactory = new EntityFactory(this.engine);
		
		floors = new ArrayList<>();
		
		if (newGame) {
			Floor floor1 = new Floor(this, 1);
			floor1.generate();
			floors.add(floor1);
			activeFloor = floor1;
			Floor floor2 = new Floor(this, 2);
			floors.add(floor2);
			Floor floor3 = new Floor(this, 3);
			floors.add(floor3);
			Floor floor4 = new Floor(this, 4);
			floors.add(floor4);
			Floor floor5 = new Floor(this, 5);
			floors.add(floor5);

			
			player = entityFactory.playerFactory.createPlayer(playerName, new Vector2(11, 11), 5, floor1.getActiveRoom());
		} else {
			Persister persister = new Persister(this);
			persister.loadGameState();
		}

		Room room = activeFloor.getActiveRoom();

		
		
		
		mapRenderer = new MapRenderer(miniMapStage, activeFloor);
		renderers.add(new RoomRenderer(fxStage,game.batcher, room, guiCam));
		renderers.add(new HUDRenderer(hudStage, player));
		renderers.add(mapRenderer);
		renderers.add(new JournalRenderer(journalStage));
		renderers.add(new DialogRenderer(room, stage));
		renderers.add(new WheelRenderer(attackWheel, this, game.batcher, game.shapeRenderer, hudStage));
		renderers.add(new ContextualActionPopinRenderer(room, stage, player));
		renderers.add(new InspectPopinRenderer(room, stage, player));
		renderers.add(new ItemPopinRenderer(room, stage, player));
		renderers.add(new InventoryPopinRenderer(room, inventoryStage, player));
		renderers.add(new LootPopinRenderer(room, inventoryStage, player));
		renderers.add(new ProfilePopinRenderer(room, stage, player));
		renderers.add(new StatusPopinRenderer(room, stage));
		if (debugMode) { renderers.add(new DebugPopinRenderer(room, inventoryStage, player)); }
		renderers.add(new LevelUpPopinRenderer(room, stage, player));
		renderers.add(new WinPopinRenderer(this, menuStage));
		renderers.add(new GameOverPopinRenderer(this, menuStage));
		renderers.add(new MenuPopinRenderer(this, menuStage));

		

		
		engine.addSystem(room);
		engine.addSystem(new StateSystem());
		engine.addSystem(new AnimationSystem(room));
		engine.addSystem(new VisualEffectSystem(room));
		engine.addSystem(new TurnSystem(room));
		engine.addSystem(new WheelSystem(attackWheel, player, room));
		engine.addSystem(new ExplosionSystem(room, fxStage));
		engine.addSystem(new CreepSystem(this, room, fxStage, player));
		engine.addSystem(new EnemySystem(room, fxStage));
		engine.addSystem(new PlayerAttackSystem(fxStage,room, attackWheel));
		engine.addSystem(new PlayerMoveSystem(room));
		engine.addSystem(new ItemSystem(	player, room, foregroundFxStage));
		engine.addSystem(new AlterationSystem(player, room, foregroundFxStage));
		engine.addSystem(new StatusSystem(player, room, fxStage));
		engine.addSystem(new ShopSystem(	player, room, fxStage));
		engine.addSystem(new SoulbenderSystem(	player, room, fxStage));
		engine.addSystem(new ContextualActionSystem(	player, room));
		engine.addSystem(new DialogSystem(room));
		engine.addSystem(new ExperienceSystem(room, stage));
		engine.addSystem(new OrbSystem(player, room, stage));
		engine.addSystem(new HealthSystem(this,room, fxStage));
		
		engine.addSystem(new InspectSystem(player, room, fxStage));
		
		

		

		
		
		pauseBounds = new Rectangle(10, 10, 64, 64);
		resumeBounds = new Rectangle(160 - 96, 240, 192, 36);
		quitBounds = new Rectangle(160 - 96, 240 - 36, 192, 36);
				
		//Enter the first room
		activeFloor.enterRoom(activeFloor.getActiveRoom());
		
		Journal.addEntry("Welcome to Tactical Journey!");
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
		
		
		if (!newRoom.isVisited()) {
			newRoom.setVisited(true);
			AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(player);
			alterationReceiverComponent.onRoomVisited(player, newRoom);
		}
		
		
		//TODO : probably improve this code, especially if any other entity than the player can travel between rooms
		//Set the player in the new room	
		updateRoomForComponents(player, newRoom);
		
		PlayerComponent playerComponent = Mappers.playerComponent.get(player);
		updateRoomForComponents(playerComponent.getSkillMelee(), newRoom);
		updateRoomForComponents(playerComponent.getSkillRange(), newRoom);
		updateRoomForComponents(playerComponent.getSkillBomb(), newRoom);
		updateRoomForComponents(playerComponent.getSkillThrow(), newRoom);
	}
	
	
	public void enterNextFloor() {
		int indexOfCurrentFloor = this.floors.indexOf(this.activeFloor);
		Floor nextFloor = this.floors.get(indexOfCurrentFloor + 1);
		this.requestedFloor = nextFloor;
	}
	
	private void enterFloor(Floor newFloor) {
		// Generate the new floor
		if (newFloor.getRooms() == null) {
			newFloor.generate();
		}
		
		// Leave the room of the current floor
		Room oldRoom = this.activeFloor.getActiveRoom();
		this.activeFloor.removePlayerFromRoom(oldRoom);

		// Enter the room of the new floor
		Room newActiveRoom = newFloor.getActiveRoom();
		enterRoom(newActiveRoom, this.activeFloor.getActiveRoom());

		PoolableVector2 tempPos = PoolableVector2.create(11,6);
		MovementHandler.placeEntity(this.player, tempPos, newActiveRoom);
		tempPos.free();
		
		// Update the map
		this.mapRenderer.enterFloor(newFloor);
		
		// Remove all entities from the previous floor
		for(Room r : this.activeFloor.getRooms()) {
			for (Entity e : r.getAllEntities()) {
				engine.removeEntity(e);
			}
		}
		
		this.activeFloor = newFloor;
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
				
		switch (state) {
		case GAME_RUNNING:
			
			engine.update(deltaTime);
			
			break;
			default:
		}
	}


	public void draw (float delta) {
		switch (state) {
		case GAME_RUNNING:
		case GAME_PAUSED:
		case GAME_COMPLETED:
		case GAME_OVER:
			
			presentRunning(delta);
			
			break;

		}
	}

	private void presentRunning (float delta) {
		for (Renderer r : renderers) {
			r.render(delta);
		}
		
		foregroundFxStage.act(Gdx.graphics.getDeltaTime());
		foregroundFxStage.draw();
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
		
		InputSingleton.getInstance().resetEvents();

		// Switch floor
		if (this.requestedFloor != null) {
			this.enterFloor(this.requestedFloor);
			this.requestedFloor = null;
		}
	}

	@Override
	public void pause () {
		// TODO nothing yet
	}
	
	public void backToMenu() {
		this.game.setScreen(new MainMenuScreen(this.game));
		quickDispose();
	}
	
	public void quickDispose() {
		Assets.getInstance().dispose();
		stage.dispose();
		hudStage.dispose();
		fxStage.dispose();
		miniMapStage.dispose();
		GameTimeSingleton.dispose();
		RandomSingleton.dispose();
		Journal.dispose();
		AnimationSingleton.dispose();
	}
	
	@Override
	public void dispose() {
		quickDispose();
		SceneAssets.getInstance().dispose();
		game.dispose();
		super.dispose();
	}
}