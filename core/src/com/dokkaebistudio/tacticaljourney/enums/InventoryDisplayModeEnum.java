package com.dokkaebistudio.tacticaljourney.enums;

public enum InventoryDisplayModeEnum {

	NONE,
	INVENTORY,
	INFUSION,
	LOOT,
	DEBUG;
	
	
	public boolean isInventoryPopin() {
		return this == InventoryDisplayModeEnum.INVENTORY || this == INFUSION;
	}
}
