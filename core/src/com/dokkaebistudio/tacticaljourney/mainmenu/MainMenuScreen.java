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

package com.dokkaebistudio.tacticaljourney.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dokkaebistudio.tacticaljourney.Settings;
import com.dokkaebistudio.tacticaljourney.TacticalJourney;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.assets.MenuAssets;
import com.dokkaebistudio.tacticaljourney.assets.SceneAssets;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameTypeEnum;
import com.dokkaebistudio.tacticaljourney.gamescreen.LoadingGameScreen;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.persistence.Persister;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.singletons.GameTimeSingleton;

public class MainMenuScreen extends ScreenAdapter {
	
	
	
	private static final int BUTTON_WIDTH = 500;
	private static final int BUTTON_HEIGHT = 120;
	TacticalJourney game;
	OrthographicCamera guiCam;
	Vector3 touchPoint;
	FitViewport viewport;
	Stage hudStage;

	TextureRegion menuBackground;
	
	boolean enteredSeed = false;
	TextField seedField;
	
	public MainMenuScreen (final TacticalJourney game) {
		this.game = game;

		guiCam = new OrthographicCamera(GameScreen.SCREEN_W, GameScreen.SCREEN_H);
		guiCam.position.set(GameScreen.SCREEN_W / 2, GameScreen.SCREEN_H / 2, 0);
		viewport = new FitViewport(GameScreen.SCREEN_W, GameScreen.SCREEN_H, guiCam);
		hudStage = new Stage(viewport);

		touchPoint = new Vector3();

		// should be already loaded
		menuBackground = MenuAssets.menuBackground.getRegion();
		
		Gdx.input.setInputProcessor(hudStage);
		
		boolean hasSave = new Persister().hasSave();
				
		Table mainTable = new Table();
		
		Image mainTitle = new Image(MenuAssets.mainTitle.getRegion());
		mainTable.add(mainTitle).padBottom(150);
		mainTable.row();

		
		// New game and Load buttons
		
		TextButton start = new TextButton("NEW GAME", PopinService.buttonStyle());
		start.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new NewGameScreen(game));
			}
		});
		mainTable.add(start).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(20);
		mainTable.row();
		
		final TextButton loadBtn = new TextButton("LOAD GAME", PopinService.buttonStyle());
		loadBtn.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new LoadGameScreen(game));
			}
		});
		mainTable.add(loadBtn).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(20);
		loadBtn.setDisabled(!hasSave);
		mainTable.row();
		
				
		TextButton ranking = new TextButton("RANKINGS", PopinService.buttonStyle());
		ranking.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new RankingScreen(game));
			}
		});
		mainTable.add(ranking).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(20);
		mainTable.row();

		
		final TextButton howToPlay = new TextButton("TUTORIAL", PopinService.buttonStyle());
		howToPlay.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				RandomSingleton.createInstance();
				game.setScreen(new LoadingGameScreen(game, GameTypeEnum.TUTORIAL, null));
			}
		});
		mainTable.add(howToPlay).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(150);
		mainTable.row();
		

		mainTable.pack();
		mainTable.setPosition(GameScreen.SCREEN_W/2 - mainTable.getWidth()/2, 0);
		hudStage.addActor(mainTable);

		
		Table smallBtnsTable = new Table();
		TextButton quit = new TextButton("EXIT GAME", PopinService.buttonStyle());
		quit.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				//Quit the game
				dispose();
	            Gdx.app.exit();;
			}
		});
		smallBtnsTable.add(quit).pad(0,0,10,10);
		smallBtnsTable.pack();
		smallBtnsTable.setPosition(GameScreen.SCREEN_W - smallBtnsTable.getWidth(), 0);
		hudStage.addActor(smallBtnsTable);
		
	}

	public void update () {}

	public void draw () {
		GL20 gl = Gdx.gl;
		gl.glClearColor(0, 0, 0, 1);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		guiCam.update();
		game.batcher.setProjectionMatrix(guiCam.combined);


//		game.batcher.enableBlending();
		game.batcher.begin();
		game.batcher.draw(menuBackground, 0, 0, 1920, 1080);
		game.batcher.end();	
		
		hudStage.draw();
	}

	@Override
	public void render (float delta) {
		update();
		draw();
	}

	@Override
	public void pause () {
		Settings.save();
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	@Override
	public void dispose() {
		MenuAssets.getInstance().dispose();
		SceneAssets.getInstance().dispose();
		hudStage.dispose();
		game.dispose();
		PopinService.dispose();
		GameTimeSingleton.dispose();
		RandomSingleton.dispose();
		Journal.dispose();
		AnimationSingleton.dispose();
		super.dispose();
	}
}
