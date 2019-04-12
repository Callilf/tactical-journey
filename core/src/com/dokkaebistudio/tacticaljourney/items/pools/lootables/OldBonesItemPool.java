package com.dokkaebistudio.tacticaljourney.items.pools.lootables;

import java.util.ArrayList;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;

public class OldBonesItemPool extends ItemPool {
	
	public OldBonesItemPool() {
		this.id = "OldBones";
	}
	

	/**
	 * This map contains the whole list of items that can be in the shop, as well as the unit price of each item.
	 */
	private static final List<PooledItemDescriptor> commonItemPool = new ArrayList<>();
		
	static {
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.MONEY, 30));

		commonItemPool.add(new PooledItemDescriptor(ItemEnum.AMMO_ARROW, 20));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.AMMO_BOMB, 20));
		

		commonItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_REGEN, 13));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_WING, 15));
		
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.ENERGY_ORB, 5));

	}
	

	private static final List<PooledItemDescriptor> rareItemPool = new ArrayList<>();
		
	static {
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.MONEY_MEDIUM, 5));
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.ARMOR_PIECE, 5));
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.ORB_CONTAINER, 3));
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.VEGETAL_ORB, 2));
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.POISON_ORB, 2));
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.FIRE_ORB, 2));
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
