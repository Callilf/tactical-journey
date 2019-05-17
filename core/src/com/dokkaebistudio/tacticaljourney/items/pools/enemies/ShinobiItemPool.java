package com.dokkaebistudio.tacticaljourney.items.pools.enemies;

import java.util.ArrayList;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;

public class ShinobiItemPool extends ItemPool {
	
	public ShinobiItemPool() {
		this.id = "Shinobi";
	}
	
	/**
	 * This map contains the whole list of items that can be in the shop, as well as the unit price of each item.
	 */
	private static final List<PooledItemDescriptor> commonItemPool = new ArrayList<>();
		
	static {
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.HAND_PROSTHESIS, 5, true));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.LEFT_JIKATABI, 5, true));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.RIGHT_JIKATABI, 5, true));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.SHINOBI_HEADBAND, 5, true));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_PURITY, 1, false));
	}
	

	private static final List<PooledItemDescriptor> rareItemPool = new ArrayList<>();
		
	static {
//		rareItemPool.add(new PooledItemDescriptor(ItemEnum.ARMOR_PIECE, 5));
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
