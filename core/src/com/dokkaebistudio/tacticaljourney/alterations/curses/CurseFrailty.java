/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.curses;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Curse of frailty. Reduce the entity's max HP.
 * @author Callil
 *
 */
public class CurseFrailty extends Curse {

	@Override
	public void onReceive(Entity entity) {
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
	
		if (healthComponent != null) {
			healthComponent.setMaxHp(healthComponent.getMaxHp() - 10);
			if (healthComponent.getHp() > healthComponent.getMaxHp()) {
				healthComponent.setHp(healthComponent.getMaxHp());
			}
		}
	}

	@Override
	public void onRemove(Entity entity) {
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
		
		if (healthComponent != null) {
			healthComponent.setMaxHp(healthComponent.getMaxHp() + 10);
		}
	}

}
