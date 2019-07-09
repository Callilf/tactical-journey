/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.leveling.statsUp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.ces.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.leveling.LevelUpRewardEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * @author Callil
 *
 */
public class LevelUpStatsUpRewardBombThrowRangeUp extends AbstractLevelUpStatsUpReward {

	
	
	public LevelUpStatsUpRewardBombThrowRangeUp(RandomXS128 levelUpRandom) {
		super(1, 1, "Increase max range of bomb throw by 1", "Bomb's range increased by #", levelUpRandom);
		this.type = LevelUpRewardEnum.BOMB_THROW_RANGE_UP;
	}

	/* (non-Javadoc)
	 * @see com.dokkaebistudio.tacticaljourney.leveling.statsUp.AbstractLevelUpStatsUpReward#select(com.badlogic.ashley.core.Entity)
	 */
	@Override
	public void select(Entity player, Room room) {
		PlayerComponent playerComponent = Mappers.playerComponent.get(player);
		AttackComponent attackComponent = Mappers.attackComponent.get(playerComponent.getSkillBomb());
	
		if (attackComponent != null) {
			attackComponent.increaseRangeMax(getValue());
		}
	}
	

}
