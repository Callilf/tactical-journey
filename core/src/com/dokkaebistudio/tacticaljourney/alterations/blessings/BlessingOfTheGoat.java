/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.wheel.Sector;

/**
 * Blessing of the goat.
 * @author Callil
 *
 */
public class BlessingOfTheGoat extends Blessing {
	
	@Override
	public String title() {
		return "Blessing of the goat";
	}
	
	@Override
	public String description() {
		return "Increase melee and bow accuracy by 3";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_goat;
	}

	@Override
	public void onReceive(Entity entity) {
		AttackComponent attackCompo = Mappers.attackComponent.get(entity);
		if (attackCompo != null) {
			attackCompo.increaseAccuracy(3);
		}
		
		PlayerComponent playerComponent = Mappers.playerComponent.get(entity);
		if (playerComponent != null && playerComponent.getSkillMelee() != null) {
			AttackComponent meleeAttackCompo = Mappers.attackComponent.get(playerComponent.getSkillMelee());
			if (meleeAttackCompo != null) {
				meleeAttackCompo.increaseAccuracy(3);
			}
		}
		if (playerComponent != null && playerComponent.getSkillRange() != null) {
			AttackComponent rangeAttackCompo = Mappers.attackComponent.get(playerComponent.getSkillRange());
			if (rangeAttackCompo != null) {
				rangeAttackCompo.increaseAccuracy(3);
			}
		}
		
		AlterationSystem.addAlterationProc(this);
	}
	

	@Override
	public void onRemove(Entity entity) {
		AttackComponent attackCompo = Mappers.attackComponent.get(entity);
		if (attackCompo != null) {
			attackCompo.increaseAccuracy(-3);
		}
		
		PlayerComponent playerComponent = Mappers.playerComponent.get(entity);
		if (playerComponent != null && playerComponent.getSkillMelee() != null) {
			AttackComponent meleeAttackCompo = Mappers.attackComponent.get(playerComponent.getSkillMelee());
			if (meleeAttackCompo != null) {
				meleeAttackCompo.increaseAccuracy(-3);
			}
		}
		if (playerComponent != null && playerComponent.getSkillRange() != null) {
			AttackComponent rangeAttackCompo = Mappers.attackComponent.get(playerComponent.getSkillRange());
			if (rangeAttackCompo != null) {
				rangeAttackCompo.increaseAccuracy(-3);
			}
		}
	}

}
