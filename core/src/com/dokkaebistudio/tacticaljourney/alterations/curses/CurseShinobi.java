/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.curses;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.systems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Curse of heavy arrows. Range - 1.
 * @author Callil
 *
 */
public class CurseShinobi extends Curse {

	@Override
	public String title() {
		return "Curse of the shinobi";
	}
	
	@Override
	public String description() {
		return "Reduce max armor by 10. Shinobi are swift warriors that cannot perform their art properly while wearing an armor.";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.curse_shinobi;
	}

	@Override
	public void onReceive(Entity entity) {
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
		
		if (healthComponent != null) {
			healthComponent.increaseMaxArmor(-10);
			
			Journal.addEntry("[PURPLE]Curse of the shinobi reduced your max armor by 10");
			AlterationSystem.addAlterationProc(this);
		}
	}

	@Override
	public void onRemove(Entity entity) {
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
		
		if (healthComponent != null) {
			healthComponent.increaseMaxArmor(10);
		}
	}

}
