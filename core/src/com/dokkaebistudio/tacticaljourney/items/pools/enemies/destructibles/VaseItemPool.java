package com.dokkaebistudio.tacticaljourney.items.pools.enemies.destructibles;

import java.util.ArrayList;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;

public class VaseItemPool extends ItemPool {
	
	public VaseItemPool() {
		this.id = "Vase";
	}
	
	/**
	 * This map contains the whole list of items that can be in the shop, as well as the unit price of each item.
	 */
	private static final List<PooledItemDescriptor> commonItemPool = new ArrayList<>();
	
	static {
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.MONEY, 19));		
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.MONEY_MEDIUM, 1));						
	}
	

	private static final List<PooledItemDescriptor> rareItemPool = new ArrayList<>();
		
	static {
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_FIRE, 10));
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_SMALL_HEALTH, 10));
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.WORMHOLE_SHARD, 10));
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
