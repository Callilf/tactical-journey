package com.dokkaebistudio.tacticaljourney.singletons;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.IntMap;
import com.dokkaebistudio.tacticaljourney.Assets;

public class AnimationSingleton {

	private static AnimationSingleton instance;
	
	public static AnimationSingleton getInstance() {
		if (instance == null) {
			instance = new AnimationSingleton();
		}
		return instance;
	}
	
	public IntMap<Animation<Sprite>> animationsMap = new IntMap<>();
	public int animationsCount;
	
	public Animation<Sprite> player_standing;
	public Animation<Sprite> player_running;
	public Animation<Sprite> player_flying;
	
	public Animation<Sprite> bomb_slow;
	public Animation<Sprite> bomb_fast;
	public Animation<Sprite> explosion;
	public Animation<Sprite> fire;
	public Animation<Sprite> portal;
	
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

	
	public Animation<Sprite> attack_slash;
	public Animation<Sprite> attack_slash_critical;
	public Animation<Sprite> arrow;
	public Animation<Sprite> bomb;
	public Animation<Sprite> web_projectile;
	public Animation<Sprite> pebble_projectile;
	

	private void init() {
		animationsCount = 0;
		
		player_standing = new Animation<Sprite>(0.2f, Assets.player_standing, PlayMode.LOOP);
		createAnimation(player_standing);
		player_running = new Animation<Sprite>(0.2f, Assets.player_running, PlayMode.LOOP);
		createAnimation(player_running);
		player_flying = new Animation<Sprite>(0.2f, Assets.player_flying, PlayMode.LOOP_PINGPONG);
		createAnimation(player_flying);


		bomb_slow = new Animation<Sprite>(0.2f, Assets.bomb_animation, PlayMode.LOOP);
		createAnimation(bomb_slow);
		bomb_fast = new Animation<Sprite>(0.1f, Assets.bomb_animation, PlayMode.LOOP);
		createAnimation(bomb_fast);
		explosion = new Animation<Sprite>(0.07f, Assets.explosion_animation, PlayMode.NORMAL);
		createAnimation(explosion);
		
		fire = new Animation<Sprite>(0.17f, Assets.creep_fire_animation, PlayMode.LOOP);
		createAnimation(fire);
		portal = new Animation<Sprite>(0.2f, Assets.portal_animation, PlayMode.LOOP);
		createAnimation(portal);
		
		energyOrb = new Animation<Sprite>(0.2f, Assets.energy_orb, PlayMode.LOOP);
		createAnimation(energyOrb);
		vegetalOrb = new Animation<Sprite>(0.2f, Assets.vegetal_orb, PlayMode.LOOP);
		createAnimation(vegetalOrb);
		poisonOrb = new Animation<Sprite>(0.2f, Assets.poison_orb, PlayMode.LOOP);
		createAnimation(poisonOrb);
		fireOrb = new Animation<Sprite>(0.2f, Assets.fire_orb, PlayMode.LOOP);
		createAnimation(fireOrb);
		deathOrb = new Animation<Sprite>(0.2f, Assets.death_orb, PlayMode.LOOP);
		createAnimation(deathOrb);
		voidOrb = new Animation<Sprite>(0.15f, Assets.void_orb, PlayMode.LOOP);
		createAnimation(voidOrb);
		
		stingerFly = new Animation<Sprite>(0.15f, Assets.enemy_stinger, PlayMode.LOOP);
		createAnimation(stingerFly);
		stingerAttack = new Animation<Sprite>(0.1f, Assets.enemy_stinger_charge, PlayMode.LOOP);
		createAnimation(stingerAttack);
		
		
		pangolinBabyStand = new Animation<Sprite>(0.15f, Assets.enemy_pangolin_baby, PlayMode.LOOP);
		createAnimation(pangolinBabyStand);
		pangolinBabyRolled = new Animation<Sprite>(0.1f, Assets.enemy_pangolin_baby_rolled, PlayMode.LOOP);
		createAnimation(pangolinBabyRolled);
		pangolinBabyRolling = new Animation<Sprite>(0.1f, Assets.enemy_pangolin_baby_rolling, PlayMode.LOOP);
		createAnimation(pangolinBabyRolling);

		pangolinMotherStand = new Animation<Sprite>(0.15f, Assets.boss_pangolin_mother, PlayMode.LOOP);
		createAnimation(pangolinMotherStand);
		pangolinMotherEnragedStand = new Animation<Sprite>(0.15f, Assets.boss_pangolin_mother_enraged, PlayMode.LOOP);
		createAnimation(pangolinMotherEnragedStand);
		pangolinMotherCrying = new Animation<Sprite>(0.3f, Assets.boss_pangolin_mother_crying, PlayMode.LOOP);
		createAnimation(pangolinMotherCrying);
		
		tribesmenSpearStand = new Animation<Sprite>(0.3f, Assets.enemy_tribesman_spear, PlayMode.LOOP);
		createAnimation(tribesmenSpearStand);
		tribesmenShieldStand = new Animation<Sprite>(0.3f, Assets.enemy_tribesman_shield, PlayMode.LOOP);
		createAnimation(tribesmenShieldStand);
		tribesmenScoutStand = new Animation<Sprite>(0.3f, Assets.enemy_tribesman_scout, PlayMode.LOOP);
		createAnimation(tribesmenScoutStand);
		tribesmenTotem = new Animation<Sprite>(0.15f, Assets.enemy_tribesman_totem, PlayMode.LOOP_PINGPONG);
		createAnimation(tribesmenTotem);

		tribesmenShamanStand = new Animation<Sprite>(0.3f, Assets.boss_shaman, PlayMode.LOOP);
		createAnimation(tribesmenShamanStand);
		tribesmenShamanSummoning = new Animation<Sprite>(0.15f, Assets.boss_shaman_summoning, PlayMode.LOOP_PINGPONG);
		createAnimation(tribesmenShamanSummoning);
		
		
		
		attack_slash = new Animation<>(0.03f, Assets.slash_animation);
		createAnimation(attack_slash);
		attack_slash_critical = new Animation<>(0.03f, Assets.slash_critical_animation);
		createAnimation(attack_slash_critical);
		arrow = new Animation<>(0.1f, Assets.arrow);
		createAnimation(arrow);
		bomb = new Animation<>(0.1f, Assets.bomb_animation);
		createAnimation(bomb);
		web_projectile = new Animation<>(0.1f, Assets.projectile_web);
		createAnimation(web_projectile);
		pebble_projectile = new Animation<>(0.1f, Assets.projectile_pebble);
		createAnimation(pebble_projectile);

	}


	private void createAnimation(Animation<Sprite> attribute) {
		animationsMap.put(animationsCount, attribute);
		animationsCount++;
	}
	
	public Animation<Sprite> getAnimation(int index) {
		if (index == -1) return null;
		return animationsMap.get(index);
	}
	
	public int getIndex(Animation<Sprite> anim) {
		if (anim == null) return - 1;
		return animationsMap.findKey(anim, true, -1);
	}
	
	
	
	private AnimationSingleton() {
		this.init();
	}

	public static void dispose() {
		instance = null;
	}
	
}
