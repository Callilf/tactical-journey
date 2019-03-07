package com.dokkaebistudio.tacticaljourney.items.pools.enemies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate.ItemPoolRarity;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;

public abstract class EnemyItemPool {

	/**
	 * This map contains the whole list of items that can be in the shop, as well as the unit price of each item.
	 */
	public abstract List<PooledItemDescriptor> getCommonItemPool();	

	public abstract List<PooledItemDescriptor> getRareItemPool();

	
	/**
	 * Randomly retrieve x item types. There can be duplicates.
	 * @param numberOfItemsToGet the number of items to retrieve
	 * @return the list of item types randomly chosen
	 */
	public List<PooledItemDescriptor> getItemTypes(int numberOfItemsToGet, ItemPoolRarity rarity) {
		List<PooledItemDescriptor> result = new ArrayList<>();
		
		int sumOfChances = getSum(rarity);
		if (sumOfChances == 0) return result;
		
		RandomXS128 seededRandom = RandomSingleton.getInstance().getSeededRandom();
		int randomInt = 0;
		
		int chance = 0;
		for (int i=0 ; i<numberOfItemsToGet ; i++) {
			
			randomInt = seededRandom.nextInt(sumOfChances);
			Iterator<PooledItemDescriptor> poolIterator = getPool(rarity).iterator();
			while(poolIterator.hasNext()) {
				PooledItemDescriptor pid = poolIterator.next();
				if (randomInt >= chance && randomInt < chance + pid.getChanceToDrop()) {
					//This item is chosen
					result.add(pid);
					
					// Remove the item from the pool if needed
					if (pid.isRemoveFromPool()) {
						poolIterator.remove();
					}
					break;
				}
				chance += pid.getChanceToDrop();
			}
			
			chance = 0;
		}
		
		return result;
	}
	
	
	private int getSum(ItemPoolRarity rarity) {
		int sum = 0;
		for (PooledItemDescriptor pid : getPool(rarity)) {
			sum += pid.getChanceToDrop();
		}
		return sum;
	}
	
	private List<PooledItemDescriptor> getPool(ItemPoolRarity rarity) {
		switch (rarity) {
		case COMMON:
			return getCommonItemPool();
		case RARE:
			return getRareItemPool();
		}
		return null;
	}
}
