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
	
	/** The throwing skill. */
	private Entity skillThrow;
	
	/** Whether the profile popin is opened or not. */
	private boolean profilePopinDisplayed;
	
	private boolean actionDoneAtThisFrame = false;
	

	
	// Action
	
	public enum PlayerActionEnum {
		NONE,
		LOOT,
		EXIT,
		PRAY,
		RESTOCK_SHOP;
	}
	
	/** Whether the popin to ask for loot should open or not. */
	private PlayerActionEnum requestedAction = PlayerActionEnum.NONE;
	private Entity actionEntity;

	public void requestAction(PlayerActionEnum action, Entity actionEntity) {
		this.requestedAction = action;
		this.actionEntity = actionEntity;
	}

	public void clearRequestedAction() {
		this.requestedAction = PlayerActionEnum.NONE;
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


	public Entity getSkillThrow() {
		return skillThrow;
	}

	public void setSkillThrow(Entity skillThrow) {
		this.skillThrow = skillThrow;
	}

	public PlayerActionEnum getRequestedAction() {
		return requestedAction;
	}

	public Entity getActionEntity() {
		return actionEntity;
	}

	public void setActionEntity(Entity actionEntity) {
		this.actionEntity = actionEntity;
	}

	public boolean isActionDoneAtThisFrame() {
		return actionDoneAtThisFrame;
	}

	public void setActionDoneAtThisFrame(boolean actionDoneAtThisFrame) {
		this.actionDoneAtThisFrame = actionDoneAtThisFrame;
	}
}
