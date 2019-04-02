package com.dokkaebistudio.tacticaljourney.items.pools.lootables;

import java.util.ArrayList;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;

public class OrbBagItemPool extends LootableItemPool {
	
	public OrbBagItemPool() {
		this.id = "OrbBag";
	}
	
	private static final List<PooledItemDescriptor> itemPool = new ArrayList<>();
	private static int initialSumOfChances = -1;

	static {
		itemPool.add(new PooledItemDescriptor(ItemEnum.ENERGY_ORB, 25, false));
		itemPool.add(new PooledItemDescriptor(ItemEnum.VEGETAL_ORB, 25, false));
		itemPool.add(new PooledItemDescriptor(ItemEnum.POISON_ORB, 25, false));
		itemPool.add(new PooledItemDescriptor(ItemEnum.FIRE_ORB, 25, false));
		itemPool.add(new PooledItemDescriptor(ItemEnum.DEATH_ORB, 1, false));
		
		itemPool.add(new PooledItemDescriptor(ItemEnum.ORB_CONTAINER, 10, false));
	}
	
	
	@Override
	public List<PooledItemDescriptor> getItemPool() {
		return itemPool;
	}

	
	@Override
	public int getInitialSumOfChances() {
		if (initialSumOfChances == -1) {
			initialSumOfChances = 0;
			for (PooledItemDescriptor pid : getItemPool()) {
				initialSumOfChances += pid.getChanceToDrop();
			}
		}
		return initialSumOfChances;
	}

	@Override
	public void setInitialSumOfChances(int soc) {
		initialSumOfChances = soc;
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
