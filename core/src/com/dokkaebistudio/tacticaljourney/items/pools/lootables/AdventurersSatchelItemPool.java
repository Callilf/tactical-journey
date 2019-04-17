package com.dokkaebistudio.tacticaljourney.items.pools.lootables;

import java.util.ArrayList;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;

public class AdventurersSatchelItemPool extends ItemPool {
	
	public AdventurersSatchelItemPool() {
		this.id = "AdventurersSatchel";
	}

	/**
	 * This map contains the whole list of items that can be in the shop, as well as the unit price of each item.
	 */
	private static final List<PooledItemDescriptor> commonItemPool = new ArrayList<>();
		
	static {
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.MONEY, 25));

		commonItemPool.add(new PooledItemDescriptor(ItemEnum.AMMO_ARROW, 20));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.AMMO_BOMB, 20));
		
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.SHURIKEN, 15));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.SMOKE_BOMB, 10));
		
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.ARMOR_LIGHT, 15));

		commonItemPool.add(new PooledItemDescriptor(ItemEnum.VEGETAL_ORB, 7));		

		commonItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_FIRE, 20));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_REGEN, 20));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_SMALL_HEALTH, 20));				
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_WING, 20));
	}
	

	private static final List<PooledItemDescriptor> rareItemPool = new ArrayList<>();
		
	static {
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.MONEY_MEDIUM, 3));
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.ARMOR_PIECE, 10));

		rareItemPool.add(new PooledItemDescriptor(ItemEnum.ORB_CONTAINER, 5));	
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.ENERGY_ORB, 7));
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.POISON_ORB, 7));		
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.FIRE_ORB, 7));		
		
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.WORMHOLE_SHARD, 5));	
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
