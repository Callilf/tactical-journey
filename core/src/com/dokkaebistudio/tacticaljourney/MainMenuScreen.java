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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;

public class MainMenuScreen extends ScreenAdapter {
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
		menuBackground = Assets.menuBackground.getRegion();
		
		Gdx.input.setInputProcessor(hudStage);
		
		FileHandle saveFile = Gdx.files.local("gamestateFred.bin");
		boolean hasSave = saveFile.exists();

		
				
		Table mainTable = new Table();
		
		NinePatchDrawable ninePatchDrawable = new NinePatchDrawable(Assets.popinNinePatch);
		TextFieldStyle tfs = new TextFieldStyle(Assets.font.getFont(), Color.WHITE, null, null, ninePatchDrawable);
		seedField = new TextField("Enter seed", tfs);
		seedField.addListener(new FocusListener() {
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
				enteredSeed = true;
				seedField.setText("");
			}
		});
		
		mainTable.add(seedField).width(500).padBottom(300);
		mainTable.row();
		
		
		Table buttonTable = new Table();
		
		TextButton start = new TextButton("NEW GAME", PopinService.buttonStyle());
		start.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				
				//Instantiate the RNG
				if (enteredSeed) {
					RandomSingleton.createInstance(seedField.getText());
				} else {
					RandomSingleton.createInstance();
				}
				
				// Launch the game
				game.setScreen(new GameScreen(game, true));
			}
		});
		buttonTable.add(start).width(500).height(200).padBottom(350).padRight(50);
		
		
		final TextButton loadBtn = new TextButton("LOAD", PopinService.buttonStyle());
		loadBtn.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				
				//Instantiate the RNG
				if (enteredSeed) {
					RandomSingleton.createInstance(seedField.getText());
				} else {
					RandomSingleton.createInstance();
				}
				
				// Launch the game
				game.setScreen(new GameScreen(game, false));
			}
		});
		buttonTable.add(loadBtn).width(500).height(200).padBottom(350).padLeft(50);
		loadBtn.setDisabled(!hasSave);
		mainTable.add(buttonTable);
		mainTable.row();
		
		
		final TextButton removeSaveBtn = new TextButton("Erase save", PopinService.buttonStyle());
		removeSaveBtn.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				FileHandle saveFile = Gdx.files.local("gamestateFred.bin");
				saveFile.delete();
				loadBtn.setDisabled(true);
				removeSaveBtn.setDisabled(true);
			}
		});
		mainTable.add(removeSaveBtn).padBottom(50);
		removeSaveBtn.setDisabled(!hasSave);
		
		mainTable.pack();
		mainTable.setPosition(GameScreen.SCREEN_W/2 - mainTable.getWidth()/2, 0);
		hudStage.addActor(mainTable);
	}

	public void update () {
//		if (Gdx.input.justTouched()) {
//			// touched screen, start the fucking game already
//			game.setScreen(new GameScreen(game));
//		}
	}

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
