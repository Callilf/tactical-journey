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

	/**
	 * The sprite of the entity destroyed.
	 */
	private AtlasRegion destroyedTexture;
	
	
	@Override
	public void reset() {
		this.setDestroyedTexture(null);
	}


	

	// Getters and setters
	

	public AtlasRegion getDestroyedTexture() {
		return destroyedTexture;
	}


	public void setDestroyedTexture(AtlasRegion destroyedTexture) {
		this.destroyedTexture = destroyedTexture;
	}
	
}
