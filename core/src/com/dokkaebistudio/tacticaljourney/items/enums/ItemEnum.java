package com.dokkaebistudio.tacticaljourney.items.enums;

public enum ItemEnum {

	MONEY(1),
	
	// Ammos
	AMMO_ARROW(1),
	AMMO_BOMB(1),
	
	// Potions
	POTION_SMALL_HEALTH(1),
	POTION_REGEN(1),
	POTION_FIRE(1),
	POTION_WING(1),
	
	// Armors
	ARMOR_PIECE(1),
	ARMOR_LIGHT(2),
	
	ORB_CONTAINER(1),
	
	// Misc
	WEB_SACK(1),
	VENOM_GLAND(1),
	PEBBLE(1),
	
	
	// Orbs
	ENERGY_ORB(1),
	VEGETAL_ORB(1),
	
	
	
	
	
	// Infusables
	TOTEM_OF_KALAMAZOO(2),
	FATA_MORGANA(2),
	MITHRIDATIUM(2),
	NURSE_EYE_PATCH(2),
	VEGETAL_GARMENT(2),
	RAM_SKULL(2),

	
	
	// Boss items
	PANGOLIN_SCALE(2);
	
	
	
	//****************************
	// Attributes and constructor
	
	/** The level of the item. The higher, the best the item is. */
	private int level;
	
	private ItemEnum(int lvl) {
		this.setLevel(lvl);
	}

	
	
	//**************************
	// Getters and setters
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	
}
