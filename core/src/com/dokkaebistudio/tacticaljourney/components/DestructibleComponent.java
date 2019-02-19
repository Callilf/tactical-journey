package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Marker to indicate that this entity can be destroyed in a blast.
 * @author Callil
 *
 */
public class DestructibleComponent implements Component, Poolable {
	
	/** Whether the entity is already destroyed. */
	private boolean destroyed;

	/**
	 * The sprite of the entity destroyed.
	 */
	private AtlasRegion destroyedTexture;
	
	/** Whether the destroyed entity must be removed. */
	private boolean remove = true;;
	
	
	@Override
	public void reset() {
		this.setDestroyedTexture(null);
		this.setRemove(true);
		this.setDestroyed(false);
	}


	

	// Getters and setters
	

	public AtlasRegion getDestroyedTexture() {
		return destroyedTexture;
	}


	public void setDestroyedTexture(AtlasRegion destroyedTexture) {
		this.destroyedTexture = destroyedTexture;
	}




	public boolean isRemove() {
		return remove;
	}




	public void setRemove(boolean remove) {
		this.remove = remove;
	}




	public boolean isDestroyed() {
		return destroyed;
	}




	public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}
	
}
