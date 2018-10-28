package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;

public class EnemyComponent implements Component, Poolable {
	
	public enum EnemyMoveStrategy {
		STANDING_STILL,
		MOVE_TOWARD_PLAYER,
		MOVE_RANDOMLY;
	}
	
	/** The engine that managed entities.*/
	public PooledEngine engine;
	
	/** The movement pattern of this enemy. */
	private EnemyMoveStrategy moveStrategy;

	@Override
	public void reset() {
		engine = null;
	}
	
	
	
	
	// Getters and Setters

	public EnemyMoveStrategy getMoveStrategy() {
		return moveStrategy;
	}

	public void setMoveStrategy(EnemyMoveStrategy moveStrategy) {
		this.moveStrategy = moveStrategy;
	}
	

}
