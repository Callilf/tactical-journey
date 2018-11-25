package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Represent a skill of the player.
 * @author Callil
 *
 */
public class SkillComponent implements Component, Poolable {
		
	/** The number of this skill. */
	private int skillNumber;
	
	/** The skill name. */
	private String name;
	
	/** The entity possessing this skill. */
	private Entity parentEntity;



	
	@Override
	public void reset() {
		skillNumber = 0;
		if (parentEntity != null) {
			parentEntity = null;		
		}
		if (name != null) {
			name = null;
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



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}
	
}
