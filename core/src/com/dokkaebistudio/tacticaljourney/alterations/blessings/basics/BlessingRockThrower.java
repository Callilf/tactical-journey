/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings.basics;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.systems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Blessing of the rock thrower.
 * @author Callil
 *
 */
public class BlessingRockThrower extends Blessing {
	
	@Override
	public String title() {
		return "Blessing of the rock thrower";
	}
	
	@Override
	public String description() {
		return "Increase item throw range by 1";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_rock_thrower;
	}

	@Override
	public void onReceive(Entity entity) {
		PlayerComponent playerComponent = Mappers.playerComponent.get(entity);
		if (playerComponent != null && playerComponent.getSkillThrow() != null) {
			AttackComponent rangeAttackCompo = Mappers.attackComponent.get(playerComponent.getSkillThrow());
			if (rangeAttackCompo != null) {
				rangeAttackCompo.increaseRangeMax(1);
			}
		}
		
		AlterationSystem.addAlterationProc(this);
	}
	

	@Override
	public void onRemove(Entity entity) {		
		PlayerComponent playerComponent = Mappers.playerComponent.get(entity);
		if (playerComponent != null && playerComponent.getSkillThrow() != null) {
			AttackComponent rangeAttackCompo = Mappers.attackComponent.get(playerComponent.getSkillThrow());
			if (rangeAttackCompo != null) {
				rangeAttackCompo.increaseRangeMax(-1);
			}
		}
	}

}
