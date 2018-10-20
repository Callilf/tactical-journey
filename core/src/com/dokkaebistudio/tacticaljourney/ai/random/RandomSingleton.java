/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.ai.random;

import com.badlogic.gdx.math.RandomXS128;

/**
 * The Random Number Generator used for the whole game.
 * Should be instanciated with a seed once the game starts, and then used throughout the entire game.
 * @author Callil
 *
 */
public class RandomSingleton {
	
	/** The singleton instance. */
	private static RandomSingleton instance;
	
	/** The random. */
	private RandomXS128 random;
	
	/** The current seed. */
	private String seed;
	
	/**
	 * Creates the instance. Should be used only when the game starts.
	 * @return the created instance
	 */
	public static RandomSingleton createInstance() {
		instance = new RandomSingleton();
		return instance;
	}
	
	/**
	 * Creates the instance. Should be used only when the game starts.
	 * @return the created instance
	 */
	public static RandomSingleton createInstance(String seed) {
		if (seed != null && seed.length() > 0) {
			instance = new RandomSingleton(seed);
		} else {
			instance = new RandomSingleton();
		}
		return instance;
	}
	
	/**
	 * Returns the singleton instance.
	 * @return the instance
	 */
	public static RandomSingleton getInstance() {
		return instance;
	}
	
	
	/**
	 * Create the {@link RandomSingleton} without seed.
	 */
	private RandomSingleton() {
		RandomXS128 tempR = new RandomXS128();
		long seedPart1 = Math.abs(tempR.nextLong());
		long seedPart2 = Math.abs(tempR.nextLong());
		this.setSeed(String.valueOf(seedPart1) + "-" + String.valueOf(seedPart2));
		this.random = new RandomXS128(seedPart1, seedPart2);
	}
	
	/**
	 * Create the {@link RandomSingleton} without seed.
	 */
	private RandomSingleton(String seed) {
		String[] split = seed.split("-");
		Long l = new Long(split[0]);
		Long l2 = new Long(split[1]);
		this.setSeed(seed);
		this.random = new RandomXS128(l, l2);
	}
	
	
	
	
	// Get and Set

	public RandomXS128 getRandom() {
		return random;
	}

	public void setRandom(RandomXS128 random) {
		this.random = random;
	}

	public String getSeed() {
		return seed;
	}

	public void setSeed(String seed) {
		this.seed = seed;
	}

}
