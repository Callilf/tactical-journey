/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.WheelComponent.Sector;
import com.dokkaebistudio.tacticaljourney.components.display.DamageDisplayComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TransformComponent;
import com.dokkaebistudio.tacticaljourney.systems.display.RenderingSystem;

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
	private final ComponentMapper<GridPositionComponent> gridPosCompoCM;
	
	private Room room;
	
	/**
	 * Constructor.
	 * @param room the room
	 */
	public AttackManager(Room room) {
        this.attackCM = ComponentMapper.getFor(AttackComponent.class);
        this.healthCM = ComponentMapper.getFor(HealthComponent.class);
        this.gridPosCompoCM = ComponentMapper.getFor(GridPositionComponent.class);
        this.room = room;
	}
	
	
	/**
	 * Perform an attack from the attacker on the target.
	 * @param attacker the attacker entity
	 * @param target the target entity
	 */
	public void performAttack(Entity attacker, Entity target) {
		this.performAttack(attacker, target, null);
	}
	
	/**
	 * Perform an attack from the attacker on the target.
	 * @param attacker the attacker entity
	 * @param target the target entity
	 * @param pointedSector the sector pointed by the arrow (if the player attacks)
	 */
	public void performAttack(Entity attacker, Entity target, Sector pointedSector) {
		AttackComponent attackCompo = attackCM.get(attacker);
		int damage = 0;
		
		//Compute damage
		if (pointedSector != null) {
			switch (pointedSector.hit) {
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
		} else {
			damage = attackCompo.getStrength();
		}
		
		
		HealthComponent healthComponent = healthCM.get(target);
		healthComponent.setHp(healthComponent.getHp() - damage);
		
		if (healthComponent.getHp() <= 0) {
			//target is dead
			room.engine.removeEntity(target);
			//TODO: play death animation
		}
		
		
		//Add a damage displayer
		GridPositionComponent targetGridPos = gridPosCompoCM.get(target);
		room.entityFactory.createDamageDisplayer(String.valueOf(damage), targetGridPos.coord, false);
	}
	
	
}
