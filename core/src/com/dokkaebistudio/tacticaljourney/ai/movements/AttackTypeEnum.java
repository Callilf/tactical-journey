package com.dokkaebistudio.tacticaljourney.ai.movements;

import com.dokkaebistudio.tacticaljourney.components.TileComponent.TileEnum;

public enum AttackTypeEnum {

	MELEE,
	RANGE,
	THROW;
	
	
	public boolean canAttack(TileEnum tileType) {
		boolean result = false;
		
		switch(this) {
		case MELEE:
			result = true;
			break;
			
		case RANGE:
			result = true;
			break;
			
		case THROW:
			result = tileType.isWalkable();
			break;
			default:
		}
		
		return result;
	}
	
}
