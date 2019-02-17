package com.dokkaebistudio.tacticaljourney.items.pools.lootables;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate.ItemPoolRarity;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;

public abstract class LootableItemPool {

	/**
	 * This map contains the whole list of items that can be in the shop, as well as the unit price of each item.
	 */
	public abstract List<PooledItemDescriptor> getItemPool();
	public abstract int getSumOfChances();
	
	
	/**
	 * Randomly retrieve x item types. There can be duplicates.
	 * @param numberOfItemsToGet the number of items to retrieve
	 * @return the list of item types randomly chosen
	 */
	public List<PooledItemDescriptor> getItemTypes(int numberOfItemsToGet) {
		List<PooledItemDescriptor> result = new ArrayList<>();
		
		RandomXS128 seededRandom = RandomSingleton.getInstance().getSeededRandom();
		int randomInt = 0;
		
		int chance = 0;
		for (int i=0 ; i<numberOfItemsToGet ; i++) {
			
			randomInt = seededRandom.nextInt(getSumOfChances());
			for (PooledItemDescriptor pid : getItemPool()) {
				if (randomInt >= chance && randomInt < chance + pid.getChanceToDrop()) {
					//This item is chosen
					result.add(pid);
					break;
				}
				chance += pid.getChanceToDrop();
			}
			
			chance = 0;
		}
		
		return result;
	}

}
