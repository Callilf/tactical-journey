package com.dokkaebistudio.tacticaljourney;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class AnimationSingleton {

	private static AnimationSingleton instance;
	
	public static AnimationSingleton getInstance() {
		if (instance == null) {
			instance = new AnimationSingleton();
		}
		return instance;
	}
	
	
	
	public Animation<Sprite> player_standing;
	public Animation<Sprite> player_running;
	public Animation<Sprite> player_flying;
	
	public Animation<Sprite> bomb_slow;
	public Animation<Sprite> bomb_fast;
	public Animation<Sprite> explosion;
	public Animation<Sprite> fire;
	
	public Animation<Sprite> energyOrb;
	public Animation<Sprite> vegetalOrb;
	public Animation<Sprite> poisonOrb;
	public Animation<Sprite> fireOrb;
	public Animation<Sprite> deathOrb;
	public Animation<Sprite> voidOrb;
	
	public Animation<Sprite> stingerFly;
	public Animation<Sprite> stingerAttack;
	
	public Animation<Sprite> pangolinBabyStand;
	public Animation<Sprite> pangolinBabyRolled;
	public Animation<Sprite> pangolinBabyRolling;
	
	public Animation<Sprite> pangolinMotherStand;
	public Animation<Sprite> pangolinMotherEnragedStand;
	public Animation<Sprite> pangolinMotherCrying;
	
	public Animation<Sprite> tribesmenSpearStand;
	public Animation<Sprite> tribesmenShieldStand;
	public Animation<Sprite> tribesmenScoutStand;
	public Animation<Sprite> tribesmenTotem;
	
	public Animation<Sprite> tribesmenShamanStand;
	public Animation<Sprite> tribesmenShamanSummoning;


	private void init() {
		player_standing = new Animation<Sprite>(0.2f, Assets.player_standing, PlayMode.LOOP);
		player_running = new Animation<Sprite>(0.2f, Assets.player_running, PlayMode.LOOP);
		player_flying = new Animation<Sprite>(0.2f, Assets.player_flying, PlayMode.LOOP_PINGPONG);

		bomb_slow = new Animation<Sprite>(0.2f, Assets.bomb_animation, PlayMode.LOOP);
		bomb_fast = new Animation<Sprite>(0.1f, Assets.bomb_animation, PlayMode.LOOP);
		explosion = new Animation<Sprite>(0.1f, Assets.explosion_animation, PlayMode.NORMAL);
		
		fire = new Animation<Sprite>(0.17f, Assets.creep_fire_animation, PlayMode.LOOP);
		
		energyOrb = new Animation<Sprite>(0.2f, Assets.energy_orb, PlayMode.LOOP);
		vegetalOrb = new Animation<Sprite>(0.2f, Assets.vegetal_orb, PlayMode.LOOP);
		poisonOrb = new Animation<Sprite>(0.2f, Assets.poison_orb, PlayMode.LOOP);
		fireOrb = new Animation<Sprite>(0.2f, Assets.fire_orb, PlayMode.LOOP);
		deathOrb = new Animation<Sprite>(0.2f, Assets.death_orb, PlayMode.LOOP);
		voidOrb = new Animation<Sprite>(0.15f, Assets.void_orb, PlayMode.LOOP);
		
		stingerFly = new Animation<Sprite>(0.15f, Assets.enemy_stinger, PlayMode.LOOP);
		stingerAttack = new Animation<Sprite>(0.1f, Assets.enemy_stinger_charge, PlayMode.LOOP);
		
		
		pangolinBabyStand = new Animation<Sprite>(0.15f, Assets.enemy_pangolin_baby, PlayMode.LOOP);
		pangolinBabyRolled = new Animation<Sprite>(0.1f, Assets.enemy_pangolin_baby_rolled, PlayMode.LOOP);
		pangolinBabyRolling = new Animation<Sprite>(0.1f, Assets.enemy_pangolin_baby_rolling, PlayMode.LOOP);

		pangolinMotherStand = new Animation<Sprite>(0.15f, Assets.boss_pangolin_mother, PlayMode.LOOP);
		pangolinMotherEnragedStand = new Animation<Sprite>(0.15f, Assets.boss_pangolin_mother_enraged, PlayMode.LOOP);
		pangolinMotherCrying = new Animation<Sprite>(0.3f, Assets.boss_pangolin_mother_crying, PlayMode.LOOP);
		
		tribesmenSpearStand = new Animation<Sprite>(0.3f, Assets.enemy_tribesman_spear, PlayMode.LOOP);
		tribesmenShieldStand = new Animation<Sprite>(0.3f, Assets.enemy_tribesman_shield, PlayMode.LOOP);
		tribesmenScoutStand = new Animation<Sprite>(0.3f, Assets.enemy_tribesman_scout, PlayMode.LOOP);
		tribesmenTotem = new Animation<Sprite>(0.15f, Assets.enemy_tribesman_totem, PlayMode.LOOP_PINGPONG);

		tribesmenShamanStand = new Animation<Sprite>(0.3f, Assets.boss_shaman, PlayMode.LOOP);
		tribesmenShamanSummoning = new Animation<Sprite>(0.15f, Assets.boss_shaman_summoning, PlayMode.LOOP_PINGPONG);
	}
	
	
	private AnimationSingleton() {
		this.init();
	}

	public static void dispose() {
		instance = null;
	}
	
}
