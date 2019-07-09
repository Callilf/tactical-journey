/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.curses.basics;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;
import com.dokkaebistudio.tacticaljourney.ces.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Curse of frailty. Reduce the entity's max HP by 10.
 * @author Callil
 *
 */
public class CurseBadLuck extends Curse {

	@Override
	public String title() {
		return "Curse of bad luck";
	}
	
	@Override
	public String description() {
		return "Reduce karma by 2";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.curse_bad_luck;
	}

	@Override
	public void onReceive(Entity entity) {
		PlayerComponent playerComponent = Mappers.playerComponent.get(entity);
		if (playerComponent != null) {
			playerComponent.increaseKarma(-2);
		}
		
		AlterationSystem.addAlterationProc(this);
	}

	@Override
	public void onRemove(Entity entity) {
		PlayerComponent playerComponent = Mappers.playerComponent.get(entity);
		if (playerComponent != null) {
			playerComponent.increaseKarma(2);
		}
	}

}
