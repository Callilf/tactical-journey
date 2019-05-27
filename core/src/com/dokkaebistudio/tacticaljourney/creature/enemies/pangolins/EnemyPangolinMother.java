package com.dokkaebistudio.tacticaljourney.creature.enemies.pangolins;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.creature.Creature;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class EnemyPangolinMother extends Creature {

	private boolean crying;
	private int cryingEndTurn;
	
	
	@Override
	public String title() {
		return "Giant pangolin";
	}
	
	/**
	 * Becomes alerted and the textures changes to the enraged texture.
	 * @param entity
	 */
	public void enrage(Entity entity, Entity attacker) {
		AnimationComponent animationComponent = Mappers.animationComponent.get(entity);
		animationComponent.addAnimation(StatesEnum.STANDING, AnimationSingleton.getInstance().pangolinMotherEnragedStand);
		animationComponent.addAnimation(StatesEnum.MOVING, AnimationSingleton.getInstance().pangolinMotherEnragedStand);
		
		AIComponent aiComponent = Mappers.aiComponent.get(entity);
		aiComponent.setAlerted(true, entity, attacker);
		
		MoveComponent moveCompo = Mappers.moveComponent.get(entity);
		moveCompo.setMoveSpeed(4);
	}
	
	public void cry(Entity entity, Room room) {
		AnimationComponent animationComponent = Mappers.animationComponent.get(entity);
		animationComponent.addAnimation(StatesEnum.STANDING, AnimationSingleton.getInstance().pangolinMotherCrying);
		animationComponent.addAnimation(StatesEnum.MOVING, AnimationSingleton.getInstance().pangolinMotherCrying);
		
		AttackComponent attackComponent = Mappers.attackComponent.get(entity);
		attackComponent.setActive(false);

		MoveComponent moveCompo = Mappers.moveComponent.get(entity);
		moveCompo.setMoveSpeed(0);

		this.crying = true;
		this.cryingEndTurn = room.turnManager.getTurn() + 1;
	}
	
	
	@Override
	public void onReceiveDamage(int damage, Entity enemy, Entity attacker, Room room) {

		// Alert all babies when receiving a hit
		for(Entity e : room.getEnemies()) {
			AIComponent aiCompo = Mappers.aiComponent.get(e);
			Creature type = aiCompo.getType();
			if (type != null && type.getClass().equals(EnemyPangolinBaby.class)) {
				Mappers.aiComponent.get(e).setAlerted(true, e, attacker);
			}
		}
		
	}
	
	
	@Override
	public void onDeath(Entity enemy, Entity attacker, Room room) {
		// Remove the mother for all babies
		for(Entity e : room.getEnemies()) {
			AIComponent aiCompo = Mappers.aiComponent.get(e);
			Creature type = aiCompo.getType();
			if (type != null && type.getClass().equals(EnemyPangolinBaby.class)) {
				EnemyPangolinBaby baby = (EnemyPangolinBaby)aiCompo.getType();
				baby.setMother(null);
			}
		}
	}
	
	@Override
	public void onEndTurn(Entity enemy, Room room) {
		if (crying) {
			if (room.turnManager.getTurn() >= this.cryingEndTurn) {
				crying = false;
				AnimationComponent animationComponent = Mappers.animationComponent.get(enemy);
				animationComponent.addAnimation(StatesEnum.STANDING, AnimationSingleton.getInstance().pangolinMotherStand);
				animationComponent.addAnimation(StatesEnum.MOVING, AnimationSingleton.getInstance().pangolinMotherStand);
				
				AttackComponent attackComponent = Mappers.attackComponent.get(enemy);
				attackComponent.setActive(true);

				MoveComponent moveCompo = Mappers.moveComponent.get(enemy);
				moveCompo.setMoveSpeed(3);
			}
		}
	}

}
