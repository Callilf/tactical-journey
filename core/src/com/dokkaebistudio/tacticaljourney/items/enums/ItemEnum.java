package com.dokkaebistudio.tacticaljourney.items.enums;

import com.dokkaebistudio.tacticaljourney.Descriptions;

public enum ItemEnum {

	MONEY(Descriptions.ITEM_MONEY_TITLE,1),
	MONEY_MEDIUM(Descriptions.ITEM_MONEY_TITLE,1),
	MONEY_BIG(Descriptions.ITEM_MONEY_TITLE,1),
	
	// Ammos
	AMMO_ARROW(Descriptions.ITEM_ARROWS_TITLE, 1),
	AMMO_BOMB(Descriptions.ITEM_BOMBS_TITLE, 1),
	
	// End game
	UNIVERSAL_CURE(Descriptions.ITEM_UNIVERSAL_CURE_TITLE, 1),

	
	// Potions
	POTION_SMALL_HEALTH(Descriptions.ITEM_SMALL_HEALTH_POTION_TITLE, 1),
	POTION_REGEN(Descriptions.ITEM_REGEN_POTION_TITLE, 1),
	POTION_FIRE(Descriptions.ITEM_FIRE_POTION_TITLE, 1),
	POTION_WING(Descriptions.ITEM_WING_POTION_TITLE, 1),
	
	// Scrolls
	SCROLL_DOPPELGANGER(Descriptions.ITEM_SCROLL_DOPPELGANGER_TITLE, 1),
	
	// Armors
	ARMOR_PIECE(Descriptions.ITEM_ARMOR_PIECE_TITLE, 1),
	ARMOR_LIGHT(Descriptions.ITEM_LIGHT_ARMOR_TITLE, 2),
	
	ORB_CONTAINER(Descriptions.ITEM_ORB_CONTAINER_TITLE, 1),
	
	// Throw
	SHURIKEN(Descriptions.ITEM_SHURIKEN_TITLE, 1),
	SMOKE_BOMB(Descriptions.ITEM_SMOKE_BOMB_TITLE, 1),

	// Misc
	WEB_SACK(Descriptions.ITEM_WEB_SACK_TITLE, 1),
	VENOM_GLAND(Descriptions.ITEM_VENOM_GLAND_TITLE, 1),
	PEBBLE(Descriptions.ITEM_PEBBLE_TITLE, 1),
	WORMHOLE_SHARD(Descriptions.ITEM_WORMHOLE_SHARD_TITLE, 1),
	DIVINE_CATALYST(Descriptions.ITEM_DIVINE_CATALYST_TITLE, 1),
	
	
	// Orbs
	ENERGY_ORB(Descriptions.ORB_ENERGY_TITLE, 1),
	VEGETAL_ORB(Descriptions.ORB_VEGETAL_TITLE, 1),
	POISON_ORB(Descriptions.ORB_POISON_TITLE, 1),
	FIRE_ORB(Descriptions.ORB_FIRE_TITLE, 1),
	DEATH_ORB(Descriptions.ORB_DEATH_TITLE, 1),
	VOID_ORB(Descriptions.ORB_VOID_TITLE, 1),
	
	
	
	
	
	// Infusables
	TOTEM_OF_KALAMAZOO(Descriptions.ITEM_TOTEM_OF_KALAMAZOO_TITLE, 2),
	FATA_MORGANA(Descriptions.ITEM_FATA_MORGANA_TITLE, 2),
	MITHRIDATIUM(Descriptions.ITEM_MITHRIDATIUM_TITLE, 2),
	NURSE_EYE_PATCH(Descriptions.ITEM_NURSE_EYE_PATCH_TITLE, 2),
	VEGETAL_GARMENT(Descriptions.ITEM_VEGETAL_GARMENT_TITLE, 2),
	RAM_SKULL(Descriptions.ITEM_RAM_SKULL_TITLE, 2),
	COLORFUL_TIE(Descriptions.ITEM_COLORFUL_TIE_TITLE, 2),
	OLD_CROWN(Descriptions.ITEM_OLD_CROWN_TITLE, 2),
	MEMENTO_MORI(Descriptions.ITEM_MEMENTO_MORI_TITLE, 2),
	HEADBAND(Descriptions.ITEM_HEADBAND_TITLE, 2),
	VILLANELLE(Descriptions.ITEM_VILLANELLE_TITLE, 2),
	POWDER_FLASK(Descriptions.ITEM_POWDER_FLASK_TITLE, 2),
	CAMO_BACKPACK(Descriptions.ITEM_CAMO_BACKPACK_TITLE, 2),
	MERCHANT_MASK(Descriptions.ITEM_MERCHANT_MASK_TITLE, 2),
	HAND_PROSTHESIS(Descriptions.ITEM_HAND_PROSTHESIS_TITLE, 2),
	LEFT_JIKATABI(Descriptions.ITEM_LEFT_JIKATABI_TITLE, 2),
	RIGHT_JIKATABI(Descriptions.ITEM_RIGHT_JIKATABI_TITLE, 2),
	SHINOBI_HEADBAND(Descriptions.ITEM_SHINOBI_HEADBAND_TITLE, 2),
	SILKY_BEARD(Descriptions.ITEM_SILKY_BEARD_TITLE, 2),
	SCISSORHAND(Descriptions.ITEM_SCISSORHAND_TITLE, 2),

	
	
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
