package com.dokkaebistudio.tacticaljourney.items.pools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.ces.components.loot.DropRate.ItemPoolRarity;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.util.LootUtil;

public abstract class ItemPool {
	
	public String id;

	/**
	 * This map contains the whole list of items that can be in the shop, as well as the unit price of each item.
	 */
	public abstract List<PooledItemDescriptor> getCommonItemPool();	

	public abstract List<PooledItemDescriptor> getRareItemPool();

	
	/**
	 * Randomly retrieve x item types. There can be duplicates.
	 * @param numberOfItemsToGet the number of items to get
	 * @param rarity the rarity of the items
	 * @param randomToUse the random instance to use
	 * @return the list of items.
	 */
	public List<PooledItemDescriptor> getItemTypes(int numberOfItemsToGet, ItemPoolRarity rarity, RandomXS128 randomToUse) {
		return getItemTypes(numberOfItemsToGet, rarity, randomToUse, false);
	}
	
	
	/**
	 * Randomly retrieve x item types. There can be duplicates.
	 * @param numberOfItemsToGet the number of items to get
	 * @param rarity the rarity of the items
	 * @param randomToUse the random instance to use
	 * @param doNotRemove prevents removing the item from the item pools
	 * @return the list of items.
	 */
	public List<PooledItemDescriptor> getItemTypes(int numberOfItemsToGet, ItemPoolRarity rarity, RandomXS128 randomToUse, boolean doNotRemove) {
		List<PooledItemDescriptor> result = new ArrayList<>();
		
		int sumOfChances = getSum(rarity);
		if (sumOfChances == 0) return result;
		
		int randomInt = 0;
		
		List<PooledItemDescriptor> itemsToRemoveFromAllPools = new ArrayList<>();
		
		int chance = 0;
		for (int i=0 ; i<numberOfItemsToGet ; i++) {
			
			randomInt = randomToUse.nextInt(sumOfChances);
			Iterator<PooledItemDescriptor> poolIterator = getPool(rarity).iterator();
			while(poolIterator.hasNext()) {
				PooledItemDescriptor pid = poolIterator.next();
				if (randomInt >= chance && randomInt < chance + pid.getChanceToDrop()) {
					//This item is chosen
					result.add(pid);
					
					// Remove the item from the pool if needed
					if (!doNotRemove && pid.isRemoveFromPool()) {
						poolIterator.remove();
						itemsToRemoveFromAllPools.add(pid);
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
	
	public void removeItemFromPool(ItemEnum itemType) {
		Iterator<PooledItemDescriptor> iterator = getPool(ItemPoolRarity.COMMON).iterator();
		while(iterator.hasNext()) {
			if (iterator.next().getType() == itemType) {
				iterator.remove();
				break;
			}
		}
		iterator = getPool(ItemPoolRarity.RARE).iterator();
		while(iterator.hasNext()) {
			if (iterator.next().getType() == itemType) {
				iterator.remove();
				break;
			}
		}
	}
	
	
	
	// Not used at the moment, but will be used if enemies can drop unique items
	
	public int getInitialSumOfChances() {
		return 0;
	}

	public void setInitialSumOfChances(int soc) {}
	
}
