/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

/**
 * A state alteration which can be positive or negative.
 * @author Callil
 *
 */
public abstract class Alteration {
	
	public abstract String title();
	public abstract AtlasRegion texture();

	/** Called when this alteration is received by an entity. */
	public abstract void onReceive(Entity entity);
	
	/** Called when this alteration is removed from an entity. */
	public abstract void onRemove(Entity entity);
}
