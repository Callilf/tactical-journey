/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.ai.random;

import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.ces.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

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
	
	/**
	 * Returns a new instance of RandomXS128 that is initialized with a seed based on
	 * the current RandomSingleton seed and the nextInt.
	 * @return a random.
	 */
	public RandomXS128 getNextSeededRandom() {
		String[] split = seed.split("-");
		Long l = new Long(split[0]);
		RandomXS128 r = new RandomXS128(l, nextSeededInt(1000));
		return r;
	}
	
	public String getStateOfSeededRandom() {
		long seed0 = getSeededRandom().getState(0);
		long seed1 = getSeededRandom().getState(1);
		return seed0 + "#" + seed1;
	}
	
	public void restoreState(String seed) {
		String[] split = seed.split("#");
		getSeededRandom().setState(Long.valueOf(split[0]), Long.valueOf(split[1]));
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
	
	public Long[] getSeedArray() {
		Long[] result = new Long[2];

		String[] split = seed.split("-");
		result[0] = new Long(split[0]);
		if (split.length > 1) {
			result[1] = new Long(split[1]);
		} else {
			result[1] = (long) 0;
		}
		return result;
	}

	public void setSeed(String seed) {
		this.seed = seed;
	}
	
	public int getSeededNextCounter() {
		return seededNextCounter;
	}
	
	
	
	
	//********************************************
	// Useful methods
	
	/**
	 * Return a random decimal number between 0 and 99.99999 computing using the unseeded random
	 * and modified by the current karma of the player.
	 * @param random the random to use
	 * @return the random decimal number
	 */
	public static float getNextChanceWithKarma() {
		return getNextChanceWithKarma(RandomSingleton.getInstance().unseededRandom);
	}
	
	/**
	 * Return a random decimal number between 0 and 99.99999 computing using the given random
	 * and modified by the current karma of the player.
	 * @param random the random to use
	 * @return the random decimal number
	 */
	public static float getNextChanceWithKarma(RandomXS128 random) {
		float unit = (float) random.nextInt(100);
		float decimal = random.nextFloat();
		float randomValue = unit + decimal;

		// Apply player's karma
		PlayerComponent playerComponent = Mappers.playerComponent.get(GameScreen.player);
		randomValue -= playerComponent.getKarma();
		if (randomValue < 0) randomValue = 0;
		
		return randomValue;
	}
	
	/**
	 * Clone a random.
	 * @param random the random to clone
	 * @return a new {@link RandomXS128} instance with the same seed and state.
	 */
	public static RandomXS128 cloneRandom(RandomXS128 random) {
		return new RandomXS128(random.getState(0), random.getState(1));
	}
	
	
}
