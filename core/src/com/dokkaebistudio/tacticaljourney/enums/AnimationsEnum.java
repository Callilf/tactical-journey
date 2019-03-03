package com.dokkaebistudio.tacticaljourney.enums;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.dokkaebistudio.tacticaljourney.Assets;

public enum AnimationsEnum {

	PLAYER_STANDING(new Animation<Sprite>(0.2f, Assets.player_standing, PlayMode.LOOP)),
	PLAYER_FLYING(new Animation<Sprite>(0.2f, Assets.player_flying, PlayMode.LOOP)),

	BOMB_SLOW(new Animation<Sprite>(0.2f, Assets.bomb_animation, PlayMode.LOOP)),
	BOMB_FAST(new Animation<Sprite>(0.1f, Assets.bomb_animation, PlayMode.LOOP)),
	EXPLOSION(new Animation<Sprite>(0.1f, Assets.explosion_animation, PlayMode.NORMAL)),
	
	FIRE(new Animation<Sprite>(0.17f, Assets.creep_fire_animation, PlayMode.LOOP)),
	
	STINGER_FLY(new Animation<Sprite>(0.15f, Assets.enemy_stinger, PlayMode.LOOP)),
	STINGER_ATTACK(new Animation<Sprite>(0.1f, Assets.enemy_stinger_charge, PlayMode.LOOP)),
	
	
	PANGOLIN_BABY_STAND(new Animation<Sprite>(0.15f, Assets.enemy_pangolin_baby, PlayMode.LOOP)),
	PANGOLIN_BABY_ROLLED(new Animation<Sprite>(0.1f, Assets.enemy_pangolin_baby_rolled, PlayMode.LOOP)),
	PANGOLIN_BABY_ROLLING(new Animation<Sprite>(0.1f, Assets.enemy_pangolin_baby_rolling, PlayMode.LOOP));



	
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
