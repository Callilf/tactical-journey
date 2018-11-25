package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;

public class PlayerComponent implements Component {
	
	/** The engine that managed entities.*/
	public PooledEngine engine;
	
	/** The number of tiles the player can move. */
	public int health;
	
	/** The button to end the current turn. */
	private Entity endTurnButton;
	
	/** The first skill. */
	private Entity skill1;

	
	
	
	//**************************
	// Getter & Setters 
	
	public Entity getEndTurnButton() {
		return endTurnButton;
	}

	public void setEndTurnButton(Entity endTurnButton) {
		this.endTurnButton = endTurnButton;
	}

	public Entity getSkill1() {
		return skill1;
	}

	public void setSkill1(Entity skill1) {
		this.skill1 = skill1;
	}

}
