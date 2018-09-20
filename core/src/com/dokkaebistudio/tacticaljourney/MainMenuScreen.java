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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class MainMenuScreen extends ScreenAdapter {
	TacticalJourney game;
	OrthographicCamera guiCam;
	Rectangle soundBounds;
	Rectangle playBounds;
	Vector3 touchPoint;

	TextureRegion menuBackground;

	public MainMenuScreen (TacticalJourney game) {
		this.game = game;

		guiCam = new OrthographicCamera(1920, 1080);
		guiCam.position.set(1920 / 2, 1080 / 2, 0);
		soundBounds = new Rectangle(0, 0, 64, 64);
		
		playBounds = new Rectangle(1920/2 - 300/2, 1080/2 + 70, 300, 36);
		touchPoint = new Vector3();

		// should be already loaded
		menuBackground = Assets.getTexture(Assets.menuBackground);
	}

	public void update () {
		if (Gdx.input.justTouched()) {
			// touched screen, start the fucking game already
			game.setScreen(new GameScreen(game));
		}
	}

	public void draw () {
		GL20 gl = Gdx.gl;
		gl.glClearColor(1, 0, 0, 1);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		guiCam.update();
		game.batcher.setProjectionMatrix(guiCam.combined);

		game.batcher.disableBlending();
		game.batcher.begin();
		game.batcher.draw(menuBackground, 0, 0, 1920, 1080);
		game.batcher.end();

		game.batcher.enableBlending();
		game.batcher.begin();
		// draw loading text in the center
		String text = "TOUCH ANYWHERE TO START";
		GlyphLayout loadingLayout = new GlyphLayout();
		// update layout. It is used to compute the real text height and width to aid positioning
		loadingLayout.setText(Assets.font, text);
		Assets.font.draw(game.batcher, loadingLayout, 1920/2 - loadingLayout.width, 1080/2 - loadingLayout.height);
		game.batcher.end();	
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
}
