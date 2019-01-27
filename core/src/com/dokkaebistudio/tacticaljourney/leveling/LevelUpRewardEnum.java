package com.dokkaebistudio.tacticaljourney.leveling;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.enums.AmmoTypeEnum;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public enum LevelUpRewardEnum {

	HEALTH_UP(10, 30, "Increase max health \nby 10 to 30 hp", "Max health increased \nby # hp") {
		@Override
		public void select(Entity player) {
			HealthComponent healthComponent = Mappers.healthComponent.get(player);
			
			healthComponent.increaseMaxHealth(getValue());
		}
	},
	
	
	STRENGTH_UP(1, 2, "Increase strength \nby 1 or 2", "Strength increased \nby #") {
		@Override
		public void select(Entity player) {
    		AttackComponent attackComponent = Mappers.attackComponent.get(player);
    		
    		attackComponent.increaseStrength(getValue());
		}
	},
	
	
	MOVEMENT_UP(1, 1, "Increase movement by 1", "Movement increased by #") {
		@Override
		public void select(Entity player) {
    		MoveComponent moveComponent = Mappers.moveComponent.get(player);
    		moveComponent.increaseMoveSpeed(1);			
		}
	},
	
	ARROW_RANGE_UP(1, 1, "Increase max range of\nrange weapon by 1", "Range weapon's range \nincreased by #"){
		@Override
		public void select(Entity player) {
			PlayerComponent playerComponent = Mappers.playerComponent.get(player);
			AttackComponent attackComponent = Mappers.attackComponent.get(playerComponent.getSkillRange());
		
			if (attackComponent != null) {
				attackComponent.increaseRangeMax(getValue());
			}
		}
	},
	
	ARROW_MAX_UP(5, 10, "Increase max arrow \namount by 5 to 10", "Max amount of arrows \nincreased by #"){
		@Override
		public void select(Entity player) {
			AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(player);
			if (ammoCarrierComponent != null) {
				ammoCarrierComponent.increaseMaxArrows(getValue());
			}
		}
	},
	
	BOMB_RANGE_UP(1, 1, "Increase max range of\nbomb throw by 1", "Bomb's range \nincreased by #"){
		@Override
		public void select(Entity player) {
			PlayerComponent playerComponent = Mappers.playerComponent.get(player);
			AttackComponent attackComponent = Mappers.attackComponent.get(playerComponent.getSkillBomb());
		
			if (attackComponent != null) {
				attackComponent.increaseRangeMax(getValue());
			}
		}
	},
	
	
	BOMB_MAX_UP(2, 5, "Increase max bomb \namount by 2 to 5", "Max amount of bombs \nincreased by #"){
		@Override
		public void select(Entity player) {
			AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(player);
			if (ammoCarrierComponent != null) {
				ammoCarrierComponent.increaseMaxBombs(getValue());
			}
		}
	},
	
	BOMB_AND_ARROW_MAX_UP(1, 3, "Increase max bomb and \nmax arrow amount by 1 to 3", "Max bombs and max arrows \namount increased by #"){
		@Override
		public void select(Entity player) {
			AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(player);
			if (ammoCarrierComponent != null) {
				ammoCarrierComponent.increaseMaxBombsAndArrows(getValue());
			}
		}
	},
	
	
	RESTORE_HEALTH(50, 100, "Restore 50 to 100 hp", "# hp restored"){
		@Override
		public void select(Entity player) {
			HealthComponent healthComponent = Mappers.healthComponent.get(player);
			
			healthComponent.restoreHealth(getValue());
		}
	},
	
	
	RESTORE_ARROWS(10, 20, "Restore 10 to 20 arrows", "# arrows received"){
		@Override
		public void select(Entity player) {
			AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(player);
			if (ammoCarrierComponent != null) {
				ammoCarrierComponent.pickUpAmmo(AmmoTypeEnum.ARROWS, getValue());
			}
		}
	},
	
	
	RESTORE_BOMBS(5, 10, "Restore 5 to 10 bombs.", "# bombs received"){
		@Override
		public void select(Entity player) {
			AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(player);
			if (ammoCarrierComponent != null) {
				ammoCarrierComponent.pickUpAmmo(AmmoTypeEnum.BOMBS, getValue());
			}
		}
	};
	
	// Attributes
	
	private Integer valueMin;
	private Integer valueMax;
	
	private Integer value;
	private String description;
	private String finalDescription;
	
	// Constructors
		
	private LevelUpRewardEnum(String desc, String finalDesc) {
		this.setDescription(desc);
		this.setFinalDescription(finalDesc);
	}
	
	private LevelUpRewardEnum(Integer valueMin, Integer valueMax, String desc, String finalDesc) {
		this.setValueMin(valueMin);
		this.setValueMax(valueMax);
		this.setDescription(desc);
		this.setFinalDescription(finalDesc);
	}
	
	
	
	
	// Abstract methods
	
	/** Called when this reward is selected. */
	public abstract void select(Entity player);
	
	/** Called for rewards with a randomized value. */
	public void computeValue() {
		if (getValueMin() != null && getValueMax() != null) {
			if (getValueMin() == getValueMax()) {
				this.setValue( getValueMax());
			} else {
				this.setValue(getValueMin() + RandomSingleton.getInstance().getUnseededRandom().nextInt(getValueMax() - getValueMin() + 1));
			}
		}
	};
	
	
	
	
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

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getFinalDescription() {
		computeValue();
		
		if (finalDescription != null && getValue() != null) {
			return finalDescription.replace("#", String.valueOf(getValue()));
		}
		return finalDescription;
	}

	public void setFinalDescription(String finalDescription) {
		this.finalDescription = finalDescription;
	}
	
	
}
