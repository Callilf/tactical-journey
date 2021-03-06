/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.curses.basics;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;
import com.dokkaebistudio.tacticaljourney.ces.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Curse of weakness. Reduce the entity's strength by 1.
 * @author Callil
 *
 */
public class CurseWeakness extends Curse {

	@Override
	public String title() {
		return "Curse of weakness";
	}
	
	@Override
	public String description() {
		return "Reduce strength by 1";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.curse_weakness;
	}

	@Override
	public void onReceive(Entity entity) {
		AttackComponent attackCompo = Mappers.attackComponent.get(entity);
		
		if (attackCompo != null) {
			attackCompo.increaseStrength(-1);
		}
		
		AlterationSystem.addAlterationProc(this);
	}

	@Override
	public void onRemove(Entity entity) {
		AttackComponent attackCompo = Mappers.attackComponent.get(entity);
		
		if (attackCompo != null) {
			attackCompo.increaseStrength(1);
		}
	}

}
