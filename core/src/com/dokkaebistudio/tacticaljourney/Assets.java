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
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Assets {
	
	public static final String atlas = "tacticaljourney.atlas";

	public static final String background = "background-test";
	public static final String menuBackground = "background-test-menu";
	public static final String player = "player-test";
	public static final String enemy = "enemy-test";
	public static final String tile_ground = "tile-test";
	public static final String tile_wall = "tile-wall-test";
	public static final String tile_pit = "tile-pit-test";
	public static final String tile_mud = "tile-mud-test";
	
	
	public static final String tile_movable = "tile-movable";
	public static final String tile_attackable = "tile-attackable";
	
	public static final String tile_movable_waypoint = "tile-movable-waypoint";
	public static final String tile_movable_selected = "tile-movable-selected";
	
	public static final String btn_move_confirmation = "btn-move-confirmation";
	public static final String btn_end_turn = "btn-end-turn";
	public static final String btn_end_turn_pushed = "btn-end-turn-pushed";
	
	public static final String wheel_arrow = "wheel-arrow";

	

	public static BitmapFont font;
	public static BitmapFont greenFont;
	public static BitmapFont redFont;

	private static Assets instance;
	private AssetManager manager;

	public static Assets getInstance() {
		if (instance == null) {
			instance = new Assets();
		}
		return instance;
	}

	public Assets() {
		manager = new AssetManager();
		registerAssets();
	}

	/**
	 * This doesn't load the textures yet, it simply adds files to the list of assets to load.
	 * Loading will be done by calling load on the AssetManager a bunch of times until it's done.
	 */
	private void registerAssets() {
		// register the only texture atlas we have
		registerTextureAtlas(atlas);
		font = new BitmapFont(Gdx.files.internal("data/font.fnt"), Gdx.files.internal("data/font.png"), false);	
	}

	/**
	 * Loads some assets, call this in the render loop of the loading screen.
	 * @return True if all is loaded.
	 */
	public boolean loadAssets() {
		return manager.update();
	}

	/**
	 * Returns asset loading progress in percentage (0-100).
	 */
	public float getLoadingProgress() {
		return manager.getProgress() * 100;
	}

	private void registerTextureAtlas(String atlasFile) {
		this.manager.load(atlasFile, TextureAtlas.class);
	}

	public static TextureAtlas.AtlasRegion getTexture(String file){
		return getInstance().manager.get(atlas, TextureAtlas.class).findRegion(file);
	}

	private void registerMusic(String file) {
		this.manager.load(file, Music.class);
	}
	public static Music getMusic(String file) {
		return getInstance().manager.get(file);
	}

	private void registerSound(String file) {
		this.manager.load(file, Sound.class);
	}
	public static Sound getSound(String file){
		return getInstance().manager.get(file);
	}
	
	private void registerFont(String file) {
		this.manager.load(file, BitmapFont.class);
	}

	/**
	 * Should be called as soon as possible to display loading info.
	 */
	public void loadFont() {
		font = new BitmapFont(Gdx.files.internal("data/font.fnt"), Gdx.files.internal("data/font.png"), false);
		greenFont = new BitmapFont(Gdx.files.internal("data/font.fnt"), Gdx.files.internal("data/font.png"), false);
		greenFont.setColor(Color.GREEN);
		redFont = new BitmapFont(Gdx.files.internal("data/font.fnt"), Gdx.files.internal("data/font.png"), false);
		redFont.setColor(Color.RED);
	}

	public static void playSound (String sound) {
		if (Settings.soundEnabled) {
			getSound(sound).play(1);
		}
	}
}
