package com.dokkaebistudio.tacticaljourney.enemies.pangolins;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.enemies.Enemy;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
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
	public void onReceiveDamage(int damage, Entity enemy, Entity attacker, Room room) {		
		if (!rolled && damage > 0) {
			rolled = true;
			turnRolledEnd = room.turnManager.getTurn() + 2;
			
			
			AnimationComponent animationCompo = Mappers.animationComponent.get(enemy);
			animationCompo.addAnimation(StatesEnum.STANDING, AnimationSingleton.getInstance().pangolinBabyRolled);
			animationCompo.addAnimation(StatesEnum.MOVING, AnimationSingleton.getInstance().pangolinBabyRolling);
			
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
				AnimationComponent animationCompo = Mappers.animationComponent.get(enemy);
				animationCompo.addAnimation(StatesEnum.STANDING, AnimationSingleton.getInstance().pangolinBabyStand);
				animationCompo.addAnimation(StatesEnum.MOVING, AnimationSingleton.getInstance().pangolinBabyStand);
				
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
