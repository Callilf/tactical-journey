package com.dokkaebistudio.tacticaljourney.enemies.pangolins;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.enemies.Enemy;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class EnemyPangolinBaby extends Enemy {

	private boolean rolled = false;
	private int turnRolledEnd = 0;
	private Entity mother;
	
	
	public EnemyPangolinBaby() {}
	
	public EnemyPangolinBaby(Entity mother) {
		this.mother = mother;
	}
	
	
	@Override
	public String title() {
		return "Baby pangolin";
	}
	
	@Override
	public void onReceiveDamage(Entity enemy, Entity attacker, Room room) {
		if (!rolled) {
			rolled = true;
			turnRolledEnd = room.turnManager.getTurn() + 2;
			
			StateComponent stateComponent = Mappers.stateComponent.get(enemy);
			stateComponent.set(StatesEnum.PANGOLIN_BABY_ROLLED.getState());
			
			HealthComponent healthComponent = Mappers.healthComponent.get(enemy);
			healthComponent.restoreArmor(20);
			
			MoveComponent moveComponent = Mappers.moveComponent.get(enemy);
			moveComponent.setMoveSpeed(moveComponent.getMoveSpeed() + 1);
		}
		
		
		if (mother != null) {
			EnemyComponent motherEnemyCompo = Mappers.enemyComponent.get(mother);
			EnemyPangolinMother motherType = (EnemyPangolinMother)motherEnemyCompo.getType();
			motherType.enrage(mother);
		}
	}
	
	
	@Override
	public void onDeath(Entity enemy, Entity attacker, Room room) {
		if (mother != null) {
			EnemyComponent motherEnemyCompo = Mappers.enemyComponent.get(mother);
			EnemyPangolinMother motherType = (EnemyPangolinMother)motherEnemyCompo.getType();
			motherType.cry(mother, room);
		}
	}
	

	
	@Override
	public void onEndTurn(Entity enemy, Room room) {
		if (rolled) {
			if (room.turnManager.getTurn() == this.turnRolledEnd) {
				rolled = false;
				StateComponent stateComponent = Mappers.stateComponent.get(enemy);
				stateComponent.set(StatesEnum.PANGOLIN_BABY_STAND.getState());
				
				HealthComponent healthComponent = Mappers.healthComponent.get(enemy);
				if (healthComponent.getArmor() > 0) {
					healthComponent.hit(healthComponent.getArmor(), enemy,null);
				}
				
				MoveComponent moveComponent = Mappers.moveComponent.get(enemy);
				moveComponent.setMoveSpeed(moveComponent.getMoveSpeed() - 1);
			}
		}
	}
	
	
	public boolean isRolled() {
		return rolled;
	}
	
	public void setMother(Entity e) {
		this.mother = e;
	}
}
