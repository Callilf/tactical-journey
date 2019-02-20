/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.pools;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing.BlessingsEnum;
import com.dokkaebistudio.tacticaljourney.alterations.Curse.CursesEnum;

/**
 * @author Callil
 *
 */
public abstract class AlterationPool {

	public abstract List<PooledAlterationDescriptor<BlessingsEnum>> blessingPool();
	public abstract int blessingSumOfChances();
	
	public abstract List<PooledAlterationDescriptor<CursesEnum>> cursePool();
	public abstract int curseSumOfChances();

	
	/**
	 * Randomly retrieve x blessing types. There can be duplicates.
	 * @param numberOfBlessingsToGet the number of blessing types to retrieve
	 * @return the list of blessing types randomly chosen
	 */
	public List<BlessingsEnum> getBlessingTypes(int numberOfBlessingsToGet) {
		List<BlessingsEnum> result = new ArrayList<>();
		
		RandomXS128 seededRandom = RandomSingleton.getInstance().getSeededRandom();
		int randomInt = 0;
		
		int chance = 0;
		for (int i=0 ; i<numberOfBlessingsToGet ; i++) {
			
			randomInt = seededRandom.nextInt(blessingSumOfChances());
			for (PooledAlterationDescriptor<BlessingsEnum> blessingDescriptor : blessingPool()) {
				if (randomInt >= chance && randomInt < chance + blessingDescriptor.getChanceToDrop()) {
					//This item is chosen
					result.add(blessingDescriptor.getType());
					break;
				}
				chance += blessingDescriptor.getChanceToDrop();
			}
			
			chance = 0;
		}
		
		return result;
	}
	
	/**
	 * Randomly retrieve x curse types. There can be duplicates.
	 * @param numberOfCursesToGet the number of curse types to retrieve
	 * @return the list of curse types randomly chosen
	 */
	public List<CursesEnum> getCurseTypes(int numberOfCursesToGet) {
		List<CursesEnum> result = new ArrayList<>();
		
		RandomXS128 seededRandom = RandomSingleton.getInstance().getSeededRandom();
		int randomInt = 0;
		
		int chance = 0;
		for (int i=0 ; i<numberOfCursesToGet ; i++) {
			
			randomInt = seededRandom.nextInt(curseSumOfChances());
			for (PooledAlterationDescriptor<CursesEnum> curseDescriptor : cursePool()) {
				if (randomInt >= chance && randomInt < chance + curseDescriptor.getChanceToDrop()) {
					result.add(curseDescriptor.getType());
					break;
				}
				chance += curseDescriptor.getChanceToDrop();
			}
			
			chance = 0;
		}
		
		return result;
	}
}
