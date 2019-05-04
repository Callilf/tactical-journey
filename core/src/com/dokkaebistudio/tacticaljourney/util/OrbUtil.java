package com.dokkaebistudio.tacticaljourney.util;

import java.util.Optional;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.orbs.OrbComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;

public class OrbUtil {

	/**
	 * Check whether the given orb is in contact with an enemy or the player.
	 * @param orb the orb
	 * @param room the current room
	 */
	public static void checkContact(Entity orb, Room room) {
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(orb);

		Optional<Entity> player = TileUtil.getEntityWithComponentOnTile(gridPositionComponent.coord(), PlayerComponent.class, room);
		if (player.isPresent()) {
			OrbComponent orbComponent = Mappers.orbComponent.get(orb);
			orbComponent.onContact(orb, player.get(), room);
		} else {
			Optional<Entity> enemy = TileUtil.getEntityWithComponentOnTile(gridPositionComponent.coord(), EnemyComponent.class, room);
			if (enemy.isPresent() && Mappers.enemyComponent.get(enemy.get()).canActivateOrbs()) {
				OrbComponent orbComponent = Mappers.orbComponent.get(orb);
				orbComponent.onContact(orb, enemy.get(), room);
			} else {
				
				Set<Entity> otherOrbs = TileUtil.getEntitiesWithComponentOnTile(gridPositionComponent.coord(), OrbComponent.class, room);
				for (Entity otherOrb : otherOrbs) {
					if (otherOrb != null && otherOrb != orb) {
						OrbComponent orbComponent = Mappers.orbComponent.get(orb);
						orbComponent.onContactWithAnotherOrb(orb, otherOrb, room);
						break;
					}
				}
			}
		}
		
		
		
	}
}
