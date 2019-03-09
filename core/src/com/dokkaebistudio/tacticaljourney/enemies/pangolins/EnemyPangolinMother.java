package com.dokkaebistudio.tacticaljourney.enemies.pangolins;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.enemies.Enemy;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class EnemyPangolinMother extends Enemy {

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
	public void enrage(Entity entity) {
		StateComponent stateComponent = Mappers.stateComponent.get(entity);
		stateComponent.set(StatesEnum.PANGOLIN_MOTHER_ENRAGED_STAND.getState());
		
		EnemyComponent enemyComponent = Mappers.enemyComponent.get(entity);
		enemyComponent.setAlerted(true);
		
		MoveComponent moveCompo = Mappers.moveComponent.get(entity);
		moveCompo.setMoveSpeed(4);
	}
	
	public void cry(Entity entity, Room room) {
		StateComponent stateComponent = Mappers.stateComponent.get(entity);
		stateComponent.set(StatesEnum.PANGOLIN_MOTHER_CRYING.getState());
		
		AttackComponent attackComponent = Mappers.attackComponent.get(entity);
		attackComponent.setActive(false);

		MoveComponent moveCompo = Mappers.moveComponent.get(entity);
		moveCompo.setMoveSpeed(0);

		this.crying = true;
		this.cryingEndTurn = room.turnManager.getTurn() + 1;
	}
	
	
	@Override
	public void onReceiveDamage(Entity enemy, Entity attacker, Room room) {

		// Alert all babies when receiving a hit
		for(Entity e : room.getEnemies()) {
			EnemyComponent enemyComponent = Mappers.enemyComponent.get(e);
			Enemy type = enemyComponent.getType();
			if (type != null && type.getClass().equals(EnemyPangolinBaby.class)) {
				enemyComponent.setAlerted(true);
			}
		}
		
	}
	
	
	@Override
	public void onDeath(Entity enemy, Entity attacker, Room room) {
		// Remove the mother for all babies
		for(Entity e : room.getEnemies()) {
			EnemyComponent enemyComponent = Mappers.enemyComponent.get(e);
			Enemy type = enemyComponent.getType();
			if (type != null && type.getClass().equals(EnemyPangolinBaby.class)) {
				EnemyPangolinBaby baby = (EnemyPangolinBaby)enemyComponent.getType();
				baby.setMother(null);
			}
		}
	}
	
	@Override
	public void onEndTurn(Entity enemy, Room room) {
		if (crying) {
			if (room.turnManager.getTurn() == this.cryingEndTurn) {
				crying = false;
				StateComponent stateComponent = Mappers.stateComponent.get(enemy);
				stateComponent.set(StatesEnum.PANGOLIN_MOTHER_STAND.getState());
				
				AttackComponent attackComponent = Mappers.attackComponent.get(enemy);
				attackComponent.setActive(true);

				MoveComponent moveCompo = Mappers.moveComponent.get(enemy);
				moveCompo.setMoveSpeed(3);
			}
		}
	}

}
