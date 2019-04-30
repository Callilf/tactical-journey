package com.dokkaebistudio.tacticaljourney.items.pools.enemies.tribesmen;

import java.util.ArrayList;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;

public class TribesmenScoutItemPool extends ItemPool {
	
	public TribesmenScoutItemPool() {
		this.id = "TribesmanScout";
	}
	
	/**
	 * This map contains the whole list of items that can be in the shop, as well as the unit price of each item.
	 */
	private static final List<PooledItemDescriptor> commonItemPool = new ArrayList<>();
		
	static {
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.MONEY, 10));

		commonItemPool.add(new PooledItemDescriptor(ItemEnum.AMMO_ARROW, 10));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.AMMO_BOMB, 10));
		
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.PEBBLE, 10));
		
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.SHURIKEN, 5));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.SMOKE_BOMB, 5));
		
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.SCROLL_DOPPELGANGER, 5));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.SCROLL_TELEPORTATION, 5));
	}
	

	private static final List<PooledItemDescriptor> rareItemPool = new ArrayList<>();
		
	static {
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_SMALL_HEALTH, 10));
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_FIRE, 10));
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
