package com.dokkaebistudio.tacticaljourney.items.pools.lootables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;

public abstract class LootableItemPool {
	
	private static final List<PooledItemDescriptor> removedItems = new ArrayList<>();


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
		
		int sumOfChances = getSumOfChances();
		if (sumOfChances == 0) return result;
		
		RandomXS128 seededRandom = RandomSingleton.getInstance().getSeededRandom();
		int randomInt = 0;
		
		int chance = 0;
		for (int i=0 ; i<numberOfItemsToGet ; i++) {
			
			randomInt = seededRandom.nextInt(sumOfChances + 1);
			Iterator<PooledItemDescriptor> poolIterator = getItemPool().iterator();
			while(poolIterator.hasNext()) {
				PooledItemDescriptor pid = poolIterator.next();
				if (randomInt >= chance && randomInt < chance + pid.getChanceToDrop()) {
					//This item is chosen
					result.add(pid);
					
					// Remove the item from the pool if needed
					if (pid.isRemoveFromPool()) {
						poolIterator.remove();
						removedItems.add(pid);
						if (getItemPool().isEmpty()) {
							getItemPool().addAll(removedItems);
						}
					}
					break;
				}
				chance += pid.getChanceToDrop();
			}
			
			chance = 0;
		}
		
		return result;
	}

}
