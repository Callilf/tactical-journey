/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.WheelComponent.Sector;

/**
 * Manage the attacks between entities, checks whether the attack lands or fails,
 * compute the amount of damages, check whether entities are killed and check whether
 * there are still enemies in the room.
 * @author Callil
 *
 */
public class AttackManager {


	private final ComponentMapper<AttackComponent> attackCM;
	private final ComponentMapper<HealthComponent> healthCM;
	
	private Room room;
	
	/**
	 * Constructor.
	 * @param room the room
	 */
	public AttackManager(Room room) {
        this.attackCM = ComponentMapper.getFor(AttackComponent.class);
        this.healthCM = ComponentMapper.getFor(HealthComponent.class);
        this.room = room;
	}
	
	
	/**
	 * Perform an attack from the attacker on the target.
	 * @param attacker the attacker entity
	 * @param target the target entity
	 */
	public void performAttack(Entity attacker, Entity target, Sector pointedSector) {
		AttackComponent attackCompo = attackCM.get(attacker);
		int damage = 0;
		
		//Compute damage
		switch(pointedSector.hit) {
		case HIT:
			damage = attackCompo.getStrength();
			break;
		case GRAZE:
			damage = attackCompo.getStrength() / 2;
			break;
		case MISS:
			damage = 0;
			break;
		case CRITICAL:
			damage = attackCompo.getStrength() * 2;
			break;
		default:
		}
		
		
		HealthComponent healthComponent = healthCM.get(target);
		healthComponent.setHp(healthComponent.getHp() - damage);
		
		if (healthComponent.getHp() <= 0) {
			//target is dead
			room.engine.removeEntity(target);
			//TODO: play death animation
		}
	}
	
	
}
