package com.dokkaebistudio.tacticaljourney.enums;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.dokkaebistudio.tacticaljourney.Assets;

public enum AnimationsEnum {

	BOMB_SLOW(new Animation<Sprite>(0.2f, Assets.getAnimation(Assets.bomb_animation), PlayMode.LOOP)),
	BOMB_FAST(new Animation<Sprite>(0.1f, Assets.getAnimation(Assets.bomb_animation), PlayMode.LOOP));

	
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
