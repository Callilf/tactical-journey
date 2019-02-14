package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Marker to indicate that this entity is flammable.
 * @author Callil
 *
 */
public class FlammableComponent implements Component, Poolable {
	
	/** Whether the entity is currently burning. */
	private boolean burning;
	
	/** Whether this entity can ignite due to propagation. */
	private boolean propagate;
	
	/** Whether this entity is destroyed when ignited. */
	private boolean destroyed;
	
	/** The texture to use to make the item disappear. */
	private AtlasRegion destroyedTexture;


	@Override
	public void reset() {
		burning = false;
		propagate = false;	
		destroyed = false;
	}
	
	
	//************************
	// Getters and Setters
	
	public boolean isPropagate() {
		return propagate;
	}

	public void setPropagate(boolean propagate) {
		this.propagate = propagate;
	}


	public boolean isDestroyed() {
		return destroyed;
	}


	public void setDestroyed(boolean destroy) {
		this.destroyed = destroy;
	}


	public boolean isBurning() {
		return burning;
	}


	public void setBurning(boolean burning) {
		this.burning = burning;
	}


	public Image getDestroyedTexture(Vector2 pos) {
		if (destroyedTexture != null) {
			final Image img = new Image(this.destroyedTexture);
			img.setPosition( pos.x, pos.y);
			
			Action finishAction = new Action(){
				  @Override
				  public boolean act(float delta){
					  img.remove();
					  return true;
				  }
				};
			
			img.addAction(Actions.sequence(Actions.alpha(0, 1f), finishAction));
			
			return img;
		}
		
		return null;
	}


	public void setDestroyedTexture(AtlasRegion destroyedTexture) {
		this.destroyedTexture = destroyedTexture;
	}
	
	
	
}
