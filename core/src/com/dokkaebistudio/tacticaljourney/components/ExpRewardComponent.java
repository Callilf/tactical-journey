package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;

public class ExpRewardComponent implements Component {


	/** The amount of xp to gain from this entity. */
	private int expGain;
	
	
	

	public int getExpGain() {
		return expGain;
	}

	public void setExpGain(int expGain) {
		this.expGain = expGain;
	}

	
	
	

}
