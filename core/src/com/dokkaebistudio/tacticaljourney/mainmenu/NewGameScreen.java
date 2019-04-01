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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.Settings;
import com.dokkaebistudio.tacticaljourney.TacticalJourney;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;

public class NewGameScreen extends ScreenAdapter {
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

	public NewGameScreen (final TacticalJourney game) {
		this.game = game;

		guiCam = new OrthographicCamera(GameScreen.SCREEN_W, GameScreen.SCREEN_H);
		guiCam.position.set(GameScreen.SCREEN_W / 2, GameScreen.SCREEN_H / 2, 0);
		viewport = new FitViewport(GameScreen.SCREEN_W, GameScreen.SCREEN_H, guiCam);
		hudStage = new Stage(viewport);

		touchPoint = new Vector3();

		// should be already loaded
		menuBackground = Assets.menuBackground.getRegion();
		
		Gdx.input.setInputProcessor(hudStage);
		
				
		Table mainTable = new Table();
		
		seedField = new TextField("Enter seed", PopinService.textFieldStyle());
		seedField.setOnlyFontChars(true);
		seedField.setBlinkTime(0.5f);
		seedField.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					enteredSeed = true;
					seedField.setText("");
				}
			}
		});
		
		mainTable.add(seedField).width(500).padBottom(200);
		mainTable.row();
		

		nameField = new TextField("Character name", PopinService.textFieldStyle());
		nameField.setMaxLength(23);
		nameField.setOnlyFontChars(true);
		nameField.setBlinkTime(0.5f);
		nameField.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				if (focused) {
					enteredName = true;
					nameField.setText("");
				}
			}
		});
		
		mainTable.add(nameField).width(500).padBottom(50);
		mainTable.row();
		
		
		Table buttonTable = new Table();
		
		TextButton start = new TextButton("START", PopinService.buttonStyle());
		start.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				
				//Instantiate the RNG
				if (enteredSeed) {
					RandomSingleton.createInstance(seedField.getText());
				} else {
					RandomSingleton.createInstance();
				}
				
				String playerName = null;
				if (enteredName) {
					playerName = nameField.getText();
				}
				
				// Launch the game
				game.setScreen(new GameScreen(game, true, playerName));
			}
		});
		buttonTable.add(start).width(500).height(200).padBottom(350);
		mainTable.add(buttonTable);
		mainTable.row();
		
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
