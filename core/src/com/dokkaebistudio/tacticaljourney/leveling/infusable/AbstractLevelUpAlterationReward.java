package com.dokkaebistudio.tacticaljourney.leveling.infusable;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.leveling.LevelUpReward;
import com.dokkaebistudio.tacticaljourney.room.Room;

public abstract class AbstractLevelUpAlterationReward extends LevelUpReward {

	
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
