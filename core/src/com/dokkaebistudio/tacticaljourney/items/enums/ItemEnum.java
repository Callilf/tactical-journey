package com.dokkaebistudio.tacticaljourney.items.enums;

public enum ItemEnum {

	MONEY("Gold coin",1),
	
	// Ammos
	AMMO_ARROW("Arrow", 1),
	AMMO_BOMB("Bomb", 1),
	
	// Potions
	POTION_SMALL_HEALTH("Small health potion", 1),
	POTION_REGEN("Regeneration potion", 1),
	POTION_FIRE("Fire potion", 1),
	POTION_WING("Wing potion", 1),
	
	// Armors
	ARMOR_PIECE("Armor piece", 1),
	ARMOR_LIGHT("Light armor", 2),
	
	ORB_CONTAINER("Orb container", 1),
	
	// Misc
	WEB_SACK("Web sack", 1),
	VENOM_GLAND("Venom gland", 1),
	PEBBLE("Pebble", 1),
	
	
	// Orbs
	ENERGY_ORB("Energy orb", 1),
	VEGETAL_ORB("Vegetal orb", 1),
	POISON_ORB("Poison orb", 1),
	
	
	
	
	
	// Infusables
	TOTEM_OF_KALAMAZOO("Totem of Kalamazoo", 2),
	FATA_MORGANA("Fata morgana", 2),
	MITHRIDATIUM("Mithridatium", 2),
	NURSE_EYE_PATCH("Nurse eye patch", 2),
	VEGETAL_GARMENT("Vegetal garment", 2),
	RAM_SKULL("Ram skull", 2),

	
	
	// Boss items
	PANGOLIN_SCALE("Pangolin scale", 2);
	
	
	
	//****************************
	// Attributes and constructor
	
	/** The level of the item. The higher, the best the item is. */
	private int level;
	private String name;
	
	private ItemEnum(String name, int lvl) {
		this.setLevel(lvl);
		this.setName(name);
	}

	
	
	//**************************
	// Getters and setters
	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
