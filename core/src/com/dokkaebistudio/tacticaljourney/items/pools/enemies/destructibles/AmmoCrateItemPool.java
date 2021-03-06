package com.dokkaebistudio.tacticaljourney.items.pools.enemies.destructibles;

import java.util.ArrayList;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;

public class AmmoCrateItemPool extends ItemPool {
	
	public AmmoCrateItemPool() {
		this.id = "AmmoCrate";
	}
	
	/**
	 * This map contains the whole list of items that can be in the shop, as well as the unit price of each item.
	 */
	private static final List<PooledItemDescriptor> commonItemPool = new ArrayList<>();
	
	static {
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.AMMO_ARROW, 2));						
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.AMMO_BOMB, 2));						
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.SHURIKEN, 1));	
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.SMOKE_BOMB, 1));				
	}
	

	private static final List<PooledItemDescriptor> rareItemPool = new ArrayList<>();
		
	static {
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_FIRE, 10));
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_SMALL_HEALTH, 10));
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_REGEN, 10));
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_WING, 10));
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
