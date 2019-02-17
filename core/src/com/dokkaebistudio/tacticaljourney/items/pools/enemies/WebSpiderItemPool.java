package com.dokkaebistudio.tacticaljourney.items.pools.enemies;

import java.util.ArrayList;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;

public class WebSpiderItemPool extends EnemyItemPool {
	
	/**
	 * This map contains the whole list of items that can be in the shop, as well as the unit price of each item.
	 */
	private static final List<PooledItemDescriptor> commonItemPool = new ArrayList<>();
	
	private static int commonSumOfChances;
	
	static {
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.MONEY, 10));

		commonItemPool.add(new PooledItemDescriptor(ItemEnum.AMMO_ARROW, 10));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.AMMO_BOMB, 10));
				
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.WEB_SACK, 50));
		
		for (PooledItemDescriptor pid : commonItemPool) {
			commonSumOfChances += pid.getChanceToDrop();
		}
	}
	

	private static final List<PooledItemDescriptor> rareItemPool = new ArrayList<>();
	
	private static int rareSumOfChances;
	
	static {
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.ARMOR_PIECE, 4));
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.ARMOR_LIGHT, 2));

		rareItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_FIRE, 10));
		rareItemPool.add(new PooledItemDescriptor(ItemEnum.POTION_SMALL_HEALTH, 10));
		
		for (PooledItemDescriptor pid : rareItemPool) {
			rareSumOfChances += pid.getChanceToDrop();
		}
	}
	
	
	@Override
	public List<PooledItemDescriptor> getCommonItemPool() {
		return commonItemPool;
	}
	
	@Override
	public List<PooledItemDescriptor> getRareItemPool() {
		return rareItemPool;
	}
	
	@Override
	public int getCommonSumOfChances() {
		return commonSumOfChances;
	}
	
	@Override
	public int getRareSumOfChances() {
		return rareSumOfChances;
	}
	
	
	
}
