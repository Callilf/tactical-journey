/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.rewards;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * Represents the rewards received upon clearing a room.
 * @author Callil
 *
 */
public abstract class AbstractRoomReward {

	protected String color;
	protected String title;
	protected int quantity;
	
	public AbstractRoomReward(String title, int quantity) {
		this.title = title;
		this.quantity = quantity;
	}
	
	public AbstractRoomReward(String title, int quantity, String color) {
		this.title = title;
		this.quantity = quantity;
		this.color = color;
	}

	public abstract void receive(Entity e, Room r);

	
	
	// Getters and Setters
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public String getColor() {
		return color;
	}
	
	public void setColor(String color) {
		this.color = color;
	}
	
}
