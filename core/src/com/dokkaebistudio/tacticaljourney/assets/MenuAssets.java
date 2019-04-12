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

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.dokkaebistudio.tacticaljourney.Settings;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;

public class MenuAssets {
	
	public static final String atlas = "menu.atlas";
	public static HashMap<String, RegionDescriptor> allSprites;

	public static RegionDescriptor menuBackground;
	public static RegionDescriptor mainTitle;
	
	public static RegionDescriptor screen1;
	public static RegionDescriptor screen2;
	public static RegionDescriptor screen3;
	
	public static RegionDescriptor melee;
	public static RegionDescriptor bow;
	public static RegionDescriptor bombs;
	public static RegionDescriptor profile;
	public static RegionDescriptor inventory;
	public static RegionDescriptor inspect;

	private static MenuAssets instance;
	private AssetManager manager;
	
	
	public void initTextures() {
		allSprites = new HashMap<>();
		menuBackground = MenuAssets.getTexture("background-test-menu");
		mainTitle = MenuAssets.getTexture("main_title");
		
		// How to play screenshots
		screen1 = getTexture("screen1");
		screen2 = getTexture("screen2");
		screen3 = getTexture("screen3");
		
		melee = getTexture("melee");
		bow = getTexture("melee");
		bombs = getTexture("bombs");
		profile = getTexture("profile");
		inventory = getTexture("inventory");
		inspect = getTexture("inspect");
	}
	
	public static void addSprite(RegionDescriptor sprite) {
		allSprites.put(sprite.getName(), sprite);
	}
	
	public static RegionDescriptor findSprite(String name) {
		if (name == null) return null;
		RegionDescriptor regionDescriptor = allSprites.get(name);
		return regionDescriptor;
	}
	

	public static MenuAssets getInstance() {
		if (instance == null) {
			instance = new MenuAssets();
		}
		return instance;
	}

	public MenuAssets() {
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
	
	@Deprecated
	public static RegionDescriptor loadAndGetTexture(String file){
		AtlasRegion region = getInstance().manager.get(atlas, TextureAtlas.class).findRegion(file);
		region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		RegionDescriptor regionDescriptor = new RegionDescriptor(file, region);
		return regionDescriptor;
	}
	
	public static Array<Sprite> getAnimation(String file){
		Array<AtlasRegion> regions = getInstance().manager.get(atlas, TextureAtlas.class).findRegions(file);
		
		Array<Sprite> a = new Array<>();
		for (AtlasRegion r : regions) {
			r.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			a.add(new Sprite(r));
		}
		return a;
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
	
	/**
	 * Should be called as soon as possible to display loading info.
	 */
	public void loadFont() {}

	public static void playSound (String sound) {
		if (Settings.soundEnabled) {
			getSound(sound).play(1);
		}
	}
}
