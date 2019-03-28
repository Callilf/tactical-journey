package com.dokkaebistudio.tacticaljourney.items.pools.lootables;

import java.util.ArrayList;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;

public class OldBonesItemPool extends LootableItemPool {
	
	public OldBonesItemPool() {
		this.id = "OldBones";
	}
	
	/**
	 * This map contains the whole list of items that can be in the shop, as well as the unit price of each item.
	 */
	private static final List<PooledItemDescriptor> itemPool = new ArrayList<>();
		
	static {
		itemPool.add(new PooledItemDescriptor(ItemEnum.MONEY, 30));

		itemPool.add(new PooledItemDescriptor(ItemEnum.AMMO_ARROW, 20));
		itemPool.add(new PooledItemDescriptor(ItemEnum.AMMO_BOMB, 20));
		
		itemPool.add(new PooledItemDescriptor(ItemEnum.ARMOR_PIECE, 15));

		itemPool.add(new PooledItemDescriptor(ItemEnum.POTION_REGEN, 13));
		itemPool.add(new PooledItemDescriptor(ItemEnum.POTION_WING, 15));
		
		itemPool.add(new PooledItemDescriptor(ItemEnum.ORB_CONTAINER, 3));
		itemPool.add(new PooledItemDescriptor(ItemEnum.ENERGY_ORB, 2));
		itemPool.add(new PooledItemDescriptor(ItemEnum.VEGETAL_ORB, 2));
		itemPool.add(new PooledItemDescriptor(ItemEnum.POISON_ORB, 2));
		itemPool.add(new PooledItemDescriptor(ItemEnum.FIRE_ORB, 2));
		
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
