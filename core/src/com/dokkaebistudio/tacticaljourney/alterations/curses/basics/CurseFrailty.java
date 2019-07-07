/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.curses.basics;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.systems.entitysystems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Curse of frailty. Reduce the entity's max HP by 10.
 * @author Callil
 *
 */
public class CurseFrailty extends Curse {

	@Override
	public String title() {
		return "Curse of frailty";
	}
	
	@Override
	public String description() {
		return "Reduce max HP by 10";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.curse_frailty;
	}

	@Override
	public void onReceive(Entity entity) {
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
	
		if (healthComponent != null) {
			healthComponent.setMaxHp(healthComponent.getMaxHp() - 10);
			if (healthComponent.getHp() > healthComponent.getMaxHp()) {
				healthComponent.setHp(healthComponent.getMaxHp());
			}
		}
		
		AlterationSystem.addAlterationProc(this);
	}

	@Override
	public void onRemove(Entity entity) {
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
		
		if (healthComponent != null) {
			healthComponent.setMaxHp(healthComponent.getMaxHp() + 10);
		}
	}

}
