/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Blessing of Kalamazoo. Restore between 0 and 10 health after clearing a room.
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
		return "On room clear, [GREEN]restore 5 hp";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_calishka;
	}

	@Override
	public void onRoomCleared(Entity entity, Room room) {
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
		
		if (healthComponent != null) {
			healthComponent.restoreHealth(healAmount);
			
			Journal.addEntry("[GREEN]Blessing of Calishka granted " + healAmount + " hp.");
		}
	}

}
