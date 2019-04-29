package com.dokkaebistudio.tacticaljourney.items.pools.lootables;

import java.util.ArrayList;
import java.util.List;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;

public class PersonalBelongingsItemPool extends ItemPool {
	
	public PersonalBelongingsItemPool() {
		this.id = "PersonalBelongings";
	}
	
	
	/**
	 * This map contains the whole list of items that can be in the shop, as well as the unit price of each item.
	 */
	private static final List<PooledItemDescriptor> commonItemPool = new ArrayList<>();
		
	static {
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.TOTEM_OF_KALAMAZOO, 10, true));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.MITHRIDATIUM, 10, true));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.NURSE_EYE_PATCH, 10, true));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.FATA_MORGANA, 10, true));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.RAM_SKULL, 10, true));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.VEGETAL_GARMENT, 10, true));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.COLORFUL_TIE, 10, true));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.OLD_CROWN, 10, true));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.MEMENTO_MORI, 10, true));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.HEADBAND, 10, true));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.VILLANELLE, 10, true));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.POWDER_FLASK, 10, true));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.CAMO_BACKPACK, 10, true));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.MERCHANT_MASK, 10, true));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.HAND_PROSTHESIS, 10, true));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.LEFT_JIKATABI, 10, true));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.RIGHT_JIKATABI, 10, true));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.SHINOBI_HEADBAND, 10, true));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.SILKY_BEARD, 10, true));
		commonItemPool.add(new PooledItemDescriptor(ItemEnum.SCISSORHAND, 10, true));
	}
	

	private static final List<PooledItemDescriptor> rareItemPool = new ArrayList<>();
		
	static {
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
