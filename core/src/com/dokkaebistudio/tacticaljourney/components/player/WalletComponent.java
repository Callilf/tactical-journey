package com.dokkaebistudio.tacticaljourney.components.player;

import com.badlogic.ashley.core.Component;

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
	

}
