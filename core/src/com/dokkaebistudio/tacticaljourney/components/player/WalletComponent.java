package com.dokkaebistudio.tacticaljourney.components.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity can carry and pick up money.
 * @author Callil
 *
 */
public class WalletComponent implements Component, Poolable {
	
	/** Current amount of money. */
	private int amount;
	
	/** The number of extra items for sale in shops. Can be negative. */
	private int extraItemsInShops;
	
	
	@Override
	public void reset() {
		amount = 0;
		extraItemsInShops = 0;
	}
	

	/**
	 * Receive the given amount of money.
	 * @param amount the amount to receive
	 */
	public void receive(int amount) {
		this.amount += amount;
	}
	
	/**
	 * Check whether we have enough money.
	 * @param amountToCheck the amount to check
	 * @return true if the wallet has enough money
	 */
	public boolean hasEnoughMoney(int amountToCheck) {
		return this.amount >= amountToCheck;
	}
	
	/**
	 * Use the given amount of money.
	 * @param amount the amount to use
	 */
	public void use(int amount) {
		this.amount -= amount;
	}
	
	
	
	public void increaseExtraItemsInShops(int amount) {
		this.extraItemsInShops += amount;
	}
	
	
	
	
	
	// ***********************
	// Getters and setters 
	
	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
	

	public int getExtraItemsInShops() {
		return extraItemsInShops;
	}
	
	public void setExtraItemsInShops(int extraItemsInShops) {
		this.extraItemsInShops = extraItemsInShops;
	}
	
	
	public static Serializer<WalletComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<WalletComponent>() {

			@Override
			public void write(Kryo kryo, Output output, WalletComponent object) {
				output.writeInt(object.amount);
				output.writeInt(object.extraItemsInShops);
			}

			@Override
			public WalletComponent read(Kryo kryo, Input input, Class<? extends WalletComponent> type) {
				WalletComponent compo = engine.createComponent(WalletComponent.class);

				compo.amount = input.readInt(); 
				compo.extraItemsInShops = input.readInt(); 
				
				return compo;
			}
		
		};
	}

}
