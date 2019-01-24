package com.dokkaebistudio.tacticaljourney.components.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.leveling.ExperienceLevelEnum;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Marker to indicate that this entity is solid so the tile on which it stands is blocked.
 * @author Callil
 *
 */
public class ExperienceComponent implements Component,Poolable {
	
	public PooledEngine engine;
	
	/** The current level of the entity. */
	private int level;
	/** The current amount of xp. */
	private int currentXp;
	/** The amount of xp to reach next level. */
	private int nextLevelXp;
	
	/** Whether the entity just leveled up. */
	private boolean leveledUp;
	
	/** The number of level up reward choices. */
	private int choicesNumber = 6;
	
	
	//Displayers
	private Entity levelDisplayer;
	private Entity experienceDisplayer;

	public void init(PooledEngine engine) {
		this.engine = engine;
		reset();
	}
	

	@Override
	public void reset() {
		leveledUp = false;
		level = 1;
		currentXp = 0;
		nextLevelXp = 10;
		if (levelDisplayer != null) {
			engine.removeEntity(levelDisplayer);
			levelDisplayer = null;
		}
		if (experienceDisplayer != null) {
			engine.removeEntity(experienceDisplayer);
			experienceDisplayer = null;
		}
	}

	
	/**
	 * Earn the given amount of experience.
	 * @param amountToEarn the amount to earn.
	 */
	public void earnXp(int amountToEarn) {
		if (nextLevelXp > currentXp + amountToEarn) {
			currentXp += amountToEarn;
		} else {
			this.levelUp(currentXp + amountToEarn - nextLevelXp);
		}
		
		TextComponent levelText = Mappers.textComponent.get(levelDisplayer);
		levelText.setText("Level " + level);
		
		TextComponent expText = Mappers.textComponent.get(experienceDisplayer);
		expText.setText("Exp " + currentXp + "/" + nextLevelXp);
	}
	
	
	/**
	 * Level up.
	 * @param startingXp the new amount of current XP.
	 */
	private void levelUp(int startingXp) {
		level += 1;
		currentXp = startingXp;
		leveledUp = true;

		ExperienceLevelEnum experienceLevelEnum = ExperienceLevelEnum.get(level);
		nextLevelXp = experienceLevelEnum.getXpToNextLevel();
	}
	
	
	//*********************************
	// Getters and Setters
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getCurrentXp() {
		return currentXp;
	}
	public void setCurrentXp(int currentXp) {
		this.currentXp = currentXp;
	}
	public int getNextLevelXp() {
		return nextLevelXp;
	}
	public void setNextLevelXp(int nextLevelXp) {
		this.nextLevelXp = nextLevelXp;
	}

	public boolean hasLeveledUp() {
		return leveledUp;
	}

	public void setLeveledUp(boolean leveledUp) {
		this.leveledUp = leveledUp;
	}


	public Entity getLevelDisplayer() {
		return levelDisplayer;
	}


	public void setLevelDisplayer(Entity levelDisplayer) {
		this.levelDisplayer = levelDisplayer;
	}


	public Entity getExperienceDisplayer() {
		return experienceDisplayer;
	}


	public void setExperienceDisplayer(Entity experienceDisplayer) {
		this.experienceDisplayer = experienceDisplayer;
	}


	public int getChoicesNumber() {
		return choicesNumber;
	}


	public void setChoicesNumber(int choicesNumber) {
		this.choicesNumber = choicesNumber;
	}


}
