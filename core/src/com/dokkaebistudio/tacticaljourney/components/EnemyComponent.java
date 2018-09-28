package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;

public class EnemyComponent implements Component {
	
	/** The engine that managed entities.*/
	public PooledEngine engine;
	
	/** The health of the enemy. */
	public int health;

}
