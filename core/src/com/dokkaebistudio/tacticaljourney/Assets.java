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
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;

public class Assets {
	
	public static final String atlas = "tacticaljourney.atlas";
	public static HashMap<String, RegionDescriptor> allSprites;

	public static RegionDescriptor menuBackground;
	public static RegionDescriptor mainTitle;
	
	public static Array<Sprite> player_standing;
	public static Array<Sprite> player_running;
	public static Array<Sprite> player_flying;
	public static RegionDescriptor shopkeeper;
	public static RegionDescriptor shopkeeper_carpet;
	public static RegionDescriptor recycling_machine;

	public static RegionDescriptor soulbender;
	public static RegionDescriptor goddess_statue;
	public static RegionDescriptor goddess_statue_broken;
	public static RegionDescriptor altar;
	public static RegionDescriptor sewing_machine;

	
	public static RegionDescriptor grid1;
	public static RegionDescriptor grid2;
	public static RegionDescriptor grid3;
	public static RegionDescriptor tile_ground;
	public static RegionDescriptor heavy_wall;
	public static RegionDescriptor wall;
	public static RegionDescriptor wall_destroyed;
	public static RegionDescriptor tile_pit;
	public static RegionDescriptor mud;
	public static RegionDescriptor mud_destroyed;
	public static RegionDescriptor tallGrass;
	public static RegionDescriptor tallGrassClover;
	public static RegionDescriptor tallGrass_destroyed;
	public static RegionDescriptor vineGrass;
	public static RegionDescriptor entangled_vines;
	public static RegionDescriptor creep_banana;
	
	public static RegionDescriptor wooden_panel;
	public static RegionDescriptor wall_gate_closed;
	public static RegionDescriptor wall_gate_opened;
	
	public static RegionDescriptor exit_opened;
	public static RegionDescriptor exit_closed;
	public static RegionDescriptor door_closed;
	public static RegionDescriptor door_opened;
	public static RegionDescriptor secret_door_closed;
	public static RegionDescriptor secret_door_opened;


	public static RegionDescriptor tile_movable;
	public static RegionDescriptor tile_attackable;
	public static RegionDescriptor tile_explosion;
	public static RegionDescriptor target_marker;
	public static RegionDescriptor ally_marker;
	
	public static RegionDescriptor tile_movable_waypoint;
	public static RegionDescriptor tile_movable_selected;
	
	public static RegionDescriptor btn_move_confirmation;
	public static RegionDescriptor btn_end_turn;
	public static RegionDescriptor btn_end_turn_pushed;
	
	public static RegionDescriptor hud_room_cleared;
	
	
	//*****************
	// VFX animations
	public static Array<Sprite> slash_animation;
	public static Array<Sprite> slash_critical_animation;

	public static Array<Sprite> explosion_animation;
	public static Array<Sprite> portal_animation;

	
	//**********
	// Popins
	
	public static RegionDescriptor lvl_up_background;
	public static RegionDescriptor lvl_up_choice_frame;
	public static RegionDescriptor lvl_up_choice_desc_panel;
	public static RegionDescriptor lvl_up_choice_reward_panel;
	
	public static RegionDescriptor profile_background;
	public static RegionDescriptor profile_alteration_background;
	public static RegionDescriptor btn_profile;
	public static RegionDescriptor btn_profile_pushed;
	public static RegionDescriptor item_infused_icon;
	
	public static RegionDescriptor btn_inspect;
	public static RegionDescriptor btn_inspect_pushed;
	public static RegionDescriptor btn_inspect_checked;
	
	public static RegionDescriptor inventory_background;
	public static RegionDescriptor inventory_money;
	public static RegionDescriptor inventory_slot;
	public static RegionDescriptor inventory_slot_disabled;
	public static RegionDescriptor btn_inventory;
	public static RegionDescriptor btn_inventory_pushed;
	
	public static RegionDescriptor small_popin_background;
	
	public static RegionDescriptor inventory_item_popin_background;
	public static RegionDescriptor inventory_lootable_item_background;

	public static RegionDescriptor dialog_background;
//	public static RegionDescriptor shop_item_background;	
	
	public static RegionDescriptor btn_skill_attack;
	public static RegionDescriptor btn_skill_attack_pushed;
	public static RegionDescriptor btn_skill_attack_checked;
	
	public static RegionDescriptor btn_skill_bow;
	public static RegionDescriptor btn_skill_bow_pushed;
	public static RegionDescriptor btn_skill_bow_checked;
	
	public static RegionDescriptor btn_skill_bomb;
	public static RegionDescriptor btn_skill_bomb_pushed;
	public static RegionDescriptor btn_skill_bomb_checked;

	public static RegionDescriptor key_slot;
	public static RegionDescriptor key;
	
	
	
//	public static RegionDescriptor wheel_arc;
	public static RegionDescriptor wheel_arrow;
	
	
	//***********************
	// Map
	
	//public static RegionDescriptor map_background;
	public static RegionDescriptor map_panel;
	public static RegionDescriptor map_plus;
	public static RegionDescriptor map_minus;
	public static RegionDescriptor map_background;
	
	public static RegionDescriptor map_player;
	public static RegionDescriptor map_corridor;
	public static RegionDescriptor map_room;
	public static RegionDescriptor map_room_enemy;
	public static RegionDescriptor map_room_exit;
	public static RegionDescriptor map_room_shop;
	public static RegionDescriptor map_room_statue;
	public static RegionDescriptor map_room_statue_enemy;
	public static RegionDescriptor map_room_key;
	public static RegionDescriptor map_room_key_enemy;
	public static RegionDescriptor map_room_item;
	public static RegionDescriptor map_room_item_enemy;
	public static RegionDescriptor map_room_gift;
	public static RegionDescriptor map_room_gift_enemy;
	public static RegionDescriptor map_room_chalice;
	public static RegionDescriptor map_room_chalice_enemy;
	public static RegionDescriptor map_room_mini_boss;
	public static RegionDescriptor map_room_mini_boss_enemy;
	public static RegionDescriptor map_room_boss;
	public static RegionDescriptor map_room_boss_enemy;
	public static RegionDescriptor map_room_start;
	public static RegionDescriptor map_room_unknown;
	
	
	//********
	// Journal
	
	public static RegionDescriptor journal_background;

	
	//**************
	// Projectiles
	
	public static RegionDescriptor projectile_arrow;
	public static RegionDescriptor projectile_bomb;
	public static Array<Sprite> projectile_web;
	public static Array<Sprite> projectile_pebble;
	public static Array<Sprite> projectile_shuriken;

	
	//********
	// Creep
	public static RegionDescriptor creep_web;
	public static RegionDescriptor creep_poison;
	public static RegionDescriptor creep_fire;
	public static Array<Sprite> creep_fire_animation;
	
	
	//***************
	// Destructibles
	
	public static RegionDescriptor destructible_vase;
	public static RegionDescriptor destructible_golden_vase;
	public static RegionDescriptor destructible_vase_destroyed;
	public static RegionDescriptor destructible_vase_big;
	public static RegionDescriptor destructible_vase_big_destroyed;
	public static RegionDescriptor destructible_ammo_crate;
	public static RegionDescriptor destructible_ammo_crate_destroyed;
	public static RegionDescriptor destructible_ammo_crate_reinforced;



	//********
	// Enemies
	
	public static RegionDescriptor enemy_spider;
	public static RegionDescriptor enemy_spider_venom;
	public static RegionDescriptor enemy_spider_web;
	public static RegionDescriptor enemy_scorpion;
	public static Array<Sprite> enemy_stinger;
	public static Array<Sprite> enemy_stinger_charge;
	
	public static Array<Sprite> enemy_pangolin_baby;
	public static Array<Sprite> enemy_pangolin_baby_rolled;
	public static Array<Sprite> enemy_pangolin_baby_rolling;
	
	public static Array<Sprite> enemy_tribesman_spear;
	public static Array<Sprite> enemy_tribesman_shield;
	public static Array<Sprite> enemy_tribesman_scout;
	public static Array<Sprite> enemy_tribesman_totem;


	//************
	// Bosses
	
	public static Array<Sprite> boss_pangolin_mother;
	public static Array<Sprite> boss_pangolin_mother_enraged;
	public static Array<Sprite> boss_pangolin_mother_crying;

	public static Array<Sprite> boss_shaman;
	public static Array<Sprite> boss_shaman_summoning;

	public static Array<Sprite> boss_shinobi_stand;
	public static Array<Sprite> boss_shinobi_run;
	public static Array<Sprite> boss_shinobi_attack;
	public static Array<Sprite> boss_shinobi_sleep;
	public static Array<Sprite> boss_shinobi_throw;
	public static Array<Sprite> boss_shinobi_clone;

	public static Array<Sprite> boss_orangutan_stand;

	
	//*************
	// Containers
	
	public static RegionDescriptor lootable_bones;
	public static RegionDescriptor lootable_bones_opened;
	public static RegionDescriptor lootable_satchel;
	public static RegionDescriptor lootable_satchel_opened;
	public static RegionDescriptor lootable_belongings;
	public static RegionDescriptor lootable_belongings_opened;
	public static RegionDescriptor lootable_orb_bag;
	public static RegionDescriptor lootable_orb_bag_opened;
	public static RegionDescriptor lootable_spell_book;
	public static RegionDescriptor lootable_spell_book_opened;

	//*********
	// Statuses
	
	public static RegionDescriptor status_poison;
	public static RegionDescriptor status_poison_full;
	public static Array<Sprite> poisoned_animation;	
	public static RegionDescriptor status_regen;
	public static RegionDescriptor status_regen_full;
	public static Array<Sprite> heal_animation;
	public static RegionDescriptor status_flight;
	public static RegionDescriptor status_flight_full;
	public static RegionDescriptor status_burning;
	public static RegionDescriptor status_burning_full;
	public static Array<Sprite> burning_animation;	
	public static RegionDescriptor status_entangled;
	public static RegionDescriptor status_entangled_full;
	public static RegionDescriptor status_death_door;
	public static RegionDescriptor status_death_door_full;
	public static RegionDescriptor death_door_animation;
	public static RegionDescriptor status_stunned;
	public static RegionDescriptor status_stunned_full;
	public static RegionDescriptor stunned_animation;

	public static Array<Sprite> holy_animation;	

	
	//*********
	// Orbs
	
	public static Array<Sprite> energy_orb;
	public static RegionDescriptor energy_orb_item;
	public static Array<Sprite> vegetal_orb;
	public static RegionDescriptor vegetal_orb_item;
	public static Array<Sprite> poison_orb;
	public static RegionDescriptor poison_orb_item;
	public static Array<Sprite> fire_orb;
	public static RegionDescriptor fire_orb_item;
	public static Array<Sprite> death_orb;
	public static RegionDescriptor death_orb_item;
	public static Array<Sprite> void_orb;
	public static RegionDescriptor void_orb_item;

	
	//******
	// Items
	
	public static RegionDescriptor universal_cure;

	public static RegionDescriptor tutorial_page_item;
	public static RegionDescriptor money_item;
	public static RegionDescriptor money_medium_item;
	public static RegionDescriptor health_up_item;
	public static RegionDescriptor regen_potion_item;
	public static RegionDescriptor wing_potion_item;
	public static RegionDescriptor fire_potion_item;
	public static RegionDescriptor purity_potion_item;
	public static RegionDescriptor web_sack_item;
	public static RegionDescriptor venom_gland_item;
	public static RegionDescriptor pebble_item;
	public static RegionDescriptor shuriken_item;
	public static RegionDescriptor smoke_bomb_item;
	public static RegionDescriptor banana_item;
	
	public static RegionDescriptor armor_up_item;
	public static RegionDescriptor armor_piece_item;
	
	public static RegionDescriptor orb_container_item;
	public static RegionDescriptor wormhole_shard_item;
	public static RegionDescriptor divine_catalyst_item;
	public static RegionDescriptor leather_item;
	
	public static RegionDescriptor scroll_doppelganger_item;
	public static RegionDescriptor scroll_teleportation_item;
	public static RegionDescriptor scroll_destruction_item;
	
	public static RegionDescriptor arrow_item;
	public static RegionDescriptor bomb_item;
	public static RegionDescriptor clover_item;
	
	// Personal items
	public static RegionDescriptor totem_of_kalamazoo;
	public static RegionDescriptor fata_morgana;
	public static RegionDescriptor mithridatium;
	public static RegionDescriptor nurse_eye_patch;
	public static RegionDescriptor vegetal_garment;
	public static RegionDescriptor ram_skull;
	public static RegionDescriptor colorful_tie;
	public static RegionDescriptor old_crown;
	public static RegionDescriptor memento_mori;
	public static RegionDescriptor headband;
	public static RegionDescriptor villanelle;
	public static RegionDescriptor powder_flask;
	public static RegionDescriptor camo_backpack;
	public static RegionDescriptor merchant_mask;
	public static RegionDescriptor hand_prothesis;
	public static RegionDescriptor left_jikatabi;
	public static RegionDescriptor right_jikatabi;
	public static RegionDescriptor shinobi_headband;
	public static RegionDescriptor silky_beard;
	public static RegionDescriptor scissorhand;
	public static RegionDescriptor durian;

	
	// Boss items
	public static RegionDescriptor pangolin_scale;

	
	
	
	
	// Vfx
	
	public static Array<Sprite> arrow;
	public static Array<Sprite> bomb_animation;
	public static RegionDescriptor kawarimi_log;
	public static RegionDescriptor projectile_kunai;
	public static Array<Sprite> smoke_bomb_animation;
	
	//**************************
	// Alterations
	
	public static RegionDescriptor blessing_vigor;
	public static RegionDescriptor blessing_strength;
	public static RegionDescriptor blessing_celerity;
	public static RegionDescriptor blessing_accuracy;
	public static RegionDescriptor blessing_luck;
	public static RegionDescriptor blessing_bowmaster_might;
	public static RegionDescriptor blessing_bowmaster_steadiness;
	public static RegionDescriptor blessing_bowmaster_accuracy;
	public static RegionDescriptor blessing_bombmaster_might;
	public static RegionDescriptor blessing_rock_thrower;
	public static RegionDescriptor blessing_of_kalamazoo;
	public static RegionDescriptor blessing_of_vilma;
	public static RegionDescriptor blessing_mithridatism;
	public static RegionDescriptor blessing_black_mamba;
	public static RegionDescriptor blessing_poisoner;
	public static RegionDescriptor blessing_pangolin;
	public static RegionDescriptor blessing_photosynthesis;
	public static RegionDescriptor blessing_calishka;
	public static RegionDescriptor blessing_fast_learner;
	public static RegionDescriptor blessing_cinders;
	public static RegionDescriptor blessing_acceptance;
	public static RegionDescriptor blessing_goat;
	public static RegionDescriptor blessing_contract_killer;
	public static RegionDescriptor blessing_fire_arrows;
	public static RegionDescriptor blessing_looter;
	public static RegionDescriptor blessing_mask_merchant;
	public static RegionDescriptor blessing_shurikenjutsu;
	public static RegionDescriptor blessing_kawarimi;
	public static RegionDescriptor blessing_hangeki;
	public static RegionDescriptor blessing_bunshin;
	public static RegionDescriptor blessing_indegistible;
	public static RegionDescriptor blessing_unfinished;
	public static RegionDescriptor blessing_orangutan;
	public static RegionDescriptor curse_frailty;
	public static RegionDescriptor curse_slowness;
	public static RegionDescriptor curse_weakness;
	public static RegionDescriptor curse_tremors;
	public static RegionDescriptor curse_bad_luck;
	public static RegionDescriptor curse_black_mamba;
	public static RegionDescriptor curse_acceptance;
	public static RegionDescriptor curse_pangolin_mother;
	public static RegionDescriptor curse_heavy_arrows;
	public static RegionDescriptor curse_shinobi;
	public static RegionDescriptor curse_unfinished;

	private static Assets instance;
	private AssetManager manager;
	
	
	public void initTextures() {
		allSprites = new HashMap<>();
		menuBackground = Assets.getTexture("background-test-menu");
		mainTitle = Assets.getTexture("main_title");

		player_standing = Assets.getAnimation("player_stand");
		player_running = Assets.getAnimation("player_run");
		player_flying = Assets.getAnimation("player_flight");
		
		shopkeeper = Assets.getTexture("shopkeeper");
		shopkeeper_carpet = Assets.getTexture("shopkeeper_carpet");
		recycling_machine = Assets.getTexture("recycling_machine");
		soulbender = Assets.getTexture("soulbender");
		goddess_statue = Assets.getTexture("godess_statue");
		goddess_statue_broken = Assets.getTexture("godess_statue_broken");
		altar = Assets.getTexture("altar");
		sewing_machine = Assets.getTexture("sewing_machine");

		grid1 = Assets.getTexture("grid_world1");
		grid2 = Assets.getTexture("grid_world2");
		grid3 = Assets.getTexture("grid_world3");
		tile_ground = Assets.getTexture("tile-test");
		heavy_wall = Assets.getTexture("tile-heavy-wall");
		wall = Assets.getTexture("tile-wall-test");
		wall_destroyed = Assets.getTexture("tile-wall-destroyed");
		tile_pit = Assets.getTexture("tile-pit-test");
		mud = Assets.getTexture("tile-mud-test");
		tallGrass = Assets.getTexture("tile-tall-grass");
		tallGrassClover = Assets.getTexture("tile-tall-grass-clover");
		tallGrass_destroyed = Assets.getTexture("tile-tall-grass-destroyed");
		vineGrass = Assets.getTexture("tile-vines-grass");
		mud_destroyed = Assets.getTexture("tile-mud-destroyed");
		entangled_vines = Assets.getTexture("entangled_vines");
		creep_banana = Assets.getTexture("creep-banana");
		
		wooden_panel = Assets.getTexture("wooden-panel");
		wall_gate_closed = Assets.getTexture("wall-gate-closed");
		wall_gate_opened = Assets.getTexture("wall-gate-opened");

		exit_opened = Assets.getTexture("exit_opened");
		exit_closed = Assets.getTexture("exit_closed");
		door_closed = Assets.getTexture("door-closed");
		door_opened = Assets.getTexture("door-opened");
		secret_door_closed = Assets.getTexture("secret-door-closed");
		secret_door_opened = Assets.getTexture("secret-door-opened");

		tile_movable = Assets.getTexture("tile-movable");
		tile_attackable = Assets.getTexture("tile-attackable");
		tile_explosion = Assets.getTexture("tile-explosion");
		ally_marker = Assets.getTexture("ally_marker");
		target_marker = Assets.getTexture("target_marker");
		
		tile_movable_waypoint = Assets.getTexture("tile-movable-waypoint");
		tile_movable_selected = Assets.getTexture("tile-movable-selected");
		
		btn_move_confirmation = Assets.getTexture("btn-move-confirmation");
		btn_end_turn = Assets.getTexture("btn-end-turn");
		btn_end_turn_pushed = Assets.getTexture("btn-end-turn-pushed");
		
		hud_room_cleared = Assets.getTexture("hud_room_cleared");
		
		
		//***********************
		// VFX animations
		slash_animation = Assets.getAnimation("anim_slash");
		slash_critical_animation = Assets.getAnimation("anim_slash_crit");
		explosion_animation = Assets.getAnimation("explosion");
		portal_animation = Assets.getAnimation("portal");
		
		//**********
		// Popins
		
		lvl_up_background = Assets.getTexture("hud_lvl_up_background");
		lvl_up_choice_frame = Assets.getTexture("hud_lvl_up_choice_frame");
		lvl_up_choice_desc_panel = Assets.getTexture("hud_lvl_up_choice_desc_panel");
		lvl_up_choice_reward_panel = Assets.getTexture("hud_lvl_up_choice_reward_panel");
		
		profile_background = Assets.getTexture("hud_profile_background");
		profile_alteration_background = Assets.getTexture("hud_profile_alteration_background");
		btn_profile = Assets.getTexture("btn-profile");
		btn_profile_pushed = Assets.getTexture("btn-profile-pushed");
		item_infused_icon = Assets.getTexture("item-infused-icon");
		
		btn_inspect = Assets.getTexture("btn-inspect");
		btn_inspect_pushed = Assets.getTexture("btn-inspect-pushed");
		btn_inspect_checked = Assets.getTexture("btn-inspect-checked");

		
		inventory_background = Assets.getTexture("hud_inventory_background");
		inventory_money = Assets.getTexture("hud_money");
		inventory_slot = Assets.getTexture("hud_inventory_slot");
		inventory_slot_disabled = Assets.getTexture("hud_inventory_slot_disabled");
		btn_inventory = Assets.getTexture("btn-inventory");
		btn_inventory_pushed = Assets.getTexture("btn-inventory-pushed");
		
		small_popin_background = Assets.getTexture("hud_small_popin_background");
		
		inventory_item_popin_background = Assets.getTexture("hud_inventory_item_popin_background");
		inventory_lootable_item_background = Assets.getTexture("hud_lootable_item_background");

		dialog_background = Assets.getTexture("dialog_background");
//		shop_item_background = Assets.getTexture("shop-item-background");
		
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
		map_room_item = Assets.getTexture("map_room_item");
		map_room_item_enemy = Assets.getTexture("map_room_item_enemy");
		map_room_gift = Assets.getTexture("map_room_gift");
		map_room_gift_enemy = Assets.getTexture("map_room_gift_enemy");
		map_room_chalice = Assets.getTexture("map_room_chalice");
		map_room_chalice_enemy = Assets.getTexture("map_room_chalice_enemy");
		map_room_mini_boss = Assets.getTexture("map_room_mini_boss");
		map_room_mini_boss_enemy = Assets.getTexture("map_room_mini_boss_enemy");
		map_room_boss = Assets.getTexture("map_room_boss");
		map_room_boss_enemy = Assets.getTexture("map_room_boss_enemy");
		map_room_start = Assets.getTexture("map_room_start");
		map_room_unknown = Assets.getTexture("map_room_unknown");
		
		//********
		// Journal
		
		journal_background = Assets.getTexture("hud_journal_background");

		
		//**************
		// Projectiles
		
		projectile_arrow = Assets.getTexture("arrow");
		projectile_bomb = Assets.getTexture("bomb");
		projectile_web = Assets.getAnimation("projectile-web");
		projectile_pebble = Assets.getAnimation("item-consumable-pebble");
		projectile_shuriken = Assets.getAnimation("item-consumable-shuriken");

		
		//********
		// Creep
		creep_web = Assets.getTexture("creep-web");
		creep_poison = Assets.getTexture("creep-poison");
		creep_fire = Assets.getTexture("creep-fire");
		creep_fire_animation = Assets.getAnimation("creep-fire");

		
		//***************
		// Destructibles
		
		destructible_vase = Assets.getTexture("vase");
		destructible_golden_vase = Assets.getTexture("golden_vase");
		destructible_vase_destroyed = Assets.getTexture("vase_destroyed");
		destructible_vase_big = Assets.getTexture("vase_big");
		destructible_vase_big_destroyed = Assets.getTexture("vase_big_destroyed");
		destructible_ammo_crate = Assets.getTexture("ammo_crate");
		destructible_ammo_crate_destroyed = Assets.getTexture("ammo_crate_destroyed");
		destructible_ammo_crate_reinforced = Assets.getTexture("ammo_crate_reinforced");


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
		
		enemy_tribesman_spear = Assets.getAnimation("tribesman_spear");
		enemy_tribesman_shield = Assets.getAnimation("tribesman_shield");
		enemy_tribesman_scout = Assets.getAnimation("tribesman_scout");
		enemy_tribesman_totem = Assets.getAnimation("tribesman_totem");

		
		//************
		// Bosses
		
		boss_pangolin_mother = Assets.getAnimation("pangolin");
		boss_pangolin_mother_enraged = Assets.getAnimation("pangolin_enraged");
		boss_pangolin_mother_crying = Assets.getAnimation("pangolin_crying");
		
		boss_shaman = Assets.getAnimation("tribesman_shaman");
		boss_shaman_summoning = Assets.getAnimation("tribesman_shaman_summon");
		
		boss_shinobi_stand = Assets.getAnimation("shinobi_stand");
		boss_shinobi_run = Assets.getAnimation("shinobi_run");
		boss_shinobi_attack = Assets.getAnimation("shinobi_attack");
		boss_shinobi_sleep = Assets.getAnimation("shinobi_sleep");
		boss_shinobi_throw = Assets.getAnimation("shinobi_throw");
		boss_shinobi_clone = Assets.getAnimation("shinobi_clone");
		
		boss_orangutan_stand = Assets.getAnimation("orangutan_stand");

		
		//*************
		// Containers
		
		lootable_bones = Assets.getTexture("remains_bones");
		lootable_bones_opened = Assets.getTexture("remains_bones_opened");
		lootable_satchel = Assets.getTexture("remains_satchel");
		lootable_satchel_opened = Assets.getTexture("remains_satchel_opened");
		lootable_belongings = Assets.getTexture("remains_belongings");
		lootable_belongings_opened = Assets.getTexture("remains_belongings_opened");
		lootable_orb_bag = Assets.getTexture("remains_orb_bag");
		lootable_orb_bag_opened = Assets.getTexture("remains_orb_bag_opened");
		lootable_spell_book = Assets.getTexture("remains_spellbook");
		lootable_spell_book_opened = Assets.getTexture("remains_spellbook_opened");

		//*********
		// Statuses
		
		status_poison = Assets.getTexture("poison");
		status_poison_full = Assets.getTexture("poison_full");
		poisoned_animation = Assets.getAnimation("poisoned");
		status_regen = Assets.getTexture("regen");
		status_regen_full = Assets.getTexture("regen_full");
		heal_animation = Assets.getAnimation("healing");
		status_flight = Assets.getTexture("flight");
		status_flight_full = Assets.getTexture("flight_full");
		status_burning = Assets.getTexture("burning");
		status_burning_full = Assets.getTexture("burning_full");
		burning_animation = Assets.getAnimation("burning_animation");
		status_entangled = Assets.getTexture("entangled");
		status_entangled_full = Assets.getTexture("entangled_full");
		status_death_door = Assets.getTexture("death_door");
		status_death_door_full = Assets.getTexture("death_door_full");
		death_door_animation = Assets.getTexture("death_door_animation");
		status_stunned = Assets.getTexture("stunned");
		status_stunned_full = Assets.getTexture("stunned_full");
		stunned_animation = Assets.getTexture("stunned_animation");

		
		holy_animation = Assets.getAnimation("holy_aura");

		
		//*************
		// Orbs
		
		energy_orb = Assets.getAnimation("energy_orb");
		energy_orb_item = Assets.getTexture("item-orb-energy");
		vegetal_orb = Assets.getAnimation("vegetal_orb");
		vegetal_orb_item = Assets.getTexture("item-orb-vegetal");
		poison_orb = Assets.getAnimation("poison_orb");
		poison_orb_item = Assets.getTexture("item-orb-poison");
		fire_orb = Assets.getAnimation("fire_orb");
		fire_orb_item = Assets.getTexture("item-orb-fire");
		death_orb = Assets.getAnimation("death_orb");
		death_orb_item = Assets.getTexture("item-orb-death");
		void_orb = Assets.getAnimation("void_orb");
		void_orb_item = Assets.getTexture("item-orb-void");

		//******
		// Items

		universal_cure = Assets.getTexture("universal-cure");

		tutorial_page_item = Assets.getTexture("item-tutorial-page");

		money_item = Assets.getTexture("item-money");
		money_medium_item = Assets.getTexture("item-money-medium");
		health_up_item = Assets.getTexture("item-consumable-health-up");
		regen_potion_item = Assets.getTexture("item-consumable-regen-potion");
		wing_potion_item = Assets.getTexture("item-consumable-wing-potion");
		fire_potion_item = Assets.getTexture("item-consumable-fire-potion");
		purity_potion_item = Assets.getTexture("item-consumable-purity-potion");
		web_sack_item = Assets.getTexture("item-web-sack");
		venom_gland_item = Assets.getTexture("item-consumable-venom-gland");
		pebble_item = Assets.getTexture("item-consumable-pebble");
		shuriken_item = Assets.getTexture("item-consumable-shuriken");
		smoke_bomb_item = Assets.getTexture("item-consumable-smoke-bomb");
		banana_item = Assets.getTexture("item-banana");
		armor_up_item = Assets.getTexture("item-consumable-armor-up");
		armor_piece_item = Assets.getTexture("item-consumable-armor-piece");
		orb_container_item = Assets.getTexture("item-consumable-orb-container");
		wormhole_shard_item = Assets.getTexture("item-consumable-wormhole-shard");
		divine_catalyst_item = Assets.getTexture("item-consumable-divine-catalyst");
		leather_item = Assets.getTexture("item-consumable-leather");
		
		scroll_doppelganger_item = Assets.getTexture("item-scroll-doppelganger");
		scroll_teleportation_item = Assets.getTexture("item-scroll-teleportation");
		scroll_destruction_item = Assets.getTexture("item-scroll-destruction");
		
		arrow_item = Assets.getTexture("item-consumable-arrow");
		bomb_item = Assets.getTexture("item-consumable-bomb");
		clover_item = Assets.getTexture("item-clover");
		
		// Personal items
		totem_of_kalamazoo = Assets.getTexture("item-infusable-totem-of-kalamazoo");
		fata_morgana = Assets.getTexture("item-infusable-fata-morgana");
		mithridatium = Assets.getTexture("item-infusable-mithridatium");
		nurse_eye_patch = Assets.getTexture("item-infusable-eye-patch");
		vegetal_garment = Assets.getTexture("item-infusable-leafy-bra");
		ram_skull = Assets.getTexture("item-infusable-ram-skull");
		colorful_tie = Assets.getTexture("item-infusable-colorful-tie");
		old_crown = Assets.getTexture("item-infusable-old-crown");
		memento_mori = Assets.getTexture("item-infusable-memento-mori");
		headband = Assets.getTexture("item-infusable-headband");
		villanelle = Assets.getTexture("item-infusable-villanelle");
		powder_flask = Assets.getTexture("item-infusable-powder-flask");
		camo_backpack = Assets.getTexture("item-infusable-camo-backpack");
		merchant_mask = Assets.getTexture("item-infusable-merchant-mask");
		hand_prothesis = Assets.getTexture("item-infusable-hand-prosthesis");
		left_jikatabi = Assets.getTexture("item-infusable-left-jikatabi");
		right_jikatabi = Assets.getTexture("item-infusable-right-jikatabi");
		shinobi_headband = Assets.getTexture("item-infusable-ninja-headband");
		silky_beard = Assets.getTexture("item-infusable-silky-beard");
		scissorhand = Assets.getTexture("item-infusable-scissorhand");
		durian = Assets.getTexture("item-infusable-durian");
		
		// Boss items
		pangolin_scale = Assets.getTexture("item-infusable-pangolin-scale");
		
		

		
		//********
		// Vfx
		
		arrow = Assets.getAnimation("arrow");
		bomb_animation = Assets.getAnimation("bomb");
		kawarimi_log = Assets.getTexture("kawarimi_log");
		projectile_kunai = Assets.getTexture("kunai");
		smoke_bomb_animation = Assets.getAnimation("smokebomb_anim");

		
		//*****************
		// Alterations
		
		blessing_vigor = Assets.getTexture("blessing-vigor");
		blessing_strength = Assets.getTexture("blessing-strength");
		blessing_celerity = Assets.getTexture("blessing-celerity");
		blessing_accuracy = Assets.getTexture("blessing-accuracy");
		blessing_luck = Assets.getTexture("blessing-luck");
		blessing_bowmaster_might = Assets.getTexture("blessing-bowmaster-might");
		blessing_bowmaster_steadiness = Assets.getTexture("blessing-bowmaster-steadiness");
		blessing_bowmaster_accuracy = Assets.getTexture("blessing-bowmaster-accuracy");
		blessing_bombmaster_might = Assets.getTexture("blessing-bombmaster-might");
		blessing_rock_thrower = Assets.getTexture("blessing-rock-thrower");
		blessing_of_kalamazoo = Assets.getTexture("blessing-kalamazoo");
		blessing_of_vilma = Assets.getTexture("blessing-vilma");
		blessing_mithridatism = Assets.getTexture("blessing-mithridatism");
		blessing_black_mamba = Assets.getTexture("blessing-black-mamba");
		blessing_poisoner = Assets.getTexture("blessing-poisoner");
		blessing_pangolin = Assets.getTexture("blessing-pangolin");
		blessing_photosynthesis = Assets.getTexture("blessing-photosynthesis");
		blessing_calishka = Assets.getTexture("blessing-calishka");
		blessing_fast_learner = Assets.getTexture("blessing-fast-learner");
		blessing_cinders = Assets.getTexture("blessing-cinders");
		blessing_acceptance = Assets.getTexture("blessing-acceptance");
		blessing_goat = Assets.getTexture("blessing-goat");
		blessing_contract_killer = Assets.getTexture("blessing-bounty");
		blessing_fire_arrows = Assets.getTexture("blessing-fire-arrow");
		blessing_looter = Assets.getTexture("blessing-looter");
		blessing_mask_merchant = Assets.getTexture("blessing-mask-merchant");
		blessing_shurikenjutsu = Assets.getTexture("blessing-shurikenjutsu");
		blessing_kawarimi = Assets.getTexture("blessing-kawarimi");
		blessing_hangeki = Assets.getTexture("blessing-hangeki");
		blessing_bunshin = Assets.getTexture("blessing-bunshin");
		blessing_indegistible = Assets.getTexture("blessing-indegistible");
		blessing_unfinished = Assets.getTexture("blessing-unfinished");
		blessing_orangutan = Assets.getTexture("blessing-orangutan");
		curse_frailty = Assets.getTexture("curse-frailty");
		curse_slowness = Assets.getTexture("curse-slowness");
		curse_weakness = Assets.getTexture("curse-weakness");
		curse_tremors = Assets.getTexture("curse-tremors");
		curse_bad_luck = Assets.getTexture("curse-bad-luck");
		curse_black_mamba = Assets.getTexture("curse-black-mamba");
		curse_acceptance = Assets.getTexture("curse-acceptance");
		curse_pangolin_mother = Assets.getTexture("curse-pangolin-mother");
		curse_heavy_arrows = Assets.getTexture("curse-heavy-arrow");
		curse_shinobi = Assets.getTexture("curse-shinobi");
		curse_unfinished = Assets.getTexture("curse-unfinished");
	}
	
	public static void addSprite(RegionDescriptor sprite) {
		allSprites.put(sprite.getName(), sprite);
	}
	
	public static RegionDescriptor findSprite(String name) {
		if (name == null) return null;
		RegionDescriptor regionDescriptor = allSprites.get(name);
		return regionDescriptor;
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
	

//	private void registerMusic(String file) {
//		this.manager.load(file, Music.class);
//	}
	public static Music getMusic(String file) {
		return getInstance().manager.get(file);
	}

//	private void registerSound(String file) {
//		this.manager.load(file, Sound.class);
//	}
	public static Sound getSound(String file){
		return getInstance().manager.get(file);
	}

	public static void playSound (String sound) {
		if (Settings.soundEnabled) {
			getSound(sound).play(1);
		}
	}
}
