package com.dokkaebistudio.tacticaljourney.items.pools.lootables;

import java.util.ArrayList;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;

public class OrbBagItemPool extends ItemPool {
	
	public OrbBagItemPool() {
		this.id = "OrbBag";
	}

	
	/**
	 * This map contains the whole list of items that can be in the shop, as well as the unit price of each item.
	 */
	private static final List<PooledItemDescriptor> commonItemPool = new ArrayList<>();
		
	static {
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.ENERGY_ORB, 25, false));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.VEGETAL_ORB, 25, false));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.POISON_ORB, 25, false));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.FIRE_ORB, 25, false));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.ORB_CONTAINER, 10, false));
	}
	

	private static final List<PooledItemDescriptor> rareItemPool = new ArrayList<>();
		
	static {
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.DEATH_ORB, 1, false));
	}
	
	
	@Override
	public List<PooledItemDescriptor> getCommonItemPool() {
		return commonItemPool;
	}
	
	@Override
	public List<PooledItemDescriptor> getRareItemPool() {
		return rareItemPool;
	}

	
}
