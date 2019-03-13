package com.dokkaebistudio.tacticaljourney.leveling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.leveling.infusable.LevelUpAlterationRewardReceiveInfusable;
import com.dokkaebistudio.tacticaljourney.leveling.items.LevelUpItemRewardArrowsMaxUp;
import com.dokkaebistudio.tacticaljourney.leveling.items.LevelUpItemRewardArrowsReceive;
import com.dokkaebistudio.tacticaljourney.leveling.items.LevelUpItemRewardBombsAndArrowsMaxUp;
import com.dokkaebistudio.tacticaljourney.leveling.items.LevelUpItemRewardBombsMaxUp;
import com.dokkaebistudio.tacticaljourney.leveling.items.LevelUpItemRewardBombsReceive;
import com.dokkaebistudio.tacticaljourney.leveling.items.LevelUpItemRewardInventorySlot;
import com.dokkaebistudio.tacticaljourney.leveling.statsUp.LevelUpStatsUpRewardArrowRangeUp;
import com.dokkaebistudio.tacticaljourney.leveling.statsUp.LevelUpStatsUpRewardBombThrowRangeUp;
import com.dokkaebistudio.tacticaljourney.leveling.statsUp.LevelUpStatsUpRewardHealthUp;
import com.dokkaebistudio.tacticaljourney.leveling.statsUp.LevelUpStatsUpRewardItemThrowRangeUp;
import com.dokkaebistudio.tacticaljourney.leveling.statsUp.LevelUpStatsUpRewardMoveUp;
import com.dokkaebistudio.tacticaljourney.leveling.statsUp.LevelUpStatsUpRewardStrengthUp;
import com.dokkaebistudio.tacticaljourney.room.Room;

public abstract class LevelUpReward {

	// Attributes	
	public LevelUpRewardEnum type;
	private Integer value;
	private String description;
	protected String finalDescription;
	
	// Constructors
	public LevelUpReward(String desc, String finalDesc) {
		this.setDescription(desc);
		this.setFinalDescription(finalDesc);
	}

	// Abstract methods
	
	/** Called when this reward is selected. */
	public abstract void select(Entity player, Room room);
	
	/** Called for rewards with a randomized value. */
	public abstract void computeValue();
	
	
	
	
	// Getters and setters

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getFinalDescription() {
		computeValue();
		
		if (finalDescription != null && getValue() != null) {
			return finalDescription.replace("#", String.valueOf(getValue()));
		}
		return finalDescription;
	}

	public void setFinalDescription(String finalDescription) {
		this.finalDescription = finalDescription;
	}

	public LevelUpRewardEnum getType() {
		return type;
	}

	public void setType(LevelUpRewardEnum type) {
		this.type = type;
	}
	
	
	
	public static List<LevelUpReward> getRewards(int level, int numberOfChoices) {
		List<LevelUpReward> rewards = new ArrayList<>();
		
		List<LevelUpRewardEnum> enums = LevelUpRewardEnum.getValuesForLevel(level);
		Collections.shuffle(enums, RandomSingleton.getInstance().getUnseededRandom());
		
		for (int i=0 ; i<numberOfChoices ; i++) {
			if (enums.size() > i) {
				LevelUpRewardEnum type = enums.get(i);
				rewards.add(LevelUpReward.create(type));
			}
		}
		
		return rewards;
	}
	
	//********************
	// Factory
	
	public static LevelUpReward create(LevelUpRewardEnum type) {
		LevelUpReward reward = null;
		
		switch(type) {
		case HEALTH_UP:
			reward = new LevelUpStatsUpRewardHealthUp();
			break;
		case STRENGTH_UP:
			reward = new LevelUpStatsUpRewardStrengthUp();
			break;
		case MOVEMENT_UP:
			reward = new LevelUpStatsUpRewardMoveUp();
			break;
		case ARROW_RANGE_UP:
			reward = new LevelUpStatsUpRewardArrowRangeUp();
			break;
		case BOMB_THROW_RANGE_UP:
			reward = new LevelUpStatsUpRewardBombThrowRangeUp();
			break;
		case ITEM_THROW_RANGE_UP:
			reward = new LevelUpStatsUpRewardItemThrowRangeUp();
			break;
			
			
			
			
		case ARROWS_MAX_UP:
			reward = new LevelUpItemRewardArrowsMaxUp();
			break;
		case ARROW_RECEIVE:
			reward = new LevelUpItemRewardArrowsReceive();
			break;
		case BOMBS_MAX_UP:
			reward = new LevelUpItemRewardBombsMaxUp();
			break;
		case BOMBS_RECEIVE:
			reward = new LevelUpItemRewardBombsReceive();
			break;
		case BOMBS_AND_ARROWS_MAX_UP:
			reward = new LevelUpItemRewardBombsAndArrowsMaxUp();
			break;
		case INVENTORY_SLOT:
			reward = new LevelUpItemRewardInventorySlot();
			break;
			
			
		case RECEIVE_INFUSABLE:
			reward = new LevelUpAlterationRewardReceiveInfusable();
			break;
//		case CURE_CURSE:
//			reward = new LevelUpAlterationRewardReceiveInfusable();
//			break;
		
		default:
			
		}
		return reward;
	}
	
	
}
