package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;

public class PlayerComponent extends SolidComponent implements Component {
	
	/** The engine that managed entities.*/
	public PooledEngine engine;
	
	/** The number of tiles the player can move. */
	public int health;

}
