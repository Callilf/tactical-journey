package com.dokkaebistudio.tacticaljourney.components.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.skills.SkillEnum;

/**
 * Represent a skill of the player.
 * @author Callil
 *
 */
public class SkillComponent implements Component, Poolable {
		
	/** The number of this skill. */
	private int skillNumber;
	
	/** The type of skill. */
	private SkillEnum type;
	
	
	/** The entity possessing this skill. */
	private Entity parentEntity;



	
	@Override
	public void reset() {
		skillNumber = 0;
		if (parentEntity != null) {
			parentEntity = null;		
		}
	}

	
	
	public Entity getParentEntity() {
		return parentEntity;
	}

	public void setParentEntity(Entity parentEntity) {
		this.parentEntity = parentEntity;
	}

	public int getSkillNumber() {
		return skillNumber;
	}

	public void setSkillNumber(int skillNumber) {
		this.skillNumber = skillNumber;
	}

	public SkillEnum getType() {
		return type;
	}

	public void setType(SkillEnum type) {
		this.type = type;
	}
	
}
