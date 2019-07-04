package com.dokkaebistudio.tacticaljourney.creature.enemies;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.creature.Creature;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class EnemyScorpion extends Creature {


	@Override
	public String title() {
		return "Scorpion";
	}
	
	
	@Override
	public void onLoseTarget(Entity creature, Room room) {
		super.onLoseTarget(creature, room);
		
		Mappers.aiComponent.get(creature).setAlerted(true, creature, GameScreen.player);
	}
	
}
