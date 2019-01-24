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
	private Entity skillMelee;
	/** The button to activate skill 1. */
	private Entity skillMeleeButton;

	/** The second skill. */
	private Entity skillRange;
	/** The button to activate skill 2. */
	private Entity skillRangeButton;
	
	
	
	//**************************
	// Getter & Setters 
	
	public Entity getEndTurnButton() {
		return endTurnButton;
	}

	public void setEndTurnButton(Entity endTurnButton) {
		this.endTurnButton = endTurnButton;
	}

	public Entity getSkillMelee() {
		return skillMelee;
	}

	public void setSkillMelee(Entity skill1) {
		this.skillMelee = skill1;
	}

	public Entity getSkillMeleeButton() {
		return skillMeleeButton;
	}

	public void setSkillMeleeButton(Entity skill1Button) {
		this.skillMeleeButton = skill1Button;
	}

	public Entity getSkillRange() {
		return skillRange;
	}

	public void setSkillRange(Entity skill2) {
		this.skillRange = skill2;
	}

	public Entity getSkillRangeButton() {
		return skillRangeButton;
	}

	public void setSkillRangeButton(Entity skill2Button) {
		this.skillRangeButton = skill2Button;
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
