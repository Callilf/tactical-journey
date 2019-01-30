package com.dokkaebistudio.tacticaljourney.components.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Marker to indicate that this entity is a child entity of a parent one.
 * @author Callil
 *
 */
public class ParentEntityComponent implements Component, Poolable {
	
	/** The parent entity. */
	private Entity parent;
	
	

	@Override
	public void reset() {
		parent = null;
	}


	public Entity getParent() {
		return parent;
	}


	public void setParent(Entity parent) {
		this.parent = parent;
	}


	
	

}
