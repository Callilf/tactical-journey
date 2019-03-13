package com.dokkaebistudio.tacticaljourney.components.player;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.leveling.ExperienceLevelEnum;

/**
 * Marker to indicate that this entity can acquire experience.
 * @author Callil
 *
 */
public class ExperienceComponent implements Component,Poolable {
		
	/** The current level of the entity. */
	private int level;
	/** The current amount of xp. */
	private int currentXp;
	/** The amount of xp to reach next level. */
	private int nextLevelXp;
	
	private List<Integer> xpGainedAtCurrentFrame = new ArrayList<>();
	
	/** Whether the entity just leveled up. */
	private int numberOfNewLevelReached;
	private boolean levelUpPopinDisplayed = false;
	
	/** The number of level up reward choices. */
	private int choicesNumber = 3;
	
	/** The number of choices that can be selected. */
	private int selectNumber;
	

	@Override
	public void reset() {
		xpGainedAtCurrentFrame.clear();
		levelUpPopinDisplayed = false;
		numberOfNewLevelReached = 0;
		level = 1;
		currentXp = 0;
		selectNumber = 1;
		
		ExperienceLevelEnum experienceLevelEnum = ExperienceLevelEnum.get(level);
		nextLevelXp = experienceLevelEnum.getXpToNextLevel();
	}

	
	/**
	 * Earn the given amount of experience.
	 * @param amountToEarn the amount to earn.
	 */
	public void earnXp(int amountToEarn) {
		Journal.addEntry("You gained [YELLOW]" + amountToEarn + " xp");
		this.xpGainedAtCurrentFrame.add(amountToEarn);
		
		if (nextLevelXp > currentXp + amountToEarn) {
			currentXp += amountToEarn;
		} else {
			this.levelUp(currentXp + amountToEarn - nextLevelXp);
		}
	}
	
	
	/**
	 * Level up.
	 * @param startingXp the new amount of current XP.
	 */
	private void levelUp(int startingXp) {
		level += 1;
		currentXp = startingXp;
		numberOfNewLevelReached = numberOfNewLevelReached + 1 ;

		ExperienceLevelEnum experienceLevelEnum = ExperienceLevelEnum.get(level);
		nextLevelXp = experienceLevelEnum.getXpToNextLevel();
		
		Journal.addEntry("[GREEN]LEVEL UP: You reached level " + level);
		
		if (currentXp > nextLevelXp) {
			levelUp(currentXp - nextLevelXp);
		}
	}
	
	/**
	 * Get the level to display in the level up popin.
	 * It can differ from the current level if we gain multiple level at the same time and we
	 * have to display multiple popins in a row.
	 * @return the level to display in the current popin
	 */
	public int getLevelForPopin() {
		return level - (numberOfNewLevelReached - 1);
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

	public int getChoicesNumber() {
		return choicesNumber;
	}


	public void setChoicesNumber(int choicesNumber) {
		this.choicesNumber = choicesNumber;
	}


	public int getNumberOfNewLevelReached() {
		return numberOfNewLevelReached;
	}


	public void setNumberOfNewLevelReached(int numberOfNewLevelReached) {
		this.numberOfNewLevelReached = numberOfNewLevelReached;
		if (this.numberOfNewLevelReached < 0) this.numberOfNewLevelReached = 0;
	}


	public boolean isLevelUpPopinDisplayed() {
		return levelUpPopinDisplayed;
	}


	public void setLevelUpPopinDisplayed(boolean levelUpPopinDisplayed) {
		this.levelUpPopinDisplayed = levelUpPopinDisplayed;
	}


	public List<Integer> getXpGainedAtCurrentFrame() {
		return xpGainedAtCurrentFrame;
	}


	public void addXpGainedAtCurrentFrame(Integer xpGainedAtCurrentFrame) {
		this.xpGainedAtCurrentFrame.add(xpGainedAtCurrentFrame);
	}


	public int getSelectNumber() {
		return selectNumber;
	}


	public void setSelectNumber(int selectNumber) {
		this.selectNumber = selectNumber;
	}

	

}
