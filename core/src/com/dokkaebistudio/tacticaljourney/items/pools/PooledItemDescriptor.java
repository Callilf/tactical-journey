package com.dokkaebistudio.tacticaljourney.items.pools;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;

public class PooledItemDescriptor {

	private ItemEnum type;
	private int price;
	private int chanceToDrop;
	private boolean removeFromPool;
	
	
	public PooledItemDescriptor(ItemEnum type, int chanceToDrop) {
		this(type, chanceToDrop, 0, false);
	}
	
	public PooledItemDescriptor(ItemEnum type, int chanceToDrop, int price) {
		this(type, chanceToDrop, price, false);
	}
	
	public PooledItemDescriptor(ItemEnum type, int chanceToDrop, boolean removeFromPool) {
		this(type, chanceToDrop, 0, removeFromPool);
	}
	
	public PooledItemDescriptor(ItemEnum type, int chanceToDrop, int price, boolean removeFromPool) {
		this.setType(type);
		this.setPrice(price);
		this.setChanceToDrop(chanceToDrop);
		this.setRemoveFromPool(removeFromPool);
	}

	
	
	
	
	public ItemEnum getType() {
		return type;
	}

	public void setType(ItemEnum type) {
		this.type = type;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getChanceToDrop() {
		return chanceToDrop;
	}

	public void setChanceToDrop(int chanceToDrop) {
		this.chanceToDrop = chanceToDrop;
	}

	public boolean isRemoveFromPool() {
		return removeFromPool;
	}

	public void setRemoveFromPool(boolean removeFromPool) {
		this.removeFromPool = removeFromPool;
	}
	
	
	
}
