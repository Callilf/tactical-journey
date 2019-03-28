package com.dokkaebistudio.tacticaljourney.items.pools;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;

public abstract class ItemPool {
	
	public int id;

	public abstract void removeItemFromPool(ItemEnum itemEnum);
	
}
