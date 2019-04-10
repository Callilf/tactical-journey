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

package com.dokkaebistudio.tacticaljourney.assets;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.dokkaebistudio.tacticaljourney.Settings;
import com.dokkaebistudio.tacticaljourney.descriptors.FontDescriptor;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;

public class SceneAssets {
	
	public static final String atlas = "scenes.atlas";
	public static HashMap<String, RegionDescriptor> allSprites;
	
	public static RegionDescriptor textfield_cursor;
	public static NinePatch popinNinePatch;
	public static NinePatch popinInnerNinePatch;
	public static NinePatch popinOuterNinePatch;
	public static NinePatch buttonNinePatch;
	public static NinePatch buttonPressedNinePatch;
	public static NinePatch buttonDisabledNinePatch;

	public static FontDescriptor font;	
	public static FontDescriptor smallFont;

	private static SceneAssets instance;
	private AssetManager manager;
	
	
	public void initTextures() {
		allSprites = new HashMap<>();
		textfield_cursor = SceneAssets.getTexture("textfield_cursor");
		
		popinNinePatch = getNinePatch("popin", 17, 17, 17, 17);
		popinInnerNinePatch = getNinePatch("popin_inner", 13, 13, 13, 13);
		popinOuterNinePatch = getNinePatch("popin_outer", 13, 13, 13, 13);
		buttonNinePatch = getNinePatch("button", 17, 17, 17, 17);
		buttonPressedNinePatch = getNinePatch("button_pressed", 17, 17, 17, 17);
		buttonDisabledNinePatch = getNinePatch("button_disabled", 17, 17, 17, 17);
	}
	
	public static void addSprite(RegionDescriptor sprite) {
		allSprites.put(sprite.getName(), sprite);
	}
	
	public static RegionDescriptor findSprite(String name) {
		if (name == null) return null;
		RegionDescriptor regionDescriptor = allSprites.get(name);
		return regionDescriptor;
	}
	
	public static FontDescriptor findFont(String name) {
		if (font.getName().equals(name)) return font;
		if (smallFont.getName().equals(name)) return smallFont;
		return null;
	}
	

	public static SceneAssets getInstance() {
		if (instance == null) {
			instance = new SceneAssets();
		}
		return instance;
	}

	public SceneAssets() {
		manager = new AssetManager();
		registerAssets();
	}
	
	public void dispose() {
		manager.dispose();
		instance = null;
	}

	/**
	 * This doesn't load the textures yet, it simply adds files to the list of assets to load.
	 * Loading will be done by calling load on the AssetManager a bunch of times until it's done.
	 */
	private void registerAssets() {
		// register the only texture atlas we have
		registerTextureAtlas(atlas);
		loadFont();
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

	private static RegionDescriptor getTexture(String file){
		AtlasRegion region = getInstance().manager.get(atlas, TextureAtlas.class).findRegion(file);
		region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		RegionDescriptor regionDescriptor = new RegionDescriptor(file, region);
		addSprite(regionDescriptor);
		return regionDescriptor;
	}
	
	public static NinePatch getNinePatch(String file, int left, int right, int top, int bottom){
		return new NinePatch(getTexture(file).getRegion(), left, right, top, bottom);
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
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("data/fonts/Acme-Regular.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 24;
		BitmapFont font24 = generator.generateFont(parameter); // font size 12
		font24.getData().markupEnabled = true;
		font = new FontDescriptor("font", font24);
		
		parameter.size = 20;
		BitmapFont font12 = generator.generateFont(parameter); // font size 12
		font12.getData().markupEnabled = true;
		smallFont = new FontDescriptor("font", font12);

		
		generator.dispose(); // don't forget to dispose to avoid memory leaks!
	}

	public static void playSound (String sound) {
		if (Settings.soundEnabled) {
			getSound(sound).play(1);
		}
	}
}
