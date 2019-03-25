package com.dokkaebistudio.tacticaljourney.components.loot;

import java.util.LinkedHashMap;
import java.util.Map;

import com.badlogic.gdx.utils.Pool.Poolable;

public class DropRate implements Poolable {

	public enum ItemPoolRarity {
		COMMON,
		RARE;
	}
	
	private Map<ItemPoolRarity, Integer> ratePerRarity = new LinkedHashMap<>();
	
	public void add(ItemPoolRarity rarity, Integer rate) {
		ratePerRarity.put(rarity, rate);
	}
	
	
	@Override
	public void reset() {
		ratePerRarity.clear();
	}


	public Map<ItemPoolRarity, Integer> getRatePerRarity() {
		return ratePerRarity;
	}


	public void setRatePerRarity(Map<ItemPoolRarity, Integer> ratePerRarity) {
		this.ratePerRarity = ratePerRarity;
	}
	
	
	
}
