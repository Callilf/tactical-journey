package com.dokkaebistudio.tacticaljourney.components.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity can carry and pick up money.
 * @author Callil
 *
 */
public class WalletComponent implements Component {
	
	/** Current amount of money. */
	private int amount;

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
	
	
	// Getters and setters 
	
	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
	

	
	public static Serializer<WalletComponent> getSerializer(final PooledEngine engine, final Floor floor) {
		return new Serializer<WalletComponent>() {

			@Override
			public void write(Kryo kryo, Output output, WalletComponent object) {
				output.writeInt(object.amount);
			}

			@Override
			public WalletComponent read(Kryo kryo, Input input, Class<WalletComponent> type) {
				WalletComponent compo = engine.createComponent(WalletComponent.class);

				compo.amount = input.readInt(); 
				
				return compo;
			}
		
		};
	}
}
