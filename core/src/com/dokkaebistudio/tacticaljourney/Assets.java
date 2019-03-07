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
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;

public class Assets {
	
	public static final String atlas = "tacticaljourney.atlas";

	public static AtlasRegion menuBackground;
	
	public static Array<Sprite> player_standing;
	public static Array<Sprite> player_flying;
	public static AtlasRegion shopkeeper;
	public static AtlasRegion godess_statue;
	public static AtlasRegion godess_statue_broken;
	
	
	public static AtlasRegion grid1;
	public static AtlasRegion grid2;
	public static AtlasRegion tile_ground;
	public static AtlasRegion wall;
	public static AtlasRegion wall_destroyed;
	public static AtlasRegion tile_pit;
	public static AtlasRegion mud;
	public static AtlasRegion mud_destroyed;
	
	public static AtlasRegion exit_opened;
	public static AtlasRegion exit_closed;
	public static AtlasRegion door_closed;
	public static AtlasRegion door_opened;

	public static AtlasRegion tile_movable;
	public static AtlasRegion tile_attackable;
	public static AtlasRegion tile_explosion;
	
	public static AtlasRegion tile_movable_waypoint;
	public static AtlasRegion tile_movable_selected;
	
	public static AtlasRegion btn_move_confirmation;
	public static AtlasRegion btn_end_turn;
	public static AtlasRegion btn_end_turn_pushed;
	
	public static AtlasRegion hud_room_cleared;
	
	//**********
	// Popins
	
	public static AtlasRegion popin_big_btn_up;
	public static AtlasRegion popin_big_btn_down;
	public static AtlasRegion popin_small_btn_up;
	public static AtlasRegion popin_small_btn_down;
	
	public static AtlasRegion lvl_up_background;
	public static AtlasRegion lvl_up_choice_frame;
	public static AtlasRegion lvl_up_choice_desc_panel;
	public static AtlasRegion lvl_up_choice_reward_panel;
	
	public static AtlasRegion profile_background;
	public static AtlasRegion profile_alteration_background;
	public static AtlasRegion btn_profile;
	public static AtlasRegion btn_profile_pushed;
	
	public static AtlasRegion inventory_background;
	public static AtlasRegion inventory_money;
	public static AtlasRegion inventory_slot;
	public static AtlasRegion inventory_slot_disabled;
	public static AtlasRegion btn_inventory;
	public static AtlasRegion btn_inventory_pushed;
	
	public static AtlasRegion inventory_item_popin_background;
	public static AtlasRegion inventory_lootable_item_background;

	public static AtlasRegion dialog_background;
	public static AtlasRegion shop_item_background;	
	
	public static AtlasRegion btn_skill_attack;
	public static AtlasRegion btn_skill_attack_pushed;
	public static AtlasRegion btn_skill_attack_checked;
	
	public static AtlasRegion btn_skill_bow;
	public static AtlasRegion btn_skill_bow_pushed;
	public static AtlasRegion btn_skill_bow_checked;
	
	public static AtlasRegion btn_skill_bomb;
	public static AtlasRegion btn_skill_bomb_pushed;
	public static AtlasRegion btn_skill_bomb_checked;

	public static AtlasRegion key_slot;
	public static AtlasRegion key;
	
	
	
//	public static AtlasRegion wheel_arc;
	public static AtlasRegion wheel_arrow;
	
	
	//***********************
	// Map
	
	//public static AtlasRegion map_background;
	public static AtlasRegion map_panel;
	public static AtlasRegion map_plus;
	public static AtlasRegion map_minus;
	public static AtlasRegion map_background;
	
	public static AtlasRegion map_player;
	public static AtlasRegion map_corridor;
	public static AtlasRegion map_room;
	public static AtlasRegion map_room_enemy;
	public static AtlasRegion map_room_exit;
	public static AtlasRegion map_room_shop;
	public static AtlasRegion map_room_statue;
	public static AtlasRegion map_room_statue_enemy;
	public static AtlasRegion map_room_key;
	public static AtlasRegion map_room_key_enemy;
	public static AtlasRegion map_room_start;
	public static AtlasRegion map_room_unknown;
	
	
	//********
	// Journal
	
	public static AtlasRegion journal_background;

	
	//**************
	// Projectiles
	
	public static AtlasRegion projectile_arrow;
	public static AtlasRegion projectile_bomb;
	public static AtlasRegion projectile_web;

	
	//********
	// Creep
	public static AtlasRegion creep_web;
	public static AtlasRegion creep_poison;
	public static AtlasRegion creep_fire;
	public static Array<Sprite> creep_fire_animation;
	
	
	//***************
	// Destructibles
	
	public static AtlasRegion destructible_vase;
	public static AtlasRegion destructible_vase_destroyed;
	public static AtlasRegion destructible_vase_big;
	public static AtlasRegion destructible_vase_big_destroyed;



	//********
	// Enemies
	
	public static AtlasRegion enemy_spider;
	public static AtlasRegion enemy_spider_venom;
	public static AtlasRegion enemy_spider_web;
	public static AtlasRegion enemy_scorpion;
	public static Array<Sprite> enemy_stinger;
	public static Array<Sprite> enemy_stinger_charge;
	
	public static Array<Sprite> enemy_pangolin_baby;
	public static Array<Sprite> enemy_pangolin_baby_rolled;
	public static Array<Sprite> enemy_pangolin_baby_rolling;

	
	//************
	// Bosses
	
	public static Array<Sprite> boss_pangolin_mother;
	public static Array<Sprite> boss_pangolin_mother_enraged;
	public static Array<Sprite> boss_pangolin_mother_crying;

	
	//*************
	// Containers
	
	public static AtlasRegion remains_bones;
	public static AtlasRegion remains_bones_opened;
	public static AtlasRegion remains_satchel;
	public static AtlasRegion remains_satchel_opened;

	//*********
	// Statuses
	
	public static AtlasRegion status_poison;
	public static AtlasRegion status_regen;
	public static AtlasRegion status_flight;
	public static AtlasRegion status_burning;
	
	//******
	// Items
	
	public static AtlasRegion tutorial_page_item;

	public static AtlasRegion money_item;
	public static AtlasRegion health_up_item;
	public static AtlasRegion regen_potion_item;
	public static AtlasRegion wing_potion_item;
	public static AtlasRegion fire_potion_item;
	public static AtlasRegion web_sack_item;
	public static AtlasRegion venom_gland_item;
	
	public static AtlasRegion armor_up_item;
	public static AtlasRegion armor_piece_item;
	
	public static AtlasRegion arrow_item;
	public static AtlasRegion bomb_item;
	
	public static AtlasRegion totem_of_kalamazoo;
	public static AtlasRegion fata_morgana;
	public static AtlasRegion mithridatium;
	public static AtlasRegion nurse_eye_patch;

	
	
	
	
	
	
	public static AtlasRegion arrow;
	public static Array<Sprite> bomb_animation;
	public static Array<Sprite> explosion_animation;

	
	//**************************
	// Alterations
	
	public static AtlasRegion blessing_vigor;
	public static AtlasRegion blessing_strength;
	public static AtlasRegion blessing_celerity;
	public static AtlasRegion blessing_of_kalamazoo;
	public static AtlasRegion blessing_of_vilma;
	public static AtlasRegion blessing_mithridatism;
	public static AtlasRegion blessing_black_mamba;
	public static AtlasRegion curse_frailty;
	public static AtlasRegion curse_slowness;
	public static AtlasRegion curse_weakness;
	public static AtlasRegion curse_black_mamba;

	

	public static BitmapFont font;	
	public static BitmapFont smallFont;

	private static Assets instance;
	private AssetManager manager;
	
	
	public void initTextures() {
		menuBackground = Assets.getTexture("background-test-menu");

		player_standing = Assets.getAnimation("player_stand");
		player_flying = Assets.getAnimation("player_flight");
		
		shopkeeper = Assets.getTexture("shopkeeper");
		godess_statue = Assets.getTexture("godess_statue");
		godess_statue_broken = Assets.getTexture("godess_statue_broken");

		grid1 = Assets.getTexture("grid_world1");
		grid2 = Assets.getTexture("grid_world2");
		tile_ground = Assets.getTexture("tile-test");
		wall = Assets.getTexture("tile-wall-test");
		wall_destroyed = Assets.getTexture("tile-wall-destroyed");
		tile_pit = Assets.getTexture("tile-pit-test");
		mud = Assets.getTexture("tile-mud-test");
		mud_destroyed = Assets.getTexture("tile-mud-destroyed");
		
		exit_opened = Assets.getTexture("exit_opened");
		exit_closed = Assets.getTexture("exit_closed");
		door_closed = Assets.getTexture("door-closed");
		door_opened = Assets.getTexture("door-opened");

		tile_movable = Assets.getTexture("tile-movable");
		tile_attackable = Assets.getTexture("tile-attackable");
		tile_explosion = Assets.getTexture("tile-explosion");
		
		tile_movable_waypoint = Assets.getTexture("tile-movable-waypoint");
		tile_movable_selected = Assets.getTexture("tile-movable-selected");
		
		btn_move_confirmation = Assets.getTexture("btn-move-confirmation");
		btn_end_turn = Assets.getTexture("btn-end-turn");
		btn_end_turn_pushed = Assets.getTexture("btn-end-turn-pushed");
		
		hud_room_cleared = Assets.getTexture("hud_room_cleared");
		
		
		//**********
		// Popins
		
		popin_big_btn_up = Assets.getTexture("hud_inventory_item_popin_btn_up");
		popin_big_btn_down = Assets.getTexture("hud_inventory_item_popin_btn_down");
		popin_small_btn_up = Assets.getTexture("hud_lvl_up_choice_claim_btn");
		popin_small_btn_down = Assets.getTexture("hud_lvl_up_choice_claim_btn_pushed");

		
		lvl_up_background = Assets.getTexture("hud_lvl_up_background");
		lvl_up_choice_frame = Assets.getTexture("hud_lvl_up_choice_frame");
		lvl_up_choice_desc_panel = Assets.getTexture("hud_lvl_up_choice_desc_panel");
		lvl_up_choice_reward_panel = Assets.getTexture("hud_lvl_up_choice_reward_panel");
		
		profile_background = Assets.getTexture("hud_profile_background");
		profile_alteration_background = Assets.getTexture("hud_profile_alteration_background");
		btn_profile = Assets.getTexture("btn-profile");
		btn_profile_pushed = Assets.getTexture("btn-profile-pushed");
		
		inventory_background = Assets.getTexture("hud_inventory_background");
		inventory_money = Assets.getTexture("hud_money");
		inventory_slot = Assets.getTexture("hud_inventory_slot");
		inventory_slot_disabled = Assets.getTexture("hud_inventory_slot_disabled");
		btn_inventory = Assets.getTexture("btn-inventory");
		btn_inventory_pushed = Assets.getTexture("btn-inventory-pushed");
		
		inventory_item_popin_background = Assets.getTexture("hud_inventory_item_popin_background");
		inventory_lootable_item_background = Assets.getTexture("hud_lootable_item_background");

		dialog_background = Assets.getTexture("dialog_background");
		shop_item_background = Assets.getTexture("shop-item-background");
		
		btn_skill_attack = Assets.getTexture("btn-skill-slash");
		btn_skill_attack_pushed = Assets.getTexture("btn-skill-slash-pushed");
		btn_skill_attack_checked = Assets.getTexture("btn-skill-slash-checked");
		
		btn_skill_bow = Assets.getTexture("btn-skill-bow");
		btn_skill_bow_pushed = Assets.getTexture("btn-skill-bow-pushed");
		btn_skill_bow_checked = Assets.getTexture("btn-skill-bow-checked");
		
		btn_skill_bomb = Assets.getTexture("btn-skill-bomb");
		btn_skill_bomb_pushed = Assets.getTexture("btn-skill-bomb-pushed");
		btn_skill_bomb_checked = Assets.getTexture("btn-skill-bomb-checked");
		
		key_slot = Assets.getTexture("hud_key_slot");
		key = Assets.getTexture("key");
		
//		wheel_arc = Assets.getTexture("wheel-arc");
		wheel_arrow = Assets.getTexture("wheel-arrow");
		
		//****************
		// Map
		map_panel = Assets.getTexture("hud_map_panel");
		map_plus = Assets.getTexture("hud_map_plus");
		map_minus = Assets.getTexture("hud_map_less");
		map_background = Assets.getTexture("hud_map_background");
		
		map_player = Assets.getTexture("map_player");
		map_corridor = Assets.getTexture("map_corridor");
		map_room = Assets.getTexture("map_room");
		map_room_enemy = Assets.getTexture("map_room_enemy");
		map_room_exit = Assets.getTexture("map_room_exit");
		map_room_shop = Assets.getTexture("map_room_shop");
		map_room_statue = Assets.getTexture("map_room_statue");
		map_room_statue_enemy = Assets.getTexture("map_room_statue_enemy");
		map_room_key = Assets.getTexture("map_room_key");
		map_room_key_enemy = Assets.getTexture("map_room_key_enemy");
		map_room_start = Assets.getTexture("map_room_start");
		map_room_unknown = Assets.getTexture("map_room_unknown");
		
		//********
		// Journal
		
		journal_background = Assets.getTexture("hud_journal_background");

		
		//**************
		// Projectiles
		
		projectile_arrow = Assets.getTexture("arrow");
		projectile_bomb = Assets.getTexture("bomb");
		projectile_web = Assets.getTexture("projectile-web");

		
		//********
		// Creep
		creep_web = Assets.getTexture("creep-web");
		creep_poison = Assets.getTexture("creep-poison");
		creep_fire = Assets.getTexture("creep-fire");
		creep_fire_animation = Assets.getAnimation("creep-fire");

		
		//***************
		// Destructibles
		
		destructible_vase = Assets.getTexture("vase");
		destructible_vase_destroyed = Assets.getTexture("vase_destroyed");
		destructible_vase_big = Assets.getTexture("vase_big");
		destructible_vase_big_destroyed = Assets.getTexture("vase_big_destroyed");


		//********
		// Enemies
		
		enemy_spider = Assets.getTexture("enemy-test");
		enemy_spider_venom = Assets.getTexture("enemy-spider-venom");
		enemy_spider_web = Assets.getTexture("enemy-spider-web");
		enemy_scorpion = Assets.getTexture("enemy-scorpion-test");
		enemy_stinger = Assets.getAnimation("stinger");
		enemy_stinger_charge = Assets.getAnimation("stinger_charge");
		
		enemy_pangolin_baby = Assets.getAnimation("pangolin_baby");
		enemy_pangolin_baby_rolled = Assets.getAnimation("pangolin_baby_rolled");
		enemy_pangolin_baby_rolling = Assets.getAnimation("pangolin_baby_rolling");

		
		//************
		// Bosses
		
		boss_pangolin_mother = Assets.getAnimation("pangolin");
		boss_pangolin_mother_enraged = Assets.getAnimation("pangolin_enraged");
		boss_pangolin_mother_crying = Assets.getAnimation("pangolin_crying");
		
		
		//*************
		// Containers
		
		remains_bones = Assets.getTexture("remains_bones");
		remains_bones_opened = Assets.getTexture("remains_bones_opened");
		remains_satchel = Assets.getTexture("remains_satchel");
		remains_satchel_opened = Assets.getTexture("remains_satchel_opened");

		//*********
		// Statuses
		
		status_poison = Assets.getTexture("poison");
		status_regen = Assets.getTexture("regen");
		status_flight = Assets.getTexture("flight");
		status_burning = Assets.getTexture("burning");

		//******
		// Items

		tutorial_page_item = Assets.getTexture("item-tutorial-page");

		money_item = Assets.getTexture("item-money");
		health_up_item = Assets.getTexture("item-consumable-health-up");
		regen_potion_item = Assets.getTexture("item-consumable-regen-potion");
		wing_potion_item = Assets.getTexture("item-consumable-wing-potion");
		fire_potion_item = Assets.getTexture("item-consumable-fire-potion");
		web_sack_item = Assets.getTexture("item-web-sack");
		venom_gland_item = Assets.getTexture("item-consumable-venom-gland");
		armor_up_item = Assets.getTexture("item-consumable-armor-up");
		armor_piece_item = Assets.getTexture("item-consumable-armor-piece");
		
		arrow_item = Assets.getTexture("item-consumable-arrow");
		bomb_item = Assets.getTexture("item-consumable-bomb");
		
		totem_of_kalamazoo = Assets.getTexture("item-infusable-totem-of-kalamazoo");
		fata_morgana = Assets.getTexture("item-infusable-fata-morgana");
		mithridatium = Assets.getTexture("item-infusable-mithridatium");
		nurse_eye_patch = Assets.getTexture("item-infusable-eye-patch");
		
		
		arrow = Assets.getTexture("arrow");
		bomb_animation = Assets.getAnimation("bomb");
		explosion_animation = Assets.getAnimation("explosion");

		
		//*****************
		// Alterations
		
		blessing_vigor = Assets.getTexture("blessing-vigor");
		blessing_strength = Assets.getTexture("blessing-strength");
		blessing_celerity = Assets.getTexture("blessing-celerity");
		blessing_of_kalamazoo = Assets.getTexture("blessing-kalamazoo");
		blessing_of_vilma = Assets.getTexture("blessing-vilma");
		blessing_mithridatism = Assets.getTexture("blessing-mithridatism");
		blessing_black_mamba = Assets.getTexture("blessing-black-mamba");
		curse_frailty = Assets.getTexture("curse-frailty");
		curse_slowness = Assets.getTexture("curse-slowness");
		curse_weakness = Assets.getTexture("curse-weakness");
		curse_black_mamba = Assets.getTexture("curse-black-mamba");

	}
	
	

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
		instance = null;
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
		smallFont = new BitmapFont(Gdx.files.internal("data/font.fnt"), Gdx.files.internal("data/font.png"), false);
		smallFont.getData().markupEnabled = true;
		smallFont.getData().setScale(0.8f);

	}

	public static void playSound (String sound) {
		if (Settings.soundEnabled) {
			getSound(sound).play(1);
		}
	}
}
