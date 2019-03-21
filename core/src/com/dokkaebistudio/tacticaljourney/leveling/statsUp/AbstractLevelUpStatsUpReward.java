package com.dokkaebistudio.tacticaljourney.leveling.statsUp;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.leveling.AbstractLevelUpReward;

public abstract class AbstractLevelUpStatsUpReward extends AbstractLevelUpReward {

	
	// Attributes
	
	private Integer valueMin;
	private Integer valueMax;
	
	// Constructors
	
	public AbstractLevelUpStatsUpReward(Integer valueMin, Integer valueMax, String desc, String finalDesc) {
		super(desc, finalDesc);
		this.setValueMin(valueMin);
		this.setValueMax(valueMax);
	}
	
	
	
	
	// Abstract methods
	
	
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
	
}
