package com.dokkaebistudio.tacticaljourney.items.pools.lootables;

import java.util.ArrayList;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;

public class AdventurersSatchelItemPool extends LootableItemPool {
	
	/**
	 * This map contains the whole list of items that can be in the shop, as well as the unit price of each item.
	 */
	private static final List<PooledItemDescriptor> itemPool = new ArrayList<>();
		
	static {
		itemPool.add(new PooledItemDescriptor(ItemEnum.MONEY, 10));

		itemPool.add(new PooledItemDescriptor(ItemEnum.AMMO_ARROW, 10));
		itemPool.add(new PooledItemDescriptor(ItemEnum.AMMO_BOMB, 10));
		
		itemPool.add(new PooledItemDescriptor(ItemEnum.ARMOR_LIGHT, 5));
		itemPool.add(new PooledItemDescriptor(ItemEnum.ARMOR_PIECE, 2));

		itemPool.add(new PooledItemDescriptor(ItemEnum.POTION_FIRE, 10));
		itemPool.add(new PooledItemDescriptor(ItemEnum.POTION_REGEN, 10));
		itemPool.add(new PooledItemDescriptor(ItemEnum.POTION_SMALL_HEALTH, 10));				
		itemPool.add(new PooledItemDescriptor(ItemEnum.POTION_WING, 10));	
		
		itemPool.add(new PooledItemDescriptor(ItemEnum.ORB_CONTAINER, 5));	
		itemPool.add(new PooledItemDescriptor(ItemEnum.ENERGY_ORB, 8));
		itemPool.add(new PooledItemDescriptor(ItemEnum.VEGETAL_ORB, 8));		
		itemPool.add(new PooledItemDescriptor(ItemEnum.POISON_ORB, 8));		
	}
	
	
	@Override
	public List<PooledItemDescriptor> getItemPool() {
		return itemPool;
	}

	@Override
	public int getSumOfChances() {
		int sumOfChances = 0;
		for (PooledItemDescriptor pid : itemPool) {
			sumOfChances += pid.getChanceToDrop();
		}
		return sumOfChances;
	}

	
}
