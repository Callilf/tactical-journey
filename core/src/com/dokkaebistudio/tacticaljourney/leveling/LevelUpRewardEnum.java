package com.dokkaebistudio.tacticaljourney.leveling;

import java.util.ArrayList;
import java.util.List;

public enum LevelUpRewardEnum {

	// Stats up
	HEALTH_UP (LevelUpRewardTypeEnum.STATS_UP),
	STRENGTH_UP (LevelUpRewardTypeEnum.STATS_UP),
	MOVEMENT_UP (LevelUpRewardTypeEnum.STATS_UP),
	ARROW_RANGE_UP (LevelUpRewardTypeEnum.STATS_UP),
	BOMB_THROW_RANGE_UP (LevelUpRewardTypeEnum.STATS_UP),
	ITEM_THROW_RANGE_UP (LevelUpRewardTypeEnum.STATS_UP),
	
	
	// items
	ARROWS_MAX_UP (LevelUpRewardTypeEnum.ITEMS),
	ARROW_RECEIVE (LevelUpRewardTypeEnum.ITEMS),
	BOMBS_MAX_UP (LevelUpRewardTypeEnum.ITEMS),
	BOMBS_RECEIVE (LevelUpRewardTypeEnum.ITEMS),
	BOMBS_AND_ARROWS_MAX_UP (LevelUpRewardTypeEnum.ITEMS),
	INVENTORY_SLOT (LevelUpRewardTypeEnum.ITEMS),
	
	// Alterations
	RECEIVE_INFUSABLE (LevelUpRewardTypeEnum.ALTERATIONS);
//	CURE_CURSE (LevelUpRewardTypeEnum.ALTERATIONS);
	
	
	
	// Infusables
	
	
	
	private LevelUpRewardTypeEnum type;
	
	private LevelUpRewardEnum(LevelUpRewardTypeEnum type) {
		this.type = type;
	}
	
	
	
	public static List<LevelUpRewardEnum> getValuesForLevel(int level) {
		List<LevelUpRewardEnum> result = new ArrayList<>();
		
		LevelUpRewardTypeEnum t = null;
		int modulo = level % 3;
		if (modulo == 2) {
			t = LevelUpRewardTypeEnum.STATS_UP;
		} else if (modulo == 0) {
			t = LevelUpRewardTypeEnum.ITEMS;
		} else {
			t = LevelUpRewardTypeEnum.ALTERATIONS;
		}
		
		
		for (LevelUpRewardEnum val : LevelUpRewardEnum.values()) {
			if (val.type == t) {
				result.add(val);
			}
		}
		
		return result;
	}
	
	
	public enum LevelUpRewardTypeEnum {
		STATS_UP,
		ITEMS,
		ALTERATIONS;
	}
	
	
}

