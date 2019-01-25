package com.dokkaebistudio.tacticaljourney.components.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;

public class PlayerComponent implements Component {
	
	/** The engine that managed entities.*/
	public PooledEngine engine;
	
	/** The skill currently active. Null if no skill is active. */
	private Entity activeSkill;
	
	/** The first skill. */
	private Entity skillMelee;

	/** The second skill. */
	private Entity skillRange;
	
	
	
	//**************************
	// Getter & Setters 

	public Entity getSkillMelee() {
		return skillMelee;
	}

	public void setSkillMelee(Entity skill1) {
		this.skillMelee = skill1;
	}

	
	public Entity getSkillRange() {
		return skillRange;
	}

	public void setSkillRange(Entity skill2) {
		this.skillRange = skill2;
	}

	
	public Entity getActiveSkill() {
		return activeSkill;
	}

	public void setActiveSkill(Entity activeSkill) {
		this.activeSkill = activeSkill;
	}
	
}
