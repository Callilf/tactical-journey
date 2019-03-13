package com.dokkaebistudio.tacticaljourney.leveling;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.enums.AmmoTypeEnum;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public enum LevelUpItemsRewardEnum {
	
	ARROW_MAX_UP(4, 6, "Increase max arrow \namount by 4 to 6", "Max amount of arrows \nincreased by #"){
		@Override
		public void select(Entity player) {
			AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(player);
			if (ammoCarrierComponent != null) {
				ammoCarrierComponent.increaseMaxArrows(getValue());
			}
		}
	},
	
	BOMB_MAX_UP(2, 3, "Increase max bomb \namount by 2 to 3", "Max amount of bombs \nincreased by #"){
		@Override
		public void select(Entity player) {
			AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(player);
			if (ammoCarrierComponent != null) {
				ammoCarrierComponent.increaseMaxBombs(getValue());
			}
		}
	},
	
	BOMB_AND_ARROW_MAX_UP(1, 1, "Increase max bomb and \nmax arrow amount by 1", "Max bombs and max arrows \namount increased by #"){
		@Override
		public void select(Entity player) {
			AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(player);
			if (ammoCarrierComponent != null) {
				ammoCarrierComponent.increaseMaxBombsAndArrows(getValue());
			}
		}
	},
	
	
	RESTORE_HEALTH(30, 50, "Restore 30 to 50 hp", "# hp restored"){
		@Override
		public void select(Entity player) {
			HealthComponent healthComponent = Mappers.healthComponent.get(player);
			
			healthComponent.restoreHealth(getValue());
		}
	},
	
	
	RESTORE_ARROWS(10, 15, "Restore 10 to 15 arrows", "# arrows received"){
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
		
	private LevelUpItemsRewardEnum(String desc, String finalDesc) {
		this.setDescription(desc);
		this.setFinalDescription(finalDesc);
	}
	
	private LevelUpItemsRewardEnum(Integer valueMin, Integer valueMax, String desc, String finalDesc) {
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
