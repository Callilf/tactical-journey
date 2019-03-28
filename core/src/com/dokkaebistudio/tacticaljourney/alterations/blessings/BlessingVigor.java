/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Blessing of vigor. Increase the entity's max HP by 10.
 * @author Callil
 *
 */
public class BlessingVigor extends Blessing {
	
	@Override
	public String title() {
		return "Blessing of vigor";
	}
	
	@Override
	public String description() {
		return "Increase max HP by 10";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_vigor;
	}

	@Override
	public void onReceive(Entity entity) {
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
		
		if (healthComponent != null) {
			healthComponent.setMaxHp(healthComponent.getMaxHp() + 10);
		}
	}

	@Override
	public void onRemove(Entity entity) {
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
		
		if (healthComponent != null) {
			healthComponent.setMaxHp(healthComponent.getMaxHp() - 10);
			if (healthComponent.getHp() > healthComponent.getMaxHp()) {
				healthComponent.setHp(healthComponent.getMaxHp());
			}
		}

	}

}
