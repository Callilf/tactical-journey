package com.dokkaebistudio.tacticaljourney.items.pools.lootables;

import java.util.ArrayList;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;

public class OrbBagItemPool extends LootableItemPool {
	
	private static final List<PooledItemDescriptor> itemPool = new ArrayList<>();
	
	static {
		itemPool.add(new PooledItemDescriptor(ItemEnum.ENERGY_ORB, 10, true));
		itemPool.add(new PooledItemDescriptor(ItemEnum.VEGETAL_ORB, 10, true));
		itemPool.add(new PooledItemDescriptor(ItemEnum.POISON_ORB, 10, true));
		
		itemPool.add(new PooledItemDescriptor(ItemEnum.ORB_CONTAINER, 3, true));
	}
	
	
	@Override
	public List<PooledItemDescriptor> getItemPool() {
		return itemPool;
	}

	
	@Override
	public int getSumOfChances() {
		int sumOfChances = 0;
		for (PooledItemDescriptor pid : itemPool) {
			sumOfChances += pid.getChanceToDrop();
		}
		return sumOfChances;
	}

	
}
