package com.dokkaebistudio.tacticaljourney.items.pools.lootables;

import java.util.ArrayList;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;

public class PersonalBelongingsItemPool extends LootableItemPool {
	
	/**
	 * This map contains the whole list of items that can be in the shop, as well as the unit price of each item.
	 */
	private static final List<PooledItemDescriptor> itemPool = new ArrayList<>();
	
	private static int sumOfChances;
	
	static {
		itemPool.add(new PooledItemDescriptor(ItemEnum.TOTEM_OF_KALAMAZOO, 10));
		itemPool.add(new PooledItemDescriptor(ItemEnum.MITHRIDATIUM, 10));
		itemPool.add(new PooledItemDescriptor(ItemEnum.NURSE_EYE_PATCH, 10));
		itemPool.add(new PooledItemDescriptor(ItemEnum.FATA_MORGANA, 10));
		
		for (PooledItemDescriptor pid : itemPool) {
			sumOfChances += pid.getChanceToDrop();
		}
	}
	
	
	@Override
	public List<PooledItemDescriptor> getItemPool() {
		return itemPool;
	}

	
	@Override
	public int getSumOfChances() {
		return sumOfChances;
	}

	
}
