package com.dokkaebistudio.tacticaljourney.systems.entitysystems.creatures.subsystems.pangolins;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.creature.enemies.pangolins.EnemyPangolinMother;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.entitysystems.creatures.CreatureSystem;
import com.dokkaebistudio.tacticaljourney.systems.entitysystems.creatures.subsystems.CreatureSubSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class PangolinMotherSubSystem extends CreatureSubSystem {
	

	
	@Override
	public boolean update(final CreatureSystem creatureSystem, final Entity enemy, final Room room) {		
		
		AIComponent aiCompo = Mappers.aiComponent.get(enemy);
		EnemyPangolinMother pangolinType = (EnemyPangolinMother) aiCompo.getType();
		StateComponent stateComponent = Mappers.stateComponent.get(enemy);

		
		switch(room.getState()) {

			
			default:
		
		}
		
		return false;
	}

	
}
