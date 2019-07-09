/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings.basics;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.ces.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Blessing of strength. Increase the entity's strength by 1.
 * @author Callil
 *
 */
public class BlessingStrength extends Blessing {
	
	@Override
	public String title() {
		return "Blessing of strength";
	}
	
	@Override
	public String description() {
		return "Increase strength by 1";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_strength;
	}

	@Override
	public void onReceive(Entity entity) {
		AttackComponent attackCompo = Mappers.attackComponent.get(entity);
		
		if (attackCompo != null) {
			attackCompo.increaseStrength(1);
		}
		
		AlterationSystem.addAlterationProc(this);
	}

	@Override
	public void onRemove(Entity entity) {
		AttackComponent attackCompo = Mappers.attackComponent.get(entity);
		
		if (attackCompo != null) {
			attackCompo.increaseStrength(-1);
		}
	}

}
