/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.ces.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Blessing of Calishka. Starting blessing that heals 5hp on room visit and room clear.
 * @author Callil
 *
 */
public class BlessingOfCalishka extends Blessing {
	
	private int healAmount = 5;
	
	@Override
	public String title() {
		return "Blessing of Calishka";
	}
	
	@Override
	public String description() {
		return "On discovering a new room, [GREEN]restore 5 hp[]. On room clear, [GREEN]restore 5 hp";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_calishka;
	}
	
	@Override
	public void onRoomVisited(Entity entity, Room room) {
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
		
		if (healthComponent != null) {
			healthComponent.restoreHealth(healAmount);
			
			Journal.addEntry("[GREEN]Blessing of Calishka granted " + healAmount + " hp for discovering a new room.");
			AlterationSystem.addAlterationProc(this);
		}	}

	@Override
	public void onRoomCleared(Entity entity, Room room) {
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
		
		if (healthComponent != null) {
			healthComponent.restoreHealth(healAmount);
			
			Journal.addEntry("[GREEN]Blessing of Calishka granted " + healAmount + " hp for clearing the room.");
			AlterationSystem.addAlterationProc(this);
		}
	}

}
