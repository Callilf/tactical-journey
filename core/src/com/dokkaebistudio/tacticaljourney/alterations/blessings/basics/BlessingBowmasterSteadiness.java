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
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Blessing of bowmaster's steadiness.
 * @author Callil
 *
 */
public class BlessingBowmasterSteadiness extends Blessing {
	
	@Override
	public String title() {
		return "Blessing of the Bowmaster's steadiness";
	}
	
	@Override
	public String description() {
		return "Increase bow damage by 1";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_bowmaster_steadiness;
	}

	@Override
	public void onReceive(Entity entity) {
		PlayerComponent playerComponent = Mappers.playerComponent.get(entity);
		if (playerComponent != null && playerComponent.getSkillRange() != null) {
			AttackComponent rangeAttackCompo = Mappers.attackComponent.get(playerComponent.getSkillRange());
			if (rangeAttackCompo != null) {
				rangeAttackCompo.increaseStrength(1);
			}
		}
	}
	

	@Override
	public void onRemove(Entity entity) {		
		PlayerComponent playerComponent = Mappers.playerComponent.get(entity);
		if (playerComponent != null && playerComponent.getSkillRange() != null) {
			AttackComponent rangeAttackCompo = Mappers.attackComponent.get(playerComponent.getSkillRange());
			if (rangeAttackCompo != null) {
				rangeAttackCompo.increaseStrength(-1);
			}
		}
	}

}
