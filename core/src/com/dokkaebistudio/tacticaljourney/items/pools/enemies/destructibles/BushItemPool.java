package com.dokkaebistudio.tacticaljourney.items.pools.enemies.destructibles;

import java.util.ArrayList;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;

public class BushItemPool extends ItemPool {
	
	public BushItemPool() {
		this.id = "bush";
	}
	
	/**
	 * This map contains the whole list of items that can be in the shop, as well as the unit price of each item.
	 */
	private static final List<PooledItemDescriptor> commonItemPool = new ArrayList<>();
	
	static {
	}
	

	private static final List<PooledItemDescriptor> rareItemPool = new ArrayList<>();
		
	static {
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.CLOVER, 1));
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
