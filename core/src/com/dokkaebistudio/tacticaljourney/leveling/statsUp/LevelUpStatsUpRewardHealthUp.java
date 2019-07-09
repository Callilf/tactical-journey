/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.leveling.statsUp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.ces.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.leveling.LevelUpRewardEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * @author Callil
 *
 */
public class LevelUpStatsUpRewardHealthUp extends AbstractLevelUpStatsUpReward {

	
	
	public LevelUpStatsUpRewardHealthUp(RandomXS128 levelUpRandom) {
		super(10, 15, "Increase max health by 10 to 15 hp", "Max health increased by # hp", levelUpRandom);
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
