package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;

public class EnemyComponent extends SolidComponent implements Component, Poolable {
	
	/** The engine that managed entities.*/
	public PooledEngine engine;
	
	/** The health of the enemy. */
	public int health;

	@Override
	public void reset() {
		engine = null;
		health = 0;
	}
	
	

}
