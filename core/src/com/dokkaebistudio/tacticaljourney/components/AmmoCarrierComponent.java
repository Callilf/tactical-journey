package com.dokkaebistudio.tacticaljourney.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.enums.AmmoTypeEnum;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Marker to indicate that this entity is solid so the tile on which it stands is blocked.
 * @author Callil
 *
 */
public class AmmoCarrierComponent implements Component {
	
	// Arrows
	private int arrows;
	private int maxArrows;
	private Entity arrowsTextDisplayer;
	private Entity arrowsSpriteDisplayer;

	
	// Bombs
	private int bombs;
	private int maxBombs;
	private Entity bombsTextDisplayer;
	private Entity bombsSpriteDisplayer;

	
	
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
			updateArrowDisplayer();
			break;
		case BOMBS:
			bombs -= number;
			updateBombDisplayer();
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
			updateArrowDisplayer();
			break;
		case BOMBS:
			bombs += number;
			updateBombDisplayer();
			break;
			default:
		}
	}
	
	
	


	private void updateBombDisplayer() {
		TextComponent bombTextComponent = Mappers.textComponent.get(bombsTextDisplayer);
		bombTextComponent.setText(bombs + "/" + maxBombs);
	}

	private void updateArrowDisplayer() {
		TextComponent arrowTextComponent = Mappers.textComponent.get(arrowsTextDisplayer);
		arrowTextComponent.setText(arrows + "/" + maxArrows);
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



	public Entity getArrowsTextDisplayer() {
		return arrowsTextDisplayer;
	}



	public void setArrowsTextDisplayer(Entity arrowsDisplayer) {
		this.arrowsTextDisplayer = arrowsDisplayer;
	}



	public Entity getBombsTextDisplayer() {
		return bombsTextDisplayer;
	}



	public void setBombsTextDisplayer(Entity bombsDisplayer) {
		this.bombsTextDisplayer = bombsDisplayer;
	}



	public Entity getArrowsSpriteDisplayer() {
		return arrowsSpriteDisplayer;
	}



	public void setArrowsSpriteDisplayer(Entity arrowsSpriteDisplayer) {
		this.arrowsSpriteDisplayer = arrowsSpriteDisplayer;
	}



	public Entity getBombsSpriteDisplayer() {
		return bombsSpriteDisplayer;
	}



	public void setBombsSpriteDisplayer(Entity bombsSpriteDisplayer) {
		this.bombsSpriteDisplayer = bombsSpriteDisplayer;
	}

}
