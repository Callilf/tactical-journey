package com.dokkaebistudio.tacticaljourney.items.pools.lootables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPoolSingleton;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;

public abstract class LootableItemPool extends ItemPool {
	
	private static final List<PooledItemDescriptor> removedItems = new ArrayList<>();

	/**
	 * This map contains the whole list of items that can be in the shop, as well as the unit price of each item.
	 */
	public abstract List<PooledItemDescriptor> getItemPool();
	public abstract int getSumOfChances();
	public abstract int getInitialSumOfChances();
	
	/**
	 * Randomly retrieve x item types. There can be duplicates.
	 * @param numberOfItemsToGet the number of items to retrieve
	 * @return the list of item types randomly chosen
	 */
	public List<PooledItemDescriptor> getItemTypes(int numberOfItemsToGet) {
		return getItemTypes(numberOfItemsToGet, true);
	}
	
	/**
	 * Randomly retrieve x item types. There can be duplicates.
	 * @param numberOfItemsToGet the number of items to retrieve
	 * @return the list of item types randomly chosen
	 */
	public List<PooledItemDescriptor> getItemTypes(int numberOfItemsToGet, boolean allowRemoval) {
		return getItemTypes(numberOfItemsToGet, allowRemoval, null);
	}
	
	
	
	/**
	 * Randomly retrieve x item types. There can be duplicates.
	 * @param numberOfItemsToGet the number of items to retrieve
	 * @return the list of item types randomly chosen
	 */
	public List<PooledItemDescriptor> getItemTypes(int numberOfItemsToGet, boolean allowRemoval, RandomXS128 randomToUse) {
		List<PooledItemDescriptor> result = new ArrayList<>();
		
		int randomInt = 0;
		
		List<PooledItemDescriptor> itemsToRemoveFromAllPools = new ArrayList<>();
		
		int chance = 0;
		for (int i=0 ; i<numberOfItemsToGet ; i++) {
			
			int sumOfChances = getSumOfChances();
			if (sumOfChances == 0) return result;

			randomInt = getNextRandomInt(getInitialSumOfChances() + 1, randomToUse);
			randomInt = randomInt % sumOfChances;
			Iterator<PooledItemDescriptor> poolIterator = getItemPool().iterator();
			while(poolIterator.hasNext()) {
				PooledItemDescriptor pid = poolIterator.next();
				if (randomInt >= chance && randomInt < chance + pid.getChanceToDrop()) {
					//This item is chosen
					result.add(pid);
					
					// Remove the item from the pool if needed
					if (pid.isRemoveFromPool() && allowRemoval) {
						poolIterator.remove();
						removedItems.add(pid);
						itemsToRemoveFromAllPools.add(pid);
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
		
		
		for (PooledItemDescriptor pid : itemsToRemoveFromAllPools) {
			ItemPoolSingleton.getInstance().removeItemFromPools(pid.getType());
		}		
		
		return result;
	}
	
	
	private int getNextRandomInt(int max, RandomXS128 random) {
		if (random != null) {
			return random.nextInt(max);
		} else {
			return RandomSingleton.getInstance().nextSeededInt(max);
		}
	}
	
	
	@Override
	public void removeItemFromPool(ItemEnum itemType) {
		Iterator<PooledItemDescriptor> iterator = getItemPool().iterator();
		while(iterator.hasNext()) {
			PooledItemDescriptor pid = iterator.next();
			if (pid.getType() == itemType) {
				iterator.remove();
				break;
			}
		}
	}

}
