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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {
	public static final String background = "data/background-test.png";
	public static final String menuBackground = "data/background-test-menu.png";

	public static String items = "data/items.png";

	public static String music = "data/music.mp3";
	public static String jumpSound = "data/jump.wav";
	public static String highJumpSound = "data/highjump.wav";
	public static String hitSound = "data/hit.wav";
	public static String coinSound = "data/coin.wav";
	public static String clickSound = "data/click.wav";


	public static TextureRegion mainMenu;
	public static TextureRegion pauseMenu;
	public static TextureRegion ready;
	public static TextureRegion gameOver;
	public static TextureRegion highScoresRegion;
	public static TextureRegion logo;
	public static TextureRegion soundOn;
	public static TextureRegion soundOff;
	public static TextureRegion arrow;
	public static TextureRegion pause;
	public static TextureRegion spring;
	public static TextureRegion castle;
	public static Animation coinAnim;
	public static Animation bobJump;
	public static Animation bobFall;
	public static Animation bobHit;
	public static Animation squirrelFly;
	public static Animation platform;
	public static Animation breakingPlatform;
	public static BitmapFont font;

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
		// register textures
		registerTexture(background);
		registerTexture(menuBackground);
		registerTexture(items);

		// TODO register texture atlases (they need to be generated first)

		// register music and sounds
		registerMusic(music);
		registerSound(jumpSound);
		registerSound(highJumpSound);
		registerSound(hitSound);
		registerSound(coinSound);
		registerSound(clickSound);
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
	public static TextureAtlas getTextureAtlas(String file){
		return getInstance().manager.get(file);
	}

	private void registerTexture(String file) {
		this.manager.load(file, Texture.class);
	}
	public static Texture getTexture(String file){
		return getInstance().manager.get(file);
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

	/**
	 * Should be called as soon as possible to display loading info.
	 */
	public void loadFont() {
		font = new BitmapFont(Gdx.files.internal("data/font.fnt"), Gdx.files.internal("data/font.png"), false);
	}



	// OLD DUMB SYNCHRONOUS WAY

	public static Texture loadTexture (String file) {
		return new Texture(Gdx.files.internal(file));
	}

	public void finalizeLoading() {
		// create regions
		Texture itemsTex = getTexture(items);
		mainMenu = new TextureRegion(itemsTex, 0, 224, 300, 110);
		pauseMenu = new TextureRegion(itemsTex, 224, 128, 192, 96);
		ready = new TextureRegion(itemsTex, 320, 224, 192, 32);
		gameOver = new TextureRegion(itemsTex, 352, 256, 160, 96);
		highScoresRegion = new TextureRegion(itemsTex, 0, 257, 300, 110 / 3);
		logo = new TextureRegion(itemsTex, 0, 352, 274, 142);
		soundOff = new TextureRegion(itemsTex, 0, 0, 64, 64);
		soundOn = new TextureRegion(itemsTex, 64, 0, 64, 64);
		arrow = new TextureRegion(itemsTex, 0, 64, 64, 64);
		pause = new TextureRegion(itemsTex, 64, 64, 64, 64);
		spring = new TextureRegion(itemsTex, 128, 0, 32, 32);
		castle = new TextureRegion(itemsTex, 128, 64, 64, 64);

		// animations
		coinAnim = new Animation(0.2f, new TextureRegion(itemsTex, 128, 32, 32, 32), new TextureRegion(itemsTex, 160, 32, 32, 32),
			new TextureRegion(itemsTex, 192, 32, 32, 32), new TextureRegion(itemsTex, 160, 32, 32, 32));
		bobJump = new Animation(0.2f, new TextureRegion(itemsTex, 0, 128, 32, 32), new TextureRegion(itemsTex, 32, 128, 32, 32));
		bobFall = new Animation(0.2f, new TextureRegion(itemsTex, 64, 128, 32, 32), new TextureRegion(itemsTex, 96, 128, 32, 32));
		bobHit = new Animation(0.2f, new TextureRegion(itemsTex, 128, 128, 32, 32));
		squirrelFly = new Animation(0.2f, new TextureRegion(itemsTex, 0, 160, 32, 32), new TextureRegion(itemsTex, 32, 160, 32, 32));
		platform = new Animation(0.2f, new TextureRegion(itemsTex, 64, 160, 64, 16));
		breakingPlatform = new Animation(0.2f, new TextureRegion(itemsTex, 64, 160, 64, 16), new TextureRegion(itemsTex, 64, 176, 64, 16),
			new TextureRegion(itemsTex, 64, 192, 64, 16), new TextureRegion(itemsTex, 64, 208, 64, 16));

		// animations playing on loop
		coinAnim.setPlayMode(PlayMode.LOOP);
		bobJump.setPlayMode(PlayMode.LOOP);
		bobFall.setPlayMode(PlayMode.LOOP);
		bobHit.setPlayMode(PlayMode.LOOP);
		squirrelFly.setPlayMode(PlayMode.LOOP);
		platform.setPlayMode(PlayMode.LOOP);

		// set music settings
		Music musicAsset = getMusic(music);
		musicAsset.setLooping(true);
		musicAsset.setVolume(0.5f);
		if (Settings.soundEnabled) musicAsset.play();

	}

	public static void playSound (String sound) {
		if (Settings.soundEnabled) {
			getSound(sound).play(1);
		}
	}
}
