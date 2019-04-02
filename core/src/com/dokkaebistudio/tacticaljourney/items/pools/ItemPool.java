package com.dokkaebistudio.tacticaljourney.items.pools;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;

public abstract class ItemPool {
	
	public String id;

	public abstract void removeItemFromPool(ItemEnum itemEnum);
	
	public abstract int getInitialSumOfChances();
	public abstract void setInitialSumOfChances(int soc);
	
}
