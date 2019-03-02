package com.dokkaebistudio.tacticaljourney.enemies.pangolins;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.enemies.Enemy;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class EnemyPangolinBaby extends Enemy {

	private boolean rolled = false;
	private int turnsRolled = 0;
	
	@Override
	public void onReceiveDamage(Entity enemy, Entity attacker, Room room) {
		if (!rolled) {
			rolled = true;
			turnsRolled = 0;
			
			StateComponent stateComponent = Mappers.stateComponent.get(enemy);
			stateComponent.set(StatesEnum.PANGOLIN_BABY_ROLLED.getState());
			
			HealthComponent healthComponent = Mappers.healthComponent.get(enemy);
			healthComponent.restoreArmor(20);
			
			MoveComponent moveComponent = Mappers.moveComponent.get(enemy);
			moveComponent.moveSpeed ++;
		}
	}
	
	
	@Override
	public void onStartTurn(Entity enemy, Room room) {
		if (rolled) {
			if (turnsRolled >= 3) {
				rolled = false;
				StateComponent stateComponent = Mappers.stateComponent.get(enemy);
				stateComponent.set(StatesEnum.PANGOLIN_BABY_STAND.getState());
				
				HealthComponent healthComponent = Mappers.healthComponent.get(enemy);
				if (healthComponent.getArmor() > 0) {
					healthComponent.hit(healthComponent.getArmor(), null);
				}
				
				MoveComponent moveComponent = Mappers.moveComponent.get(enemy);
				moveComponent.moveSpeed --;
			} else {
				turnsRolled ++;
			}
		}
	}
	
	
	public boolean isRolled() {
		return rolled;
	}
}
