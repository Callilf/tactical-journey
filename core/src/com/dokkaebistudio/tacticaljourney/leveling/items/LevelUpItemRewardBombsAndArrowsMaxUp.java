/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.leveling.items;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.leveling.LevelUpRewardEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * @author Callil
 *
 */
public class LevelUpItemRewardBombsAndArrowsMaxUp extends AbstractLevelUpItemReward {

	
	
	public LevelUpItemRewardBombsAndArrowsMaxUp(RandomXS128 levelUpRandom) {
		super(1, 1, "Increase max bomb and max arrow amount by 1", "Max bombs and max arrows amount increased by #", levelUpRandom);
		this.type = LevelUpRewardEnum.BOMBS_AND_ARROWS_MAX_UP;
	}

	/* (non-Javadoc)
	 * @see com.dokkaebistudio.tacticaljourney.leveling.statsUp.AbstractLevelUpStatsUpReward#select(com.badlogic.ashley.core.Entity)
	 */
	@Override
	public void select(Entity player, Room room) {
		AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(player);
		if (ammoCarrierComponent != null) {
			ammoCarrierComponent.increaseMaxBombsAndArrows(getValue());
		}
	}
	

}
