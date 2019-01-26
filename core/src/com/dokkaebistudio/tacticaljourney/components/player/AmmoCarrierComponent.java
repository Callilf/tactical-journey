package com.dokkaebistudio.tacticaljourney.components.player;

import com.badlogic.ashley.core.Component;
import com.dokkaebistudio.tacticaljourney.enums.AmmoTypeEnum;

/**
 * Marker to indicate that this entity is solid so the tile on which it stands is blocked.
 * @author Callil
 *
 */
public class AmmoCarrierComponent implements Component {
	
	// Arrows
	private int arrows;
	private int maxArrows;
	
	// Bombs
	private int bombs;
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
	 */
	public void pickUpAmmo(AmmoTypeEnum ammoType, int number) {
		switch(ammoType) {
		case ARROWS:
			arrows += number;
			break;
		case BOMBS:
			bombs += number;
			break;
			default:
		}
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

}
