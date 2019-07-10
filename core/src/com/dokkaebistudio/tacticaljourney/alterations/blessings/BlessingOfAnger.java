/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.ces.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class BlessingOfAnger extends Blessing {
	
	@Override
	public String title() {
		return "Blessing of anger";
	}
	
	@Override
	public String description() {
		return "Increase strength by 1 and add 1 [OLIVE]knockback[] on melee attacks";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_anger;
	}

	@Override
	public void onReceive(Entity entity) {
		AttackComponent attackCompo = Mappers.attackComponent.get(entity);
		
		if (attackCompo != null) {
			attackCompo.increaseStrength(1);
			attackCompo.increaseKnockback(1);
		}
		
		PlayerComponent playerComponent = Mappers.playerComponent.get(entity);
		if (playerComponent != null && playerComponent.getSkillMelee() != null) {
			AttackComponent meleeAttackCompo = Mappers.attackComponent.get(playerComponent.getSkillMelee());
			if (meleeAttackCompo != null) {
				meleeAttackCompo.increaseKnockback(1);
			}
		}
		
		AlterationSystem.addAlterationProc(this);
	}

	@Override
	public void onRemove(Entity entity) {
		AttackComponent attackCompo = Mappers.attackComponent.get(entity);
		
		if (attackCompo != null) {
			attackCompo.increaseStrength(-1);
			attackCompo.increaseKnockback(-1);
		}
		
		PlayerComponent playerComponent = Mappers.playerComponent.get(entity);
		if (playerComponent != null && playerComponent.getSkillMelee() != null) {
			AttackComponent meleeAttackCompo = Mappers.attackComponent.get(playerComponent.getSkillMelee());
			if (meleeAttackCompo != null) {
				meleeAttackCompo.increaseKnockback(-1);
			}
		}
	}

}
