package com.dokkaebistudio.tacticaljourney.ai.movements;

import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public enum AttackTypeEnum {

	MELEE,
	RANGE,
	THROW,
	EXPLOSION;
	
	
	public boolean canAttack(Entity tile, Room room) {
		boolean result = false;
		
		TileComponent tileComponent = Mappers.tileComponent.get(tile);
		switch(this) {
		case MELEE:
			result = true;
			break;
			
		case RANGE:
			result = true;
			break;
			
		case THROW:
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(tile);
			Set<Entity> solids = TileUtil.getEntitiesWithComponentOnTile(gridPositionComponent.coord(), SolidComponent.class, room);
			result = tileComponent.type.isThrowable() && solids.isEmpty();
			break;
			
		case EXPLOSION:
			result = true;
			break;
			default:
		}
		
		return result;
	}
	
}
