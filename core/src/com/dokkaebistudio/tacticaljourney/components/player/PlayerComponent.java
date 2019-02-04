package com.dokkaebistudio.tacticaljourney.components.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class PlayerComponent implements Component {
	
	/** The skill currently active. Null if no skill is active. */
	private Entity activeSkill;
	
	/** The melee skill. */
	private Entity skillMelee;

	/** The range skill. */
	private Entity skillRange;
	
	/** The bomb skill. */
	private Entity skillBomb;
	
	/** Whether the profile popin is opened or not. */
	private boolean profilePopinDisplayed;
	
	/** Whether the popin to ask for loot should open or not. */
	private boolean lootRequested;
	private Entity lootableEntity;
	
	
	public void clearLootRequested() {
		this.lootableEntity = null;
		this.lootRequested = false;
	}
	
	
	
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

	public Entity getSkillBomb() {
		return skillBomb;
	}

	public void setSkillBomb(Entity skillBomb) {
		this.skillBomb = skillBomb;
	}

	public boolean isProfilePopinDisplayed() {
		return profilePopinDisplayed;
	}

	public void setProfilePopinDisplayed(boolean profilePopinDisplayed) {
		this.profilePopinDisplayed = profilePopinDisplayed;
	}

	public boolean isLootRequested() {
		return lootRequested;
	}

	public void setLootRequested(boolean lootRequested) {
		this.lootRequested = lootRequested;
	}

	public Entity getLootableEntity() {
		return lootableEntity;
	}

	public void setLootableEntity(Entity lootableEntity) {
		this.lootableEntity = lootableEntity;
	}
	
}
