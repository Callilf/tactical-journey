/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings.basics;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.systems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Blessing of accuracy, increase accuracy.
 * @author Callil
 *
 */
public class BlessingAccuracy extends Blessing {
	
	@Override
	public String title() {
		return "Blessing of accuracy";
	}
	
	@Override
	public String description() {
		return "Increase melee and range accuracy by 1";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_accuracy;
	}

	@Override
	public void onReceive(Entity entity) {
		AttackComponent attackCompo = Mappers.attackComponent.get(entity);
		if (attackCompo != null) {
			attackCompo.increaseAccuracy(1);
		}
		
		PlayerComponent playerComponent = Mappers.playerComponent.get(entity);
		if (playerComponent != null && playerComponent.getSkillMelee() != null) {
			AttackComponent meleeAttackCompo = Mappers.attackComponent.get(playerComponent.getSkillMelee());
			if (meleeAttackCompo != null) {
				meleeAttackCompo.increaseAccuracy(1);
			}
		}
		if (playerComponent != null && playerComponent.getSkillRange() != null) {
			AttackComponent rangeAttackCompo = Mappers.attackComponent.get(playerComponent.getSkillRange());
			if (rangeAttackCompo != null) {
				rangeAttackCompo.increaseAccuracy(1);
			}
		}
		
		AlterationSystem.addAlterationProc(this);
	}
	

	@Override
	public void onRemove(Entity entity) {
		AttackComponent attackCompo = Mappers.attackComponent.get(entity);
		if (attackCompo != null) {
			attackCompo.increaseAccuracy(-1);
		}
		
		PlayerComponent playerComponent = Mappers.playerComponent.get(entity);
		if (playerComponent != null && playerComponent.getSkillMelee() != null) {
			AttackComponent meleeAttackCompo = Mappers.attackComponent.get(playerComponent.getSkillMelee());
			if (meleeAttackCompo != null) {
				meleeAttackCompo.increaseAccuracy(-1);
			}
		}
		if (playerComponent != null && playerComponent.getSkillRange() != null) {
			AttackComponent rangeAttackCompo = Mappers.attackComponent.get(playerComponent.getSkillRange());
			if (rangeAttackCompo != null) {
				rangeAttackCompo.increaseAccuracy(-1);
			}
		}

	}

}
