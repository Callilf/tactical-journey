package com.dokkaebistudio.tacticaljourney.util;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.room.Room;

public class AIUtil {

	public static Entity findClosestTarget(Vector2 pos, Room room) {
		int shortestDistance = -1;
		Entity target = null;
		for(Entity enemy : room.getEnemies()) {
			int dist = TileUtil.getDistanceBetweenTiles(Mappers.gridPositionComponent.get(enemy).coord(), pos);
			if (target == null || dist < shortestDistance) {
				shortestDistance = dist;
				target = enemy;
				if (shortestDistance == 1) break;
			}
		}
		return target;
	}
	
}
