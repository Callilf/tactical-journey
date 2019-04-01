/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.leveling.statsUp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.leveling.LevelUpRewardEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * @author Callil
 *
 */
public class LevelUpStatsUpRewardMoveUp extends AbstractLevelUpStatsUpReward {

	
	
	public LevelUpStatsUpRewardMoveUp(RandomXS128 levelUpRandom) {
		super(1, 1, "Increase movement by 1", "Movement increased by #", levelUpRandom);
		this.type = LevelUpRewardEnum.MOVEMENT_UP;
	}

	/* (non-Javadoc)
	 * @see com.dokkaebistudio.tacticaljourney.leveling.statsUp.AbstractLevelUpStatsUpReward#select(com.badlogic.ashley.core.Entity)
	 */
	@Override
	public void select(Entity player, Room room) {
		MoveComponent moveComponent = Mappers.moveComponent.get(player);
		moveComponent.increaseMoveSpeed(1);			
	}
	

}
