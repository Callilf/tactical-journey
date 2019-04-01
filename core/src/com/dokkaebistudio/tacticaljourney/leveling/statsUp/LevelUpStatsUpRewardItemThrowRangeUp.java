/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.leveling.statsUp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.leveling.LevelUpRewardEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * @author Callil
 *
 */
public class LevelUpStatsUpRewardItemThrowRangeUp extends AbstractLevelUpStatsUpReward {

	
	
	public LevelUpStatsUpRewardItemThrowRangeUp(RandomXS128 levelUpRandom) {
		super(1, 1, "Increase max range of item throw by 1", "Item throw range increased by #", levelUpRandom);
		this.type = LevelUpRewardEnum.ITEM_THROW_RANGE_UP;
	}

	/* (non-Javadoc)
	 * @see com.dokkaebistudio.tacticaljourney.leveling.statsUp.AbstractLevelUpStatsUpReward#select(com.badlogic.ashley.core.Entity)
	 */
	@Override
	public void select(Entity player, Room room) {
		PlayerComponent playerComponent = Mappers.playerComponent.get(player);
		AttackComponent attackComponent = Mappers.attackComponent.get(playerComponent.getSkillThrow());
	
		if (attackComponent != null) {
			attackComponent.increaseRangeMax(getValue());
		}
	}
	

}
