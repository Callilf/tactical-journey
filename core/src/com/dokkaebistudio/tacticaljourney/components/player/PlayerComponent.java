package com.dokkaebistudio.tacticaljourney.components.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;

public class PlayerComponent implements Component {
	
	/** The engine that managed entities.*/
	public PooledEngine engine;
	
	/** The button to end the current turn. */
	private Entity endTurnButton;
	
	/** The skill currently active. Null if no skill is active. */
	private Entity activeSkill;
	/** The sprite that shows which skill is active. */
	private Entity activeSkillIndicator;
	
	/** The first skill. */
	private Entity skill1;
	/** The button to activate skill 1. */
	private Entity skill1Button;

	/** The second skill. */
	private Entity skill2;
	/** The button to activate skill 2. */
	private Entity skill2Button;
	
	
	
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

	public Entity getSkill1Button() {
		return skill1Button;
	}

	public void setSkill1Button(Entity skill1Button) {
		this.skill1Button = skill1Button;
	}

	public Entity getSkill2() {
		return skill2;
	}

	public void setSkill2(Entity skill2) {
		this.skill2 = skill2;
	}

	public Entity getSkill2Button() {
		return skill2Button;
	}

	public void setSkill2Button(Entity skill2Button) {
		this.skill2Button = skill2Button;
	}

	public Entity getActiveSkill() {
		return activeSkill;
	}

	public void setActiveSkill(Entity activeSkill) {
		this.activeSkill = activeSkill;
	}

	public Entity getActiveSkillIndicator() {
		return activeSkillIndicator;
	}

	public void setActiveSkillIndicator(Entity activeSkillIndicator) {
		this.activeSkillIndicator = activeSkillIndicator;
	}

	
}
