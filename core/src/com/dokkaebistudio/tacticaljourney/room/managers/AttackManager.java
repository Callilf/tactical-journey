/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.managers;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.ExpRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ExperienceComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ParentEntityComponent;
import com.dokkaebistudio.tacticaljourney.components.player.WheelComponent.Sector;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.ComponentsUtil;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Manage the attacks between entities, checks whether the attack lands or fails,
 * compute the amount of damages, check whether entities are killed and check whether
 * there are still enemies in the room.
 * @author Callil
 *
 */
public class AttackManager {
	
	/** The current room. */
	private Room room;
	
	/**
	 * Constructor.
	 * @param room the room
	 */
	public AttackManager(Room room) {
        this.room = room;
	}
	
	
	/**
	 * Perform an attack from the attacker on the target.
	 * @param attacker the attacker entity
	 * @param target the target entity
	 */
	public void performAttack(Entity attacker, AttackComponent attackCompo) {
		this.performAttack(attacker, attackCompo, null);
	}
	
	/**
	 * Perform an attack from the attacker on the target.
	 * @param attacker the attacker entity
	 * @param target the target entity
	 * @param pointedSector the sector pointed by the arrow (if the player attacks)
	 */
	public void performAttack(Entity attacker, AttackComponent attackCompo, Sector pointedSector) {
		AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(attacker);
		if (ammoCarrierComponent != null) {
			ammoCarrierComponent.useAmmo(attackCompo.getAmmoType(), attackCompo.getAmmosUsedPerAttack());
		}
		
		Entity target = attackCompo.getTarget();
		if (target == null) {
			//Attacked an empty tiled... XD
			return;
		}
		
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
		
		applyDamage(attacker, target, damage);
	}
	
	
	/**
	 * 'attacker' deals 'damage' damages to 'target'
	 * @param attacker the attacker entity
	 * @param target the target entity
	 * @param damage the amount of damage
	 */
	public void applyDamage(Entity attacker, Entity target, int damage) {
		HealthComponent healthComponent = Mappers.healthComponent.get(target);
		
		if (healthComponent != null) {
			healthComponent.setHp(healthComponent.getHp() - damage);
			
			if (healthComponent.getHp() <= 0) {
				//target is dead
				
				//earn xp
				ExperienceComponent expCompo = getExperienceComponent(attacker);
				ExpRewardComponent expRewardCompo = Mappers.expRewardComponent.get(target);
				if (expCompo != null && expRewardCompo != null) {
					expCompo.earnXp(expRewardCompo.getExpGain());
					
					Entity mainParent = ComponentsUtil.getMainParent(attacker);
					GridPositionComponent attackerPosCompo = Mappers.gridPositionComponent.get(mainParent);
					room.entityFactory.createExpDisplayer(expRewardCompo.getExpGain(), attackerPosCompo.coord());
				}
				
				room.removeEnemy(target);
				//TODO: play death animation
			}
			
			
			//Add a damage displayer
			GridPositionComponent targetGridPos = Mappers.gridPositionComponent.get(target);
			room.entityFactory.createDamageDisplayer(String.valueOf(damage), targetGridPos.coord(), false);
		}
	}
	
	
	/**
	 * Return true if this attack component can attack. i.e if it still has ammos.
	 * @param attackCompo the attack component
	 * @return true if it is possible to attack with this attack component
	 */
	public boolean isAttackAllowed(Entity attacker, AttackComponent attackCompo) {
		AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(attacker);
		return ammoCarrierComponent.canUseAmmo(attackCompo.getAmmoType(), attackCompo.getAmmosUsedPerAttack());
	}
	
	private ExperienceComponent getExperienceComponent(Entity attacker) {
		ExperienceComponent result = null;
		result = Mappers.experienceComponent.get(attacker);
		if (result == null) {
			ParentEntityComponent parentEntityComponent = Mappers.parentEntityComponent.get(attacker);
			if (parentEntityComponent != null) {
				Entity parent = parentEntityComponent.getParent();
				result = getExperienceComponent(parent);
			}
		}
		
		return result;
	}
	
}
