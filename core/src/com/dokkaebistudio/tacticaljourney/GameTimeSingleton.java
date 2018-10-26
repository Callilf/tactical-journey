/**
 * 
 */
package com.dokkaebistudio.tacticaljourney;

/**
 * The Random Number Generator used for the whole game.
 * Should be instanciated with a seed once the game starts, and then used throughout the entire game.
 * @author Callil
 *
 */
public class GameTimeSingleton {
	
	/** The singleton instance. */
	private static GameTimeSingleton instance;
	
	private float elapsedTime;
	
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
	
	
	
	
	// Get and Set

	public float getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(float elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	

}
