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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.dokkaebistudio.tacticaljourney.Settings;
import com.dokkaebistudio.tacticaljourney.TacticalJourney;
import com.dokkaebistudio.tacticaljourney.assets.MenuAssets;
import com.dokkaebistudio.tacticaljourney.assets.SceneAssets;

public class LoadingScreen extends ScreenAdapter {
	TacticalJourney game;
	OrthographicCamera guiCam;

	Texture menuBackground;

	public LoadingScreen(TacticalJourney game) {
		this.game = game;

		guiCam = new OrthographicCamera(1920, 1080);
		guiCam.position.set(1920 / 2, 1080 / 2, 0);
	}

	public int load(){
	    // call asset manager loading each tick
		boolean menuAssetsLoaded = MenuAssets.getInstance().loadAssets();
		boolean sceneAssetsLoaded = SceneAssets.getInstance().loadAssets();

		if (menuAssetsLoaded && sceneAssetsLoaded) { // loading is finished !
			MenuAssets.getInstance().initTextures();
			SceneAssets.getInstance().initTextures();
			
            // change screen
			this.game.setScreen(new MainMenuScreen(this.game));
		}

		// report progress
		return (int)MenuAssets.getInstance().getLoadingProgress();
	}

	public void draw(int progress) {

		GL20 gl = Gdx.gl;
		gl.glClearColor(0, 0, 0, 1);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		guiCam.update();
		game.batcher.setProjectionMatrix(guiCam.combined);

		game.batcher.begin();
		// draw loading text in the center
		String text = "LOADING - " + progress + "%";
		GlyphLayout loadingLayout = new GlyphLayout();
		// update layout. It is used to compute the real text height and width to aid positioning
		loadingLayout.setText(SceneAssets.font.getFont(), text);
		SceneAssets.font.getFont().draw(game.batcher, loadingLayout, 1920/2 - loadingLayout.width, 1080/2 - loadingLayout.height);
		game.batcher.end();	
	}

	@Override
	public void render (float delta) {
		int progress = load();
		draw(progress);
	}

	@Override
	public void pause () {
		Settings.save();
	}
}
