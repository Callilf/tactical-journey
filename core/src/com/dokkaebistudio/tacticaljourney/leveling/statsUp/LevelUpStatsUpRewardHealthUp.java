/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.leveling.statsUp;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.leveling.LevelUpRewardEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * @author Callil
 *
 */
public class LevelUpStatsUpRewardHealthUp extends AbstractLevelUpStatsUpReward {

	
	
	public LevelUpStatsUpRewardHealthUp() {
		super(10, 15, "Increase max health \nby 10 to 15 hp", "Max health increased \nby # hp");
		this.type = LevelUpRewardEnum.HEALTH_UP;
	}

	/* (non-Javadoc)
	 * @see com.dokkaebistudio.tacticaljourney.leveling.statsUp.AbstractLevelUpStatsUpReward#select(com.badlogic.ashley.core.Entity)
	 */
	@Override
	public void select(Entity player, Room room) {
		HealthComponent healthComponent = Mappers.healthComponent.get(player);
		healthComponent.increaseMaxHealth(getValue());
	}
	

}
