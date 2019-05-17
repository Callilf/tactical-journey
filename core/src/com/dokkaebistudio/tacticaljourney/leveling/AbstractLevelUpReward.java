package com.dokkaebistudio.tacticaljourney.leveling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.leveling.infusable.LevelUpAlterationRewardPurityPotion;
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

public abstract class AbstractLevelUpReward {

	// Attributes	
	public LevelUpRewardEnum type;
	private Integer value;
	private String description;
	protected String finalDescription;
	
	// Constructors
	public AbstractLevelUpReward(String desc, String finalDesc) {
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
	
	
	
	public static List<AbstractLevelUpReward> getRewards(int level, int numberOfChoices, RandomXS128 levelUpSeededRandom) {
		List<AbstractLevelUpReward> rewards = new ArrayList<>();
		
		List<LevelUpRewardEnum> enums = LevelUpRewardEnum.getValuesForLevel(level);
		Collections.shuffle(enums, levelUpSeededRandom);
		
		for (int i=0 ; i<numberOfChoices ; i++) {
			LevelUpRewardEnum type = null;
			if (enums.size() > i) {
				type = enums.get(i);
			} else {
				type = enums.get(levelUpSeededRandom.nextInt(enums.size()));
			}

			rewards.add(AbstractLevelUpReward.create(type, levelUpSeededRandom));
		}
		
		return rewards;
	}
	
	//********************
	// Factory
	
	public static AbstractLevelUpReward create(LevelUpRewardEnum type, RandomXS128 levelUpSeededRandom) {
		AbstractLevelUpReward reward = null;
		
		switch(type) {
		case HEALTH_UP:
			reward = new LevelUpStatsUpRewardHealthUp(levelUpSeededRandom);
			break;
		case STRENGTH_UP:
			reward = new LevelUpStatsUpRewardStrengthUp(levelUpSeededRandom);
			break;
		case MOVEMENT_UP:
			reward = new LevelUpStatsUpRewardMoveUp(levelUpSeededRandom);
			break;
		case ARROW_RANGE_UP:
			reward = new LevelUpStatsUpRewardArrowRangeUp(levelUpSeededRandom);
			break;
		case BOMB_THROW_RANGE_UP:
			reward = new LevelUpStatsUpRewardBombThrowRangeUp(levelUpSeededRandom);
			break;
		case ITEM_THROW_RANGE_UP:
			reward = new LevelUpStatsUpRewardItemThrowRangeUp(levelUpSeededRandom);
			break;
			
			
			
			
		case ARROWS_MAX_UP:
			reward = new LevelUpItemRewardArrowsMaxUp(levelUpSeededRandom);
			break;
		case ARROW_RECEIVE:
			reward = new LevelUpItemRewardArrowsReceive(levelUpSeededRandom);
			break;
		case BOMBS_MAX_UP:
			reward = new LevelUpItemRewardBombsMaxUp(levelUpSeededRandom);
			break;
		case BOMBS_RECEIVE:
			reward = new LevelUpItemRewardBombsReceive(levelUpSeededRandom);
			break;
		case BOMBS_AND_ARROWS_MAX_UP:
			reward = new LevelUpItemRewardBombsAndArrowsMaxUp(levelUpSeededRandom);
			break;
		case INVENTORY_SLOT:
			reward = new LevelUpItemRewardInventorySlot(levelUpSeededRandom);
			break;
			
			
		case RECEIVE_INFUSABLE:
			reward = new LevelUpAlterationRewardReceiveInfusable(levelUpSeededRandom);
			break;
		case RECEIVE_PURITY_POTION:
			reward = new LevelUpAlterationRewardPurityPotion(levelUpSeededRandom);
			break;
		
		default:
			
		}
		return reward;
	}
	
	
}
