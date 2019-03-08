/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.room.managers;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.enums.DamageType;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffPoison;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.wheel.Sector;

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
	
	/** The last pointed sector on the wheel. */
	private Sector lastPointedSector;
	
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
		this.lastPointedSector = pointedSector;

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
			case POISON:
				
				// Inflict poison
				StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(target);
				if (statusReceiverComponent != null) {
					EnemyComponent enemyComponent = Mappers.enemyComponent.get(target);
					if (enemyComponent != null) {
						Journal.addEntry("[GRAY]You inflicted [PURPLE]poison[GRAY] to " + enemyComponent.getType().title());
					}
					statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffPoison(5, attacker));
				}
				return;
				
			default:
			}
		} else {
			damage = attackCompo.getStrength();
		}
		
		applyDamage(attacker, target, damage, DamageType.NORMAL);
	}
	
	
	/**
	 * Deal 'damage' damages to 'target'.
	 * @param target the target entity
	 * @param damage the amount of damage
	 */
	public void applyDamageWithoutAttacker(Entity target, int damage, DamageType damageType) {
		this.applyDamage(null, target, damage, damageType);
	}
	
	/**
	 * 'attacker' deals 'damage' damages to 'target'
	 * @param attacker the attacker entity
	 * @param target the target entity
	 * @param damage the amount of damage
	 */
	public void applyDamage(Entity attacker, Entity target, int damage, DamageType damageType) {
		HealthComponent healthComponent = Mappers.healthComponent.get(target);
		if (healthComponent != null) {
			healthComponent.hit(damage, target, attacker, damageType);
		}
		
		EnemyComponent enemyComponent = Mappers.enemyComponent.get(target);
		if (enemyComponent != null) {
			enemyComponent.onReceiveDamage(target, attacker, room);
		}
		AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(target);
		if (alterationReceiverComponent != null) {
			alterationReceiverComponent.onReceiveDamage(target, attacker, room);
		}
		
		if (attacker != null) {
			enemyComponent = Mappers.enemyComponent.get(attacker);
			if (enemyComponent != null) {
				enemyComponent.onAttack(attacker, target, room);
			}
			alterationReceiverComponent = Mappers.alterationReceiverComponent.get(attacker);
			if (alterationReceiverComponent != null) {
				alterationReceiverComponent.onAttack(attacker, target, this.lastPointedSector, room);
			}
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

	
}
