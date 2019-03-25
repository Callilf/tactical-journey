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
	
	/** The random that follows the given seed, for floor layouts, enemies, items... */
	private RandomXS128 seededRandom;
	/** The random used for combat or any RNG event that doesn't need to use the seed. */
	private RandomXS128 unseededRandom;
	
	/** The current seed. */
	private String seed;
	
	private int seededNextCounter = 0;
	
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
		this.seededRandom = new RandomXS128(seedPart1, seedPart2);
		this.unseededRandom = new RandomXS128();
	}
	
	/**
	 * Create the {@link RandomSingleton} without seed.
	 */
	private RandomSingleton(String seed) {
		String[] split = seed.split("-");
		
		Long l = new Long(split[0]);
		if (split.length > 1) {
			Long l2 = new Long(split[1]);
			this.seededRandom = new RandomXS128(l, l2);
		} else {
			this.seededRandom = new RandomXS128(l, 0);
		}
		
		this.setSeed(seed);
		this.unseededRandom = new RandomXS128();
	}
	
	
	public static void dispose() {
		instance = null;
	}
	
	
	public int nextSeededInt(int max) {
		seededNextCounter++;
		int nextInt = getSeededRandom().nextInt(max);
//		System.out.println("nextInt(" + max + "): " + nextInt);
		return nextInt;
	}
	public float nextSeededFloat() {
		seededNextCounter++;
		float f = getSeededRandom().nextFloat();
//		System.out.println("nextFloat(): " + f);
		return f;
	}
	
	public int nextUnseededInt(int max) {
		return getUnseededRandom().nextInt(max);
	}
	public float nextUnseededFloat() {
		return getUnseededRandom().nextFloat();
	}
	
	
	public RandomXS128 getSeededRandomForShuffle() {
		String[] split = seed.split("-");
		Long l = new Long(split[0]);
		RandomXS128 r = new RandomXS128(l, nextSeededInt(1000));
		return r;
	}
	
	// Get and Set

	private RandomXS128 getSeededRandom() {
		return seededRandom;
	}
	
	public RandomXS128 getUnseededRandom() {
		return unseededRandom;
	}

	public String getSeed() {
		return seed;
	}

	public void setSeed(String seed) {
		this.seed = seed;
	}
	
	public int getSeededNextCounter() {
		return seededNextCounter;
	}
}
