package com.dokkaebistudio.tacticaljourney.util;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class AnimatedImage extends Image {

	protected Animation<Sprite> animation = null;
    private float stateTime = 0;
    private boolean loop;
    private boolean actionPlaying;
    private Action finishAction;

    
    public AnimatedImage(Animation<Sprite> animation, boolean loop) {
        super(animation.getKeyFrame(0));
        this.animation = animation;
        this.loop = loop;
    }
    
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
    			if (actionPlaying) {
    				super.act(delta);
    				return;
    			}
    			
    			if (finishAction != null) {
    				Action removeImageAction = new Action(){
					  @Override
					  public boolean act(float delta){
					    remove();
					    return true;
					  }
					};
    				this.addAction(Actions.sequence(finishAction, removeImageAction));
    				actionPlaying = true;
    			} else {
    				this.remove();
    			}
    		} else {
	    		((TextureRegionDrawable)getDrawable()).setRegion(animation.getKeyFrame(stateTime, false));
	        	super.act(delta);
    		}
    	}
    }
    
    public void setFinishAction(Action finishAction) {
		this.finishAction = finishAction;
	}
    
    
    
    
	public static Serializer<AnimatedImage> getSerializer(final PooledEngine engine) {
		return new Serializer<AnimatedImage>() {

			@Override
			public void write(Kryo kryo, Output output, AnimatedImage object) {
				
				output.writeInt(AnimationSingleton.getInstance().getIndex(object.animation));
				output.writeBoolean(object.loop);
				
				output.writeFloat(object.getX());
				output.writeFloat(object.getY());

			}

			@Override
			public AnimatedImage read(Kryo kryo, Input input, Class<AnimatedImage> type) {
				int animationIndex = input.readInt();
				boolean loop = input.readBoolean();
				AnimatedImage animatedImage = new AnimatedImage(AnimationSingleton.getInstance().getAnimation(animationIndex), loop);
				
				float x = input.readFloat();
				float y = input.readFloat();
				
				animatedImage.setPosition(x, y);
				return animatedImage;
			}
		
		};
	}
}
