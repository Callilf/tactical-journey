package com.dokkaebistudio.tacticaljourney.systems.enemies.pangolins;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.enemies.pangolins.EnemyPangolinBaby;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.EnemySystem;
import com.dokkaebistudio.tacticaljourney.systems.enemies.EnemySubSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class PangolinBabySubSystem extends EnemySubSystem {
	

	
	@Override
	public boolean update(final EnemySystem enemySystem, final Entity enemy, final Room room) {		
		
		EnemyComponent enemyComponent = Mappers.enemyComponent.get(enemy);
		EnemyPangolinBaby pangolinType = (EnemyPangolinBaby) enemyComponent.getType();
		StateComponent stateComponent = Mappers.stateComponent.get(enemy);

		
		switch(room.getState()) {

		case ENEMY_MOVE_DESTINATION_SELECTED:
			if (pangolinType.isRolled()) {
				stateComponent.set(StatesEnum.PANGOLIN_BABY_ROLLING.getState());
			}

			break;
		case ENEMY_END_MOVEMENT:
			if (pangolinType.isRolled()) {
				stateComponent.set(StatesEnum.PANGOLIN_BABY_ROLLED.getState());
			}
			break;
			
			default:
		
		}
		
		return false;
	}

	
}
