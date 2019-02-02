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
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;

public class Assets {
	
	public static final String atlas = "tacticaljourney.atlas";

	public static final String background = "background-test";
	public static final String menuBackground = "background-test-menu";
	
	
	public static final String player = "player-test";
	public static final String enemy_spider = "enemy-test";
	public static final String enemy_scorpion = "enemy-scorpion-test";
	
	public static final String tile_ground = "tile-test";
	public static final String wall = "tile-wall-test";
	public static final String wall_destroyed = "tile-wall-destroyed";

	public static final String tile_pit = "tile-pit-test";
	public static final String mud = "tile-mud-test";
	public static final String mud_destroyed = "tile-mud-destroyed";
	
	public static final String exit = "exit";
	public static final String entrance = "entrance";

	
	public static final String door_closed = "door-closed";
	public static final String door_opened = "door-opened";

	
	
	public static final String tile_movable = "tile-movable";
	public static final String tile_attackable = "tile-attackable";
	
	public static final String tile_movable_waypoint = "tile-movable-waypoint";
	public static final String tile_movable_selected = "tile-movable-selected";
	
	public static final String btn_move_confirmation = "btn-move-confirmation";
	public static final String btn_end_turn = "btn-end-turn";
	public static final String btn_end_turn_pushed = "btn-end-turn-pushed";
	
	public static final String lvl_up_background_top = "hud_lvl_up_background_top";
	public static final String lvl_up_background_bottom = "hud_lvl_up_background_bottom";
	public static final String lvl_up_choice_frame = "hud_lvl_up_choice_frame";
	public static final String lvl_up_choice_desc_panel = "hud_lvl_up_choice_desc_panel";
	public static final String lvl_up_choice_reward_panel = "hud_lvl_up_choice_reward_panel";
	public static final String lvl_up_choice_claim_btn = "hud_lvl_up_choice_claim_btn";
	public static final String lvl_up_choice_claim_btn_pushed = "hud_lvl_up_choice_claim_btn_pushed";
	public static final String lvl_up_continue_btn = "hud_lvl_up_continue_btn";
	public static final String lvl_up_continue_btn_pushed = "hud_lvl_up_continue_btn_pushed";
	
	public static final String profile_background = "hud_profile_background";
	public static final String btn_profile = "btn-profile";
	public static final String btn_profile_pushed = "btn-profile-pushed";

	
	public static final String inventory_background = "hud_inventory_background";
	public static final String inventory_slot = "hud_inventory_slot";
	public static final String inventory_slot_disabled = "hud_inventory_slot_disabled";
	public static final String btn_inventory = "btn-inventory";
	public static final String btn_inventory_pushed = "btn-inventory-pushed";
	
	public static final String inventory_item_popin_background = "hud_inventory_item_popin_background";
	public static final String inventory_item_popin_btn_up = "hud_inventory_item_popin_btn_up";
	public static final String inventory_item_popin_btn_down = "hud_inventory_item_popin_btn_down";

	
	
	public static final String btn_skill_attack = "btn-skill-slash";
	public static final String btn_skill_attack_pushed = "btn-skill-slash-pushed";
	public static final String btn_skill_attack_checked = "btn-skill-slash-checked";
	
	public static final String btn_skill_bow = "btn-skill-bow";
	public static final String btn_skill_bow_pushed = "btn-skill-bow-pushed";
	public static final String btn_skill_bow_checked = "btn-skill-bow-checked";
	
	public static final String btn_skill_bomb = "btn-skill-bomb";
	public static final String btn_skill_bomb_pushed = "btn-skill-bomb-pushed";
	public static final String btn_skill_bomb_checked = "btn-skill-bomb-checked";
	
//	public static final String wheel_arc = "wheel-arc";
	public static final String wheel_arrow = "wheel-arrow";
	
	
	//public static final String map_background = "map-background";
	public static final String map_plus = "hud_map_plus";
	public static final String map_minus = "hud_map_less";
	public static final String map_background = "hud_map_background";
	

	
	//******
	// Items
	
	public static final String tutorial_page_item = "item-tutorial-page";

	public static final String health_up_item = "item-consumable-health-up";
	public static final String arrow_item = "item-consumable-arrow";
	public static final String bomb_item = "item-consumable-bomb";
	
	public static final String arrow = "arrow";
	public static final String bomb_animation = "bomb";
	public static final String explosion_animation = "explosion";


	

	public static BitmapFont font;
	public static BitmapFont greenFont;
	public static BitmapFont redFont;
	
	public static BitmapFont smallFont;

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
	
	public void dispose() {
		manager.dispose();
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
		AtlasRegion region = getInstance().manager.get(atlas, TextureAtlas.class).findRegion(file);
		region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		return region;
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
		font.getData().markupEnabled = true;
		greenFont = new BitmapFont(Gdx.files.internal("data/font.fnt"), Gdx.files.internal("data/font.png"), false);
		greenFont.setColor(Color.OLIVE);
		redFont = new BitmapFont(Gdx.files.internal("data/font.fnt"), Gdx.files.internal("data/font.png"), false);
		redFont.setColor(Color.RED);
		smallFont = new BitmapFont(Gdx.files.internal("data/font.fnt"), Gdx.files.internal("data/font.png"), false);
		smallFont.getData().setScale(0.8f);

	}

	public static void playSound (String sound) {
		if (Settings.soundEnabled) {
			getSound(sound).play(1);
		}
	}
}
