/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.statuses;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

/**
 * A status on an entity that provides a temporary buff or a debuff.
 * @author Callil
 *
 */
public abstract class Status {
	
	/** The number of turns this status will last. */
	private int duration;
	
	public abstract String title();
	public abstract AtlasRegion texture();

	/** Called when this status is received by an entity. */
	public void onReceive(Entity entity) {};
	
	/** Called when this status is removed from an entity. */
	public void onRemove(Entity entity) {};

	/** Called when the entity starts its turn. */
	public void onStartTurn(Entity entity) {};
	
	/** Called when the entity ends its turn. */
	public void onEndTurn(Entity entity) {}
	
	
	//************************
	// getters and setters
	
	public int getDuration() {
		return duration;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	};

	
}
