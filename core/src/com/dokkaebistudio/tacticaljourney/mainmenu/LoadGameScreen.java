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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dokkaebistudio.tacticaljourney.Settings;
import com.dokkaebistudio.tacticaljourney.TacticalJourney;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.assets.MenuAssets;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameTypeEnum;
import com.dokkaebistudio.tacticaljourney.gamescreen.LoadingGameScreen;
import com.dokkaebistudio.tacticaljourney.persistence.GameStatistics;
import com.dokkaebistudio.tacticaljourney.persistence.Persister;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;

public class LoadGameScreen extends ScreenAdapter {
	TacticalJourney game;
	OrthographicCamera guiCam;
	Vector3 touchPoint;
	FitViewport viewport;
	Stage hudStage;

	TextureRegion menuBackground;
	
	boolean enteredSeed = false;
	TextField seedField;
	
	boolean enteredName = false;
	TextField nameField;

	public LoadGameScreen (final TacticalJourney game) {
		this.game = game;

		guiCam = new OrthographicCamera(GameScreen.SCREEN_W, GameScreen.SCREEN_H);
		guiCam.position.set(GameScreen.SCREEN_W / 2, GameScreen.SCREEN_H / 2, 0);
		viewport = new FitViewport(GameScreen.SCREEN_W, GameScreen.SCREEN_H, guiCam);
		hudStage = new Stage(viewport);

		touchPoint = new Vector3();

		// should be already loaded
		menuBackground = MenuAssets.menuBackground.getRegion();
		
		Gdx.input.setInputProcessor(hudStage);
		
				
		Table mainTable = new Table();
//		mainTable.setDebug(true);
		
		// Main title

		Label newGameLabel = new Label("LOAD GAME", PopinService.hudStyle());
		mainTable.add(newGameLabel).padBottom(50);
		mainTable.row();
		
		
		// Display game infos

		Table statsTable = new Table();
		statsTable.left();
		
		GameStatistics stats = new Persister().loadGameStatistics();
		
		Label characterName = new Label("Name: " + stats.getCharacterName(), PopinService.hudStyle());
		statsTable.add(characterName).left();
		statsTable.row();
		Label characterLevel = new Label("Level: " + stats.getCharacterLevel(), PopinService.hudStyle());
		statsTable.add(characterLevel).left();
		statsTable.row();
		Label floor = new Label("Floor: " + stats.getFloorLevel(), PopinService.hudStyle());
		statsTable.add(floor).left();
		statsTable.row();
		
		Label gold = new Label("Money: [GOLDENROD]" + stats.getGold() + " gold coin(s)", PopinService.hudStyle());
		statsTable.add(gold).left().padBottom(10);
		statsTable.row();
		
		Label totalTime = new Label("Total time: " + String.format("%.1f", stats.getTotalTime()), PopinService.hudStyle());
		statsTable.add(totalTime).left();
		statsTable.row();
		Label totalTurns = new Label("Total turns: " + stats.getTotalTurns(), PopinService.hudStyle());
		statsTable.add(totalTurns).left().padBottom(10);
		statsTable.row();
		
		mainTable.add(statsTable).width(500).padBottom(10);
		mainTable.row();

		// Load button
		
		Table buttonTable = new Table();
		
		final TextButton loadBtn = new TextButton("LOAD", PopinService.buttonStyle());
		loadBtn.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				RandomSingleton.createInstance();
				// Launch the game
				game.setScreen(new LoadingGameScreen(game, GameTypeEnum.LOAD_GAME, null));
			}
		});
		buttonTable.add(loadBtn).width(500).height(200).padBottom(5);
		mainTable.add(buttonTable).padBottom(5);
		mainTable.row();
		
		
		// Erase save
        final TextButton removeSaveBtn = new TextButton("Erase save", PopinService.buttonStyle());
        removeSaveBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            	new Persister().deleteSave();
				game.setScreen(new MainMenuScreen(game));
            }
        });
        mainTable.add(removeSaveBtn).right().padBottom(300);
		mainTable.row();

		

		// Back button
		
		TextButton backBtn = new TextButton("Back", PopinService.buttonStyle());
		backBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {				
				game.setScreen(new MainMenuScreen(game));
			}
		});
		mainTable.add(backBtn).padBottom(50);
		mainTable.row();

		
		mainTable.pack();
		mainTable.setPosition(GameScreen.SCREEN_W/2 - mainTable.getWidth()/2, 0);
		hudStage.addActor(mainTable);
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
}
