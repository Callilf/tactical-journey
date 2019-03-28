package com.dokkaebistudio.tacticaljourney.items.pools.shops;

import java.util.ArrayList;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;

public class BasicShopItemPool extends ShopItemPool {
	
	public BasicShopItemPool() {
		this.id = "BasicShop";
	}

	/**
	 * This map contains the whole list of items that can be in the shop, as well as the unit price of each item.
	 */
	private static final List<PooledItemDescriptor> shopItemPool = new ArrayList<>();
	
	static {
		shopItemPool.add(new PooledItemDescriptor(ItemEnum.AMMO_ARROW, 10, 2));
		shopItemPool.add(new PooledItemDescriptor(ItemEnum.AMMO_BOMB, 10, 4));
		
		shopItemPool.add(new PooledItemDescriptor(ItemEnum.ARMOR_PIECE, 10, 8));
		shopItemPool.add(new PooledItemDescriptor(ItemEnum.ARMOR_LIGHT, 5, 20));

		shopItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_FIRE, 10, 6));
		shopItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_REGEN, 10, 8));
		shopItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_SMALL_HEALTH, 10, 10));
		shopItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_WING, 10, 8));
		
		shopItemPool.add(new PooledItemDescriptor(ItemEnum.ORB_CONTAINER, 10, 5));
	}
	
	
	
	@Override
	public List<PooledItemDescriptor> getItemPool() {
		return shopItemPool;
	}

	
	@Override
	public int getSumOfChances() {
		int sumOfChances = 0;
		for (PooledItemDescriptor pid : shopItemPool) {
			sumOfChances += pid.getChanceToDrop();
		}
		return sumOfChances;
	}
}
