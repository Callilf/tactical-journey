/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.leveling.items;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.ces.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.enums.AmmoTypeEnum;
import com.dokkaebistudio.tacticaljourney.leveling.LevelUpRewardEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * @author Callil
 *
 */
public class LevelUpItemRewardBombsReceive extends AbstractLevelUpItemReward {

	
	
	public LevelUpItemRewardBombsReceive(RandomXS128 levelUpRandom) {
		super(5, 10, "Restore 5 to 10 bombs.", "# bombs received", levelUpRandom);
		this.type = LevelUpRewardEnum.BOMBS_RECEIVE;
	}

	/* (non-Javadoc)
	 * @see com.dokkaebistudio.tacticaljourney.leveling.statsUp.AbstractLevelUpStatsUpReward#select(com.badlogic.ashley.core.Entity)
	 */
	@Override
	public void select(Entity player, Room room) {
		AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(player);
		if (ammoCarrierComponent != null) {
			ammoCarrierComponent.pickUpAmmo(AmmoTypeEnum.BOMBS, getValue());
		}	
	}
	

}
