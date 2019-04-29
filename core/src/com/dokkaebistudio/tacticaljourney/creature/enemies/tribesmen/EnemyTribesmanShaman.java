package com.dokkaebistudio.tacticaljourney.creature.enemies.tribesmen;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.creature.Creature;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class EnemyTribesmanShaman extends Creature {

	
	@Override
	public String title() {
		return Descriptions.ENEMY_TRIBESMAN_SHAMAN_TITLE;
	}

	
	@Override
	public void onDeath(Entity enemy, Entity attacker, Room room) {
		
		// Remove all totems
		List<Entity> totems = new ArrayList<>();
		for (Entity e : room.getEnemies()) {
			if (Mappers.aiComponent.get(e).getType() instanceof EnemyTribesmanTotem) {
				totems.add(e);
			}
		}
		
		for (Entity e : totems) {
			room.removeEntity(e);
		}
	}
	
	

	
}
