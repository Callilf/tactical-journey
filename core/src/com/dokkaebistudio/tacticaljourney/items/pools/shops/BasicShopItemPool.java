package com.dokkaebistudio.tacticaljourney.items.pools.shops;

import java.util.ArrayList;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;

public class BasicShopItemPool extends ItemPool {
	
	public BasicShopItemPool() {
		this.id = "BasicShop";
	}

	/**
	 * This map contains the whole list of items that can be in the shop, as well as the unit price of each item.
	 */
	private static final List<PooledItemDescriptor> commonItemPool = new ArrayList<>();

	static {
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.AMMO_ARROW, 10, 2));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.AMMO_BOMB, 10, 4));
		
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.ARMOR_PIECE, 10, 8));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.ARMOR_LIGHT, 5, 20));
		
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.SHURIKEN, 5, 8));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.SMOKE_BOMB, 5, 10));

		commonItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_FIRE, 10, 6));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_REGEN, 10, 8));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_SMALL_HEALTH, 10, 10));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_WING, 10, 8));
		
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.ORB_CONTAINER, 10, 5));
	}


	private static final List<PooledItemDescriptor> rareItemPool = new ArrayList<>();
		
	static {
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.DIVINE_CATALYST, 5, 40));
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.MERCHANT_MASK, 10, 25, true));
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
