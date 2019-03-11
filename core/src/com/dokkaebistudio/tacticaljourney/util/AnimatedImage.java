package com.dokkaebistudio.tacticaljourney.util;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class AnimatedImage extends Image {

	protected Animation<Sprite> animation = null;
    private float stateTime = 0;
    private boolean loop;
    private Action finishAction;

    public AnimatedImage(Animation<Sprite> animation, boolean loop, Action finishAction) {
        super(animation.getKeyFrame(0));
        this.animation = animation;
        this.loop = loop;
        this.finishAction = finishAction;
    }

    @Override
    public void act(float delta) {
    	stateTime += delta;
    	if (loop) {
    		((TextureRegionDrawable)getDrawable()).setRegion(animation.getKeyFrame(stateTime, true));
        	super.act(delta);
    	} else {
    		if (animation.isAnimationFinished(stateTime)) {
    			this.remove();
    			if (finishAction != null) {
    				finishAction.act(delta);
    			}
    		} else {
	    		((TextureRegionDrawable)getDrawable()).setRegion(animation.getKeyFrame(stateTime, false));
	        	super.act(delta);
    		}
    	}
    }
}
