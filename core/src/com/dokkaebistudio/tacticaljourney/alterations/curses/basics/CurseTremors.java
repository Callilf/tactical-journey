/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.curses.basics;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.systems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Curse of tremors, reduce accuracy.
 * @author Callil
 *
 */
public class CurseTremors extends Curse {
	
	@Override
	public String title() {
		return "Curse of tremors";
	}
	
	@Override
	public String description() {
		return "Reduce melee and range accuracy by 1";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.curse_tremors;
	}

	@Override
	public void onReceive(Entity entity) {
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
	
		AlterationSystem.addAlterationProc(this);
	}
	

	@Override
	public void onRemove(Entity entity) {
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
	}

}
