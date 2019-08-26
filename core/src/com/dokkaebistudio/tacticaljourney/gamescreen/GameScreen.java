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

package com.dokkaebistudio.tacticaljourney.gamescreen;

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
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.TacticalJourney;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.assets.SceneAssets;
import com.dokkaebistudio.tacticaljourney.ces.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.ces.entity.PublicPooledEngine;
import com.dokkaebistudio.tacticaljourney.ces.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.AnimationSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.ChaliceSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.ChasmSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.ContextualActionSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.CreepSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.DialogSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.ExplosionSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.HealthSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.InspectSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.ItemSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.OrbSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.PanelSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.ShopSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.SoulbenderSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.SpeakerSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.StateSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.StatusSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.TurnSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.TutorialSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.WheelSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.creatures.AllySystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.creatures.EnemySystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.iteratingsystems.ExperienceSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.iteratingsystems.PlayerAttackSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.iteratingsystems.PlayerMoveSystem;
import com.dokkaebistudio.tacticaljourney.ces.systems.iteratingsystems.KnockbackSystem;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.mainmenu.LoadingScreen;
import com.dokkaebistudio.tacticaljourney.persistence.Persister;
import com.dokkaebistudio.tacticaljourney.rendering.CharacteristicsPopinRenderer;
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
import com.dokkaebistudio.tacticaljourney.rendering.TeleportPopinRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.WheelRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.WinPopinRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.singletons.GameTimeSingleton;
import com.dokkaebistudio.tacticaljourney.singletons.InputSingleton;
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
	
	public static final boolean debugMode = false;
	

	public TacticalJourney game;
	public GameTypeEnum gameType;

	public FitViewport viewport;
	public OrthographicCamera guiCam;
	public Stage stage;
	public Stage inventoryStage;
	public static Stage fxStage;
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
	
	public static PublicPooledEngine engine;	
	public static String currentSystem;
	public int state;
	
	AttackWheel attackWheel = new AttackWheel();
	
	/** The list of renderers. */
	private List<Renderer> renderers = new ArrayList<>();
	private MapRenderer mapRenderer;
	
	/** The player. 
	 * Static because there is only one player in this game. Every class have access to it. */
	public static Entity player;
	
	/** The name of the entity that killed the player. */
	public String killerName;
	
	public GameScreen (TacticalJourney game, GameTypeEnum gameType, String playerName) {
		this.game = game;
		this.gameType = gameType;
		
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
		this.entityFactory = new EntityFactory(engine);
		
		floors = new ArrayList<>();
		initializeGame(playerName);
		Room room = activeFloor.getActiveRoom();

		
		
		
		mapRenderer = new MapRenderer(miniMapStage, activeFloor);
		renderers.add(new RoomRenderer(fxStage,game.batcher, room, guiCam));
		renderers.add(new HUDRenderer(hudStage));
		renderers.add(mapRenderer);
		renderers.add(new JournalRenderer(journalStage));
		renderers.add(new DialogRenderer(room, stage));
		renderers.add(new WheelRenderer(attackWheel, this, game.batcher, game.shapeRenderer, hudStage));
		renderers.add(new ContextualActionPopinRenderer(room, stage));
		renderers.add(new InspectPopinRenderer(room, stage));
		renderers.add(new TeleportPopinRenderer(room, stage));
		renderers.add(new ItemPopinRenderer(room, stage));
		renderers.add(new InventoryPopinRenderer(room, inventoryStage));
		renderers.add(new LootPopinRenderer(room, inventoryStage));
		renderers.add(new ProfilePopinRenderer(room, stage));
		renderers.add(new StatusPopinRenderer(room, stage));
		renderers.add(new CharacteristicsPopinRenderer(room, stage));
		if (debugMode) { renderers.add(new DebugPopinRenderer(room, inventoryStage)); }
		renderers.add(new LevelUpPopinRenderer(room, stage));
		renderers.add(new WinPopinRenderer(this, menuStage));
		renderers.add(new GameOverPopinRenderer(this, menuStage));
		renderers.add(new MenuPopinRenderer(this, menuStage));

		

		
		engine.addSystem(room);
		engine.addSystem(new StateSystem(room));
		engine.addSystem(new AnimationSystem(room));
		engine.addSystem(new TurnSystem(room, fxStage));
		engine.addSystem(new WheelSystem(attackWheel, room));
		engine.addSystem(new ExplosionSystem(room, fxStage));
		engine.addSystem(new CreepSystem(this, room, fxStage));
		engine.addSystem(new EnemySystem(room, fxStage));
		engine.addSystem(new AllySystem(room, fxStage));
		engine.addSystem(new PlayerAttackSystem(fxStage,room, attackWheel));
		engine.addSystem(new PlayerMoveSystem(room));
		engine.addSystem(new ItemSystem(	player, room, foregroundFxStage));
		engine.addSystem(new AlterationSystem(player, room, foregroundFxStage));
		engine.addSystem(new ChaliceSystem(room, foregroundFxStage));
		engine.addSystem(new StatusSystem(player, room, fxStage));
		engine.addSystem(new ShopSystem(	player, room));
		engine.addSystem(new SpeakerSystem(	player, room));
		engine.addSystem(new SoulbenderSystem(	player, room));
		engine.addSystem(new ContextualActionSystem(	player, room));
		engine.addSystem(new DialogSystem(room));
		engine.addSystem(new ExperienceSystem(room, stage));
		engine.addSystem(new OrbSystem(player, room, stage));
		engine.addSystem(new HealthSystem(this,room, fxStage));
		engine.addSystem(new PanelSystem(room));
		engine.addSystem(new KnockbackSystem(room, fxStage));
		engine.addSystem(new ChasmSystem(room));
		
		engine.addSystem(new InspectSystem(player, room, fxStage));
		
		if (gameType.isTutorial()) {
			engine.addSystem(new TutorialSystem(room, fxStage));
		}
		

		
		pauseBounds = new Rectangle(10, 10, 64, 64);
		resumeBounds = new Rectangle(160 - 96, 240, 192, 36);
		quitBounds = new Rectangle(160 - 96, 240 - 36, 192, 36);
				
		//Enter the first room
		activeFloor.enterRoom(activeFloor.getActiveRoom(), null);
		
		Journal.addEntry("Welcome to Calishka's Trial!");
		RoomRenderer.showFadeoutBlack();
	}


	private void initializeGame(String playerName) {
		if (gameType.isNewGame()) {
			initializeNewGame(playerName);
		} else if (gameType.isLoadGame()){
			initializeLoadGame();
		} else if (gameType.isTutorial()) {
			initializeTutorial(playerName);
		}
	}


	private void initializeNewGame(String playerName) {
		player = entityFactory.playerFactory.createPlayer(playerName);

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
		
		MovementHandler.placeEntity(player, new Vector2(11, 11), floor1.getActiveRoom());
	}

	private void initializeLoadGame() {
		Persister persister = new Persister(this);
		persister.loadGameState();
	}

	private void initializeTutorial(String playerName) {
		player = entityFactory.playerFactory.createPlayer(playerName);
		
		Floor floor = new Floor(this, 0);
		floor.generate();
		floors.add(floor);
		activeFloor = floor;
		
		MovementHandler.placeEntity(player, new Vector2(6, 6), floor.getActiveRoom());
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
		
		if (oldRoom != null) oldRoom.getAllies().remove(GameScreen.player);
		newRoom.getAllies().add(GameScreen.player);
		
		
		newRoom.setJustEntered(true);
		
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
		if (gameType.isTutorial()) {
			this.backToMenu();
			return;
		}

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
		newFloor.enterRoom(newActiveRoom, oldRoom);

		PoolableVector2 tempPos = PoolableVector2.create(11,6);
		MovementHandler.placeEntity(player, tempPos, newActiveRoom);
		tempPos.free();
		
		// Update the map
		this.mapRenderer.enterFloor(newFloor);
		
		// Remove all entities from the previous floor
		for(Room r : this.activeFloor.getRooms()) {
			for (Entity e : r.getAllEntities()) {
				engine.removeEntity(e);
			}
		}
		
		AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(player);
		if (alterationReceiverComponent != null) {
			alterationReceiverComponent.onFloorVisited(player, newFloor, newActiveRoom);
		}
		
		this.activeFloor = newFloor;
		RoomRenderer.showFadeoutBlack();
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
		this.game.setScreen(new LoadingScreen(this.game));
		quickDispose();
	}
	
	public void quickDispose() {
		stage.dispose();
		hudStage.dispose();
		fxStage.dispose();
		miniMapStage.dispose();
		GameTimeSingleton.dispose();
		RandomSingleton.dispose();
		Journal.dispose();
		AnimationSingleton.dispose();
		Assets.getInstance().dispose();
	}
	
	@Override
	public void dispose() {
		quickDispose();
		SceneAssets.getInstance().dispose();
		game.dispose();
		super.dispose();
	}
}