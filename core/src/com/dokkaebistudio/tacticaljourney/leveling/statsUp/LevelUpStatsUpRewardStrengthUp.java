/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.leveling.statsUp;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.leveling.LevelUpRewardEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * @author Callil
 *
 */
public class LevelUpStatsUpRewardStrengthUp extends AbstractLevelUpStatsUpReward {

	
	
	public LevelUpStatsUpRewardStrengthUp() {
		super(1, 1, "Increase strength \nby 1", "Strength increased \nby #");
		this.type = LevelUpRewardEnum.STRENGTH_UP;
	}

	/* (non-Javadoc)
	 * @see com.dokkaebistudio.tacticaljourney.leveling.statsUp.AbstractLevelUpStatsUpReward#select(com.badlogic.ashley.core.Entity)
	 */
	@Override
	public void select(Entity player, Room room) {
		AttackComponent attackComponent = Mappers.attackComponent.get(player);
		attackComponent.increaseStrength(getValue());
	}
	

}
