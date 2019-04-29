package com.dokkaebistudio.tacticaljourney.systems.enemies.pangolins;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.creature.enemies.pangolins.EnemyPangolinMother;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.EnemySystem;
import com.dokkaebistudio.tacticaljourney.systems.enemies.EnemySubSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class PangolinMotherSubSystem extends EnemySubSystem {
	

	
	@Override
	public boolean update(final EnemySystem enemySystem, final Entity enemy, final Room room) {		
		
		AIComponent aiCompo = Mappers.aiComponent.get(enemy);
		EnemyPangolinMother pangolinType = (EnemyPangolinMother) aiCompo.getType();
		StateComponent stateComponent = Mappers.stateComponent.get(enemy);

		
		switch(room.getState()) {

			
			default:
		
		}
		
		return false;
	}

	
}
