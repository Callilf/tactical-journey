package com.dokkaebistudio.tacticaljourney.enums;

public enum InventoryDisplayModeEnum {

	NONE,
	INVENTORY,
	INFUSION,
	RECYCLING,
	LOOT,
	DEBUG;
	
	
	public boolean isInventoryPopin() {
		return this == InventoryDisplayModeEnum.INVENTORY || this == INFUSION || this == RECYCLING;
	}
}
