package com.dokkaebistudio.tacticaljourney.util;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.components.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;

public final class TileUtil {
	
	private final static ComponentMapper<GridPositionComponent> gridPositionM = ComponentMapper
			.getFor(GridPositionComponent.class);;

	private TileUtil() {}
	
	/**
	 * Return the solid entity standing on the tile at the given position.
	 * @param position the position
	 * @param engine the engine
	 * @return The entity standing at this position, null if no entity there.
	 */
	public static Entity getSolidEntityOnTile(Vector2 position, PooledEngine engine) {
		Family family = Family.all(SolidComponent.class, GridPositionComponent.class).get();
		
		ImmutableArray<Entity> allSolids = engine.getEntitiesFor(family);
		for (Entity solid : allSolids) {
			GridPositionComponent gridPositionComponent = gridPositionM.get(solid);
			if (gridPositionComponent.coord.x == position.x && gridPositionComponent.coord.y == position.y) {
				return solid;
			}
		}
		
		return null;
	}
	
	/**
	 * Return the attackable entity standing on the tile at the given position.
	 * @param position the position
	 * @param engine the engine
	 * @return The entity standing at this position, null if no entity there.
	 */
	public static Entity getAttackableEntityOnTile(Vector2 position, PooledEngine engine) {
		Family family = Family.all(HealthComponent.class, GridPositionComponent.class).get();
		
		ImmutableArray<Entity> allAttackables = engine.getEntitiesFor(family);
		for (Entity attackableEntity : allAttackables) {
			GridPositionComponent gridPositionComponent = gridPositionM.get(attackableEntity);
			if (gridPositionComponent.coord.x == position.x && gridPositionComponent.coord.y == position.y) {
				return attackableEntity;
			}
		}
		
		return null;
	}
	
	/**
	 * Return the distance in tiles between two tiles. The distance is always positive.
	 * @param startTilePos the start tile position
	 * @param endTilePos the end tile position
	 * @return the distance between startTile and endTile
	 */
	public static int getDistanceBetweenTiles(Vector2 startTilePos, Vector2 endTilePos) {
		float xDistance = Math.abs(startTilePos.x - endTilePos.x);
		float yDistance = Math.abs(startTilePos.y - endTilePos.y);
		return (int) xDistance + (int) yDistance;
	}
}
