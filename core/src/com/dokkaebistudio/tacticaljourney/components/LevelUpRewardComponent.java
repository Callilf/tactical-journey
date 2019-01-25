package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Marker to indicate that this entity is solid so the tile on which it stands is blocked.
 * @author Callil
 *
 */
public class LevelUpRewardComponent implements Component, Poolable {
	
	public PooledEngine engine;
	private Entity text;

	
	@Override
	public void reset() {
		if (text != null) {
			engine.removeEntity(text);
			text = null;
		}
	}
	
	
	
	public Entity getText() {
		return text;
	}

	public void setText(Entity text) {
		this.text = text;
	}

}
