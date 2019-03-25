package com.dokkaebistudio.tacticaljourney.items.pools.enemies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate.ItemPoolRarity;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPoolSingleton;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;

public abstract class EnemyItemPool extends ItemPool {

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
		
		RandomSingleton random = RandomSingleton.getInstance();
		int randomInt = 0;
		
		List<PooledItemDescriptor> itemsToRemoveFromAllPools = new ArrayList<>();
		
		int chance = 0;
		for (int i=0 ; i<numberOfItemsToGet ; i++) {
			
			randomInt = random.nextSeededInt(sumOfChances);
			Iterator<PooledItemDescriptor> poolIterator = getPool(rarity).iterator();
			while(poolIterator.hasNext()) {
				PooledItemDescriptor pid = poolIterator.next();
				if (randomInt >= chance && randomInt < chance + pid.getChanceToDrop()) {
					//This item is chosen
					result.add(pid);
					
					// Remove the item from the pool if needed
					if (pid.isRemoveFromPool()) {
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
	
	@Override
	public void removeItemFromPool(ItemEnum itemType) {
		Iterator<PooledItemDescriptor> iterator = getPool(ItemPoolRarity.COMMON).iterator();
		while(iterator.hasNext()) {
			PooledItemDescriptor pid = iterator.next();
			if (pid.getType() == itemType) {
				iterator.remove();
				break;
			}
		}
		iterator = getPool(ItemPoolRarity.RARE).iterator();
		while(iterator.hasNext()) {
			PooledItemDescriptor pid = iterator.next();
			if (pid.getType() == itemType) {
				iterator.remove();
				break;
			}
		}
	}
}
