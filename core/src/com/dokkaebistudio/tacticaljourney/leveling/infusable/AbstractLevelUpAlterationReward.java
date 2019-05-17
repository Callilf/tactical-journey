package com.dokkaebistudio.tacticaljourney.leveling.infusable;

import com.dokkaebistudio.tacticaljourney.leveling.AbstractLevelUpReward;

public abstract class AbstractLevelUpAlterationReward extends AbstractLevelUpReward {

	
	protected String valueStr;
	
	// Constructors
	
	public AbstractLevelUpAlterationReward(String desc, String finalDesc) {
		super(desc, finalDesc);
	}
	
	
	public String getFinalDescription() {
		computeValue();
		
		if (finalDescription != null && getValue() != null) {
			return finalDescription.replace("#", valueStr);
		}
		return finalDescription;
	}
	
	
}
