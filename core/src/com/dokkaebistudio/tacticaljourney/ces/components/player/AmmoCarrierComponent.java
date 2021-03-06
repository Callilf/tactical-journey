package com.dokkaebistudio.tacticaljourney.ces.components.player;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.dokkaebistudio.tacticaljourney.enums.AmmoTypeEnum;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is solid so the tile on which it stands is blocked.
 * @author Callil
 *
 */
public class AmmoCarrierComponent implements Component {
	
	// Arrows
	/** Current number of arrows. */
	private int arrows;
	/** Max number of arrows. */
	private int maxArrows;
	
	// Bombs
	/** Current number of bombs. */
	private int bombs;
	/** Max number of bombs. */
	private int maxBombs;
	
	
	/**
	 * Check whether you can use the given number of the given type of ammunitions.
	 * @param ammoType the type of ammo
	 * @param number the number of ammo consumed.
	 * @return true if you have enough ammos.
	 */
	public boolean canUseAmmo(AmmoTypeEnum ammoType, int number) {
		boolean result = false;
		switch(ammoType) {
		case ARROWS:
			result = arrows >= number;
			break;
		case BOMBS:
			result = bombs >= number;
			break;
		case NONE:
			result = true;
			break;
			default:
		}
		return result;
	}
	
	
	
	/**
	 * Use the given number of the given type of ammunitions.
	 * @param ammoType the type of ammo
	 * @param number the number of ammo consumed.
	 */
	public void useAmmo(AmmoTypeEnum ammoType, int number) {
		switch(ammoType) {
		case ARROWS:
			arrows -= number;
			break;
		case BOMBS:
			bombs -= number;
			break;
			default:
		}
	}

	
	/**
	 * Check whether you can pickup the given number of the given type of ammunitions.
	 * @param ammoType the type of ammo
	 * @param number the number of ammo consumed.
	 * @return true if you have enough space for ammos.
	 */
	public boolean canPickUpAmmo(AmmoTypeEnum ammoType, int number) {
		boolean result = false;
		switch(ammoType) {
		case ARROWS:
			result = arrows + number <= maxArrows;
			break;
		case BOMBS:
			result = bombs + number <= maxBombs;
			break;
			default:
		}
		return result;
	}
	
	/**
	 * Pick up the given number of the given type of ammunitions.
	 * @param ammoType the type of ammo
	 * @param number the number of ammo picked.
	 * @return the remaining number of ammos that cannot be picked up
	 */
	public int pickUpAmmo(AmmoTypeEnum ammoType, int number) {
		int remaining = 0;
		switch(ammoType) {
		case ARROWS:
			arrows += number;
			if (arrows > maxArrows) {
				remaining = arrows - maxArrows;
				arrows = maxArrows;
			}
			break;
		case BOMBS:
			bombs += number;
			if (bombs > maxBombs) {
				remaining = bombs - maxBombs;
				bombs = maxBombs;
			}
			break;
			default:
		}
		return remaining;
	}
	
	
	
	// Increase MAX methods
	
	/**
	 * Increase the max number of arrows.
	 * @param amount the amount to add
	 */
	public void increaseMaxArrows(int amount) {
		this.maxArrows += amount;
		this.arrows += amount;
	}
	
	/**
	 * Increase the max number of bombs.
	 * @param amount the amount to add
	 */
	public void increaseMaxBombs(int amount) {
		this.maxBombs += amount;
		this.bombs += amount;
	}
	
	/**
	 * Increase the max number of bombs and arrows.
	 * @param amount the amount to add
	 */
	public void increaseMaxBombsAndArrows(int amount) {
		this.increaseMaxArrows(amount);
		this.increaseMaxBombs(amount);
	}
	

	
	
	// Getters and Setters !

	public int getArrows() {
		return arrows;
	}

	public void setArrows(int arrows) {
		this.arrows = arrows;
	}

	public int getBombs() {
		return bombs;
	}

	public void setBombs(int bombs) {
		this.bombs = bombs;
	}



	public int getMaxArrows() {
		return maxArrows;
	}



	public void setMaxArrows(int maxArrows) {
		this.maxArrows = maxArrows;
	}



	public int getMaxBombs() {
		return maxBombs;
	}



	public void setMaxBombs(int maxBombs) {
		this.maxBombs = maxBombs;
	}

	
	
	
	
	public static Serializer<AmmoCarrierComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<AmmoCarrierComponent>() {

			@Override
			public void write(Kryo kryo, Output output, AmmoCarrierComponent object) {
				output.writeInt(object.maxArrows);
				output.writeInt(object.arrows);
				output.writeInt(object.maxBombs);
				output.writeInt(object.bombs);
			}

			@Override
			public AmmoCarrierComponent read(Kryo kryo, Input input, Class<? extends AmmoCarrierComponent> type) {
				AmmoCarrierComponent compo = engine.createComponent(AmmoCarrierComponent.class);

				compo.maxArrows = input.readInt(); 
				compo.arrows = input.readInt(); 
				compo.maxBombs = input.readInt(); 
				compo.bombs = input.readInt(); 
				
				return compo;
			}
		
		};
	}
}
