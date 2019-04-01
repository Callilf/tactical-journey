/**
 * 
 */
package com.dokkaebistudio.tacticaljourney;

/**
 * The Game time and turn singleton used for the whole game.
 * @author Callil
 *
 */
public class GameTimeSingleton {
	
	/** The singleton instance. */
	private static GameTimeSingleton instance;
	
	private float elapsedTime;
	private int currentTurn= 1;
	
	/**
	 * Returns the singleton instance.
	 * @return the instance
	 */
	public static GameTimeSingleton getInstance() {
		if (instance == null) {
			instance = new GameTimeSingleton();
		}
		return instance;
	}

	/**
	 * Update the elapsed time.
	 * @param delta the delta time to add
	 */
	public void updateElapsedTime(float delta) {
		this.elapsedTime += delta;
	}
	
	public void nextTurn() {
		this.currentTurn ++;
	}
	
	
	
	// Get and Set

	public float getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(float elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	
	
	public static void dispose() {
		instance = null;
	}

	public int getCurrentTurn() {
		return currentTurn;
	}

	public void setCurrentTurn(int currentTurn) {
		this.currentTurn = currentTurn;
	}

}
