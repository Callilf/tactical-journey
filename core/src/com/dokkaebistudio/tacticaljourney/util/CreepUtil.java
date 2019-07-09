/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.util;

import java.util.Optional;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.ces.components.BlockExplosionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.creeps.Creep.CreepType;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;

/**
 * @author Callil
 *
 */
public class CreepUtil {
	
	public static boolean canSpawnWeb(Vector2 pos, Room room) {
		boolean canSpawnWeb = true;

		Tile tile = TileUtil.getTileAtGridPos(pos, room);
		canSpawnWeb = tile.isUnblockedGround(null);
		
		return canSpawnWeb;
	}

	public static boolean canCatchFire(Vector2 pos, Room room) {
		boolean canCatchFire = true;
		
		if (pos.x < 0 || pos.x >= GameScreen.GRID_W || pos.y < 0 || pos.y >= GameScreen.GRID_H) {
			return false;
		}
		
		Tile tile = TileUtil.getTileAtGridPos(pos, room);
		canCatchFire = tile.isUnblockedGround(null);
		
		if (canCatchFire) {
			Optional<Entity> creepAlreadyThere = TileUtil.getEntityWithComponentOnTile(pos, CreepComponent.class, room);
			if (creepAlreadyThere.isPresent()) {
				CreepComponent creepComponent = Mappers.creepComponent.get(creepAlreadyThere.get());
				if (creepComponent.getType().getType() == CreepType.FIRE) {
					//There is already fire on this tile, do nothing
					canCatchFire = false;
				}
			}
		}
		
		if (canCatchFire) {
			Optional<Entity> wall = TileUtil.getEntityWithComponentOnTile(pos, BlockExplosionComponent.class, room);
			if (wall.isPresent()) {
				canCatchFire = false;
			}
		}
		
		return canCatchFire;
	}
}
