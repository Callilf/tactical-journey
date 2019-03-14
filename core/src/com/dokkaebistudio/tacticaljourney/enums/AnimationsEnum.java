package com.dokkaebistudio.tacticaljourney.enums;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.dokkaebistudio.tacticaljourney.Assets;

public enum AnimationsEnum {

	PLAYER_STANDING(new Animation<Sprite>(0.2f, Assets.player_standing, PlayMode.LOOP)),
	PLAYER_RUNNING(new Animation<Sprite>(0.2f, Assets.player_running, PlayMode.LOOP)),
	PLAYER_FLYING(new Animation<Sprite>(0.2f, Assets.player_flying, PlayMode.LOOP_PINGPONG)),

	BOMB_SLOW(new Animation<Sprite>(0.2f, Assets.bomb_animation, PlayMode.LOOP)),
	BOMB_FAST(new Animation<Sprite>(0.1f, Assets.bomb_animation, PlayMode.LOOP)),
	EXPLOSION(new Animation<Sprite>(0.1f, Assets.explosion_animation, PlayMode.NORMAL)),
	
	FIRE(new Animation<Sprite>(0.17f, Assets.creep_fire_animation, PlayMode.LOOP)),
	
	
	ENERGY_ORB(new Animation<Sprite>(0.2f, Assets.energy_orb, PlayMode.LOOP)),
	VEGETAL_ORB(new Animation<Sprite>(0.2f, Assets.vegetal_orb, PlayMode.LOOP)),
	POISON_ORB(new Animation<Sprite>(0.2f, Assets.poison_orb, PlayMode.LOOP)),
	FIRE_ORB(new Animation<Sprite>(0.2f, Assets.fire_orb, PlayMode.LOOP)),
	
	STINGER_FLY(new Animation<Sprite>(0.15f, Assets.enemy_stinger, PlayMode.LOOP)),
	STINGER_ATTACK(new Animation<Sprite>(0.1f, Assets.enemy_stinger_charge, PlayMode.LOOP)),
	
	
	PANGOLIN_BABY_STAND(new Animation<Sprite>(0.15f, Assets.enemy_pangolin_baby, PlayMode.LOOP)),
	PANGOLIN_BABY_ROLLED(new Animation<Sprite>(0.1f, Assets.enemy_pangolin_baby_rolled, PlayMode.LOOP)),
	PANGOLIN_BABY_ROLLING(new Animation<Sprite>(0.1f, Assets.enemy_pangolin_baby_rolling, PlayMode.LOOP)),

	PANGOLIN_MOTHER_STAND(new Animation<Sprite>(0.15f, Assets.boss_pangolin_mother, PlayMode.LOOP)),
	PANGOLIN_MOTHER_ENRAGED_STAND(new Animation<Sprite>(0.15f, Assets.boss_pangolin_mother_enraged, PlayMode.LOOP)),
	PANGOLIN_MOTHER_CRYING(new Animation<Sprite>(0.3f, Assets.boss_pangolin_mother_crying, PlayMode.LOOP)),
	
	TRIBESMEN_SPEAR_STAND(new Animation<Sprite>(0.3f, Assets.enemy_tribesman_spear, PlayMode.LOOP)),
	TRIBESMEN_SHIELD_STAND(new Animation<Sprite>(0.3f, Assets.enemy_tribesman_shield, PlayMode.LOOP));


	
	private Animation<Sprite> animation;
	
	private AnimationsEnum(Animation<Sprite> a) {
		this.setAnimation(a);
	}

	public Animation<Sprite> getAnimation() {
		return animation;
	}

	public void setAnimation(Animation<Sprite> animation) {
		this.animation = animation;
	}
	
}
