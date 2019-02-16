package com.dokkaebistudio.tacticaljourney.items.pools;

import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;

public class PooledItemDescriptor {

	private ItemEnum type;
	private int price;
	private int chanceToDrop;
	
	
	public PooledItemDescriptor(ItemEnum type, int chanceToDrop) {
		this.setType(type);
		this.setChanceToDrop(chanceToDrop);
	}
	
	public PooledItemDescriptor(ItemEnum type, int chanceToDrop, int price) {
		this.setType(type);
		this.setPrice(price);
		this.setChanceToDrop(chanceToDrop);
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
	
	
	
}
