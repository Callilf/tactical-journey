/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.leveling;

import com.dokkaebistudio.tacticaljourney.leveling.LevelUpRewardEnum.LevelUpRewardTypeEnum;

/**
 * 
 * @author Callil
 *
 */
public enum ExperienceLevelEnum {
	LEVEL_0(10, null),
	LEVEL_1(20, LevelUpRewardTypeEnum.STATS_UP),
	LEVEL_2(30, LevelUpRewardTypeEnum.ITEMS),
	LEVEL_3(40, LevelUpRewardTypeEnum.STATS_UP),
	LEVEL_4(50, LevelUpRewardTypeEnum.ITEMS),
	LEVEL_5(60, LevelUpRewardTypeEnum.ALTERATIONS),
	LEVEL_6(70, LevelUpRewardTypeEnum.STATS_UP),
	LEVEL_7(80, LevelUpRewardTypeEnum.ITEMS),
	LEVEL_8(90, LevelUpRewardTypeEnum.STATS_UP),
	LEVEL_9(100, LevelUpRewardTypeEnum.ITEMS),
	LEVEL_10(110, LevelUpRewardTypeEnum.ALTERATIONS),
	LEVEL_11(120, LevelUpRewardTypeEnum.STATS_UP),
	LEVEL_12(130, LevelUpRewardTypeEnum.ITEMS),
	LEVEL_13(140, LevelUpRewardTypeEnum.STATS_UP),
	LEVEL_14(150, LevelUpRewardTypeEnum.ITEMS),
	LEVEL_15(160, LevelUpRewardTypeEnum.ALTERATIONS),
	LEVEL_16(170, LevelUpRewardTypeEnum.STATS_UP),
	LEVEL_17(180, LevelUpRewardTypeEnum.ITEMS),
	LEVEL_18(190, LevelUpRewardTypeEnum.STATS_UP),
	LEVEL_19(200, LevelUpRewardTypeEnum.ITEMS),
	LEVEL_20(210, LevelUpRewardTypeEnum.ALTERATIONS);
	
	/** The amount of xp needed to reach the next level. */
	private int xpToNextLevel;
	
	/** The type of reward when leveling up. */
	private LevelUpRewardTypeEnum rewardType;
	
	ExperienceLevelEnum(int xpToNextLevel, LevelUpRewardTypeEnum rewardType) {
		this.xpToNextLevel = xpToNextLevel;
		this.rewardType = rewardType;
	}
	
	
	/**
	 * Return the {@link ExperienceLevelEnum} matching the given level.
	 * @param level the level
	 * @return the matching ExperienceLevelEnum
	 * @throws Exception 
	 */
	public static ExperienceLevelEnum get(int level) {
		if (level < 0) System.out.println("Calling for the ExperienceLevelEnum for the level = " + level);
		
		ExperienceLevelEnum[] allLevels = ExperienceLevelEnum.values();
		if (allLevels.length <= level) {
			return allLevels[allLevels.length - 1];
		} else {
			return ExperienceLevelEnum.valueOf("LEVEL_" + level);
		}
	}


	
	// Getters and setters
	
	public int getXpToNextLevel() {
		return xpToNextLevel;
	}


	public void setXpToNextLevel(int xpToNextLevel) {
		this.xpToNextLevel = xpToNextLevel;
	}


	public LevelUpRewardTypeEnum getRewardType() {
		return rewardType;
	}


	public void setRewardType(LevelUpRewardTypeEnum rewardType) {
		this.rewardType = rewardType;
	}
	
	
}
