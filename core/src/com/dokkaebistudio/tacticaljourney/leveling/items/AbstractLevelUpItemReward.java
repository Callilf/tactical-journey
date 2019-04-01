package com.dokkaebistudio.tacticaljourney.leveling.items;

import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.leveling.AbstractLevelUpReward;

public abstract class AbstractLevelUpItemReward extends AbstractLevelUpReward {

	
	// Attributes
	
	private Integer valueMin;
	private Integer valueMax;
	
	private RandomXS128 random;
	
	// Constructors
	
	public AbstractLevelUpItemReward(Integer valueMin, Integer valueMax, String desc, String finalDesc, RandomXS128 levelUpRandom) {
		super(desc, finalDesc);
		this.setValueMin(valueMin);
		this.setValueMax(valueMax);
		this.random = levelUpRandom;
	}
	
	
	
	
	// Abstract methods
	
	
	/** Called for rewards with a randomized value. */
	public void computeValue() {
		if (getValueMin() != null && getValueMax() != null) {
			if (getValueMin() == getValueMax()) {
				this.setValue( getValueMax());
			} else {
				this.setValue(getValueMin() + random.nextInt(getValueMax() - getValueMin() + 1));
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
