package com.dokkaebistudio.tacticaljourney.leveling;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public enum LevelUpRewardEnum {

	HEALTH_UP(10,30, "Increase max health \nby 10 to 30 hp") {
		@Override
		public void select(Entity player) {
			HealthComponent healthComponent = Mappers.healthComponent.get(player);
			
			int value = 10 + RandomSingleton.getInstance().getUnseededRandom().nextInt(20);
			healthComponent.increaseMaxHealth(value);
		}
	},
	
	
	STRENGTH_UP(1,2, "Increase strength \nby 1 or 2") {
		@Override
		public void select(Entity player) {
    		AttackComponent attackComponent = Mappers.attackComponent.get(player);
    		
			int value = 1 + RandomSingleton.getInstance().getUnseededRandom().nextInt(1);
    		attackComponent.increaseStrength(value);
		}
	},
	
	
	MOVEMENT_UP(1,1, "Increase movement by 1") {
		@Override
		public void select(Entity player) {
    		MoveComponent moveComponent = Mappers.moveComponent.get(player);
    		moveComponent.increaseMoveSpeed(1);			
		}
	},
	
	
	ARROW_MAX_UP(5,10, "Increase max arrow \namount by 5 to 10"){
		@Override
		public void select(Entity player) {
			// TODO Auto-generated method stub
			
		}
	},
	
	
	BOMB_MAX_UP(2,5, "Increase max bomb \namount by 2 to 5"){
		@Override
		public void select(Entity player) {
			// TODO Auto-generated method stub
			
		}
	},
	
	
	REFILL_HEALTH("Refill health completely"){
		@Override
		public void select(Entity player) {
			// TODO Auto-generated method stub
			
		}
	},
	
	
	REFILL_ARROWS("Refill arrows completely"){
		@Override
		public void select(Entity player) {
			// TODO Auto-generated method stub
			
		}
	},
	
	
	REFILL_BOMBS("Refill bombs completely."){
		@Override
		public void select(Entity player) {
			// TODO Auto-generated method stub
			
		}
	};
	
	// Attributes
	
	private Integer valueMin;
	private Integer valueMax;
	private String description;
	
	// Constructors
		
	private LevelUpRewardEnum(String desc) {
		this.setDescription(desc);
	}
	
	private LevelUpRewardEnum(Integer valueMin, Integer valueMax, String desc) {
		this.setValueMin(valueMin);
		this.setValueMax(valueMax);
		this.setDescription(desc);
	}
	
	// Abstract methods
	
	/** Called when this reward is selected. */
	public abstract void select(Entity player);
	
	
	
	
	// Getters and setters

	public Integer getValueMin() {
		return valueMin;
	}

	public void setValueMin(Integer valueMin) {
		this.valueMin = valueMin;
	}

	public Integer getValueMax() {
		return valueMax;
	}

	public void setValueMax(Integer valueMax) {
		this.valueMax = valueMax;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
