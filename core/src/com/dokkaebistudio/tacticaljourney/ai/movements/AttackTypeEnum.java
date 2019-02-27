package com.dokkaebistudio.tacticaljourney.ai.movements;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;

public enum AttackTypeEnum {

	MELEE,
	RANGE,
	THROW,
	EXPLOSION;
	
	
	public boolean canAttack(Tile tile, Entity attacker, Room room) {
		boolean result = false;
		
		switch(this) {
		case MELEE:
			result = true;
			break;
			
		case RANGE:
			result = true;
			break;
			
		case THROW:
			result = tile.isThrowable(attacker);
			break;
			
		case EXPLOSION:
			result = true;
			break;
			default:
		}
		
		return result;
	}
	
}
