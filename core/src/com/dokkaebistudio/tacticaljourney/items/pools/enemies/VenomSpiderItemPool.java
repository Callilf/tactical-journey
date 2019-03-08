package com.dokkaebistudio.tacticaljourney.items.pools.enemies;

import java.util.ArrayList;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;

public class VenomSpiderItemPool extends EnemyItemPool {
	
	/**
	 * This map contains the whole list of items that can be in the shop, as well as the unit price of each item.
	 */
	private static final List<PooledItemDescriptor> commonItemPool = new ArrayList<>();
		
	static {
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.MONEY, 10));

		commonItemPool.add(new PooledItemDescriptor(ItemEnum.AMMO_ARROW, 10));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.AMMO_BOMB, 10));
				
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.WEB_SACK, 5));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.VENOM_GLAND, 30));
	}
	

	private static final List<PooledItemDescriptor> rareItemPool = new ArrayList<>();
		
	static {
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.ARMOR_PIECE, 10));
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.ARMOR_LIGHT, 1));

		rareItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_FIRE, 10));
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_REGEN, 10));
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
