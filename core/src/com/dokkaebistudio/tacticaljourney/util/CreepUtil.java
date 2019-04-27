/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.util;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.BlockExplosionComponent;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.creeps.Creep.CreepType;
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
		canSpawnWeb = tile.isThrowable(null);
		
		return canSpawnWeb;
	}

	public static boolean canCatchFire(Vector2 pos, Room room) {
		boolean canCatchFire = true;
		
		if (pos.x < 0 || pos.x >= GameScreen.GRID_W || pos.y < 0 || pos.y >= GameScreen.GRID_H) {
			return false;
		}
		
		Tile tile = TileUtil.getTileAtGridPos(pos, room);
		canCatchFire = tile.isThrowable(null);
		
		if (canCatchFire) {
			Entity creepAlreadyThere = TileUtil.getEntityWithComponentOnTile(pos, CreepComponent.class, room);
			if (creepAlreadyThere != null) {
				CreepComponent creepComponent = Mappers.creepComponent.get(creepAlreadyThere);
				if (creepComponent.getType().getType() == CreepType.FIRE) {
					//There is already fire on this tile, do nothing
					canCatchFire = false;
				}
			}
		}
		
		if (canCatchFire) {
			Entity wall = TileUtil.getEntityWithComponentOnTile(pos, BlockExplosionComponent.class, room);
			if (wall != null) {
				canCatchFire = false;
			}
		}
		
		return canCatchFire;
	}
}
