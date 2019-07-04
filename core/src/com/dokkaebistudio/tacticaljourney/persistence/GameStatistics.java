package com.dokkaebistudio.tacticaljourney.persistence;

import java.util.Calendar;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.singletons.GameTimeSingleton;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Shows all the main stats of a game.
 * @author Callil
 *
 */
public class GameStatistics {
	
	public enum GameStatisticsState {
		IN_PROGRESS,
		LOST,
		WON;
	}
	
	private String characterName;
	private int characterLevel;
	private int floorLevel;
	private GameStatisticsState state;
	
	private int totalTurns;
	private float totalTime;
	
	private int gold;
	
	/** The entity that killed the player, if the game is LOST.
	 * Null otherwise. */
	private String killer;
	
	
	private int day;
	private int month;
	private int year;

	
	
	
	public static GameStatistics create(GameScreen gamescreen) {
		GameStatistics gameStatistics = new GameStatistics();
		
		Entity player = gamescreen.player;
		gameStatistics.setCharacterName(Mappers.inspectableComponent.get(player).getTitle());
		gameStatistics.setCharacterLevel(Mappers.experienceComponent.get(player).getLevel());
		gameStatistics.setFloorLevel(gamescreen.activeFloor.getLevel());
		
		gameStatistics.setTotalTime(GameTimeSingleton.getInstance().getElapsedTime());
		gameStatistics.setTotalTurns(GameTimeSingleton.getInstance().getCurrentTurn());
		
		gameStatistics.setGold(Mappers.walletComponent.get(player).getAmount());
		
		gameStatistics.state = GameStatisticsState.IN_PROGRESS;
		
		Calendar cal = Calendar.getInstance();
		gameStatistics.day = cal.get(Calendar.DATE);
		gameStatistics.month = cal.get(Calendar.MONTH);
		gameStatistics.year = cal.get(Calendar.YEAR);
		
		return gameStatistics;
	}
	
	
	// Getters and Setters

	public String getCharacterName() {
		return characterName;
	}

	public void setCharacterName(String characterName) {
		this.characterName = characterName;
	}

	public int getCharacterLevel() {
		return characterLevel;
	}

	public void setCharacterLevel(int characterLevel) {
		this.characterLevel = characterLevel;
	}

	public int getFloorLevel() {
		return floorLevel;
	}

	public void setFloorLevel(int floorLevel) {
		this.floorLevel = floorLevel;
	}

	public GameStatisticsState getState() {
		return state;
	}

	public void setState(GameStatisticsState state) {
		this.state = state;
	}

	public int getTotalTurns() {
		return totalTurns;
	}

	public void setTotalTurns(int totalTurns) {
		this.totalTurns = totalTurns;
	}

	public float getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(float totalTime) {
		this.totalTime = totalTime;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public String getKiller() {
		return killer;
	}

	public void setKiller(String killer) {
		this.killer = killer;
	}

	public int getDay() {
		return day;
	}

	public int getMonth() {
		return month;
	}

	public int getYear() {
		return year;
	}

	
	
}
