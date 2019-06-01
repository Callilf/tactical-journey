package com.dokkaebistudio.tacticaljourney.creature.enemies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.creature.Creature;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class EnemySmallOrangutan extends Creature {
	
	private List<Entity> bananas = new ArrayList<>();
		
	public EnemySmallOrangutan() {}

	@Override
	public String title() {
		return Descriptions.ENEMY_ORANGUTAN_TITLE;
	}
	
	@Override
	public void onReceiveDamage(int damage, Entity creature, Entity attacker, Room room) {
		if (damage > 0) {
			// When taking damage, drops 3 banana peels on close tiles that doesn't already have a banana peel
			GridPositionComponent orangutanPos = Mappers.gridPositionComponent.get(creature);
			List<Tile> tilesAtProximity = TileUtil.getTilesAtProximity(orangutanPos.coord(), 3, room);
			Collections.shuffle(tilesAtProximity, RandomSingleton.getInstance().getUnseededRandom());
			
			tilesAtProximity.parallelStream()
				.filter(t -> t.isWalkable() && !EnemyOrangutan.hasBanana(t, room))
				.limit(2)
				.forEachOrdered(t -> EnemyOrangutan.throwBananas(orangutanPos.coord(), t, room));
		}
	}
	
	
	
	// Getters and Setters	

	public List<Entity> getBananas() {
		return bananas;
	}
}
