/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.ces.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Blessing of Kalamazoo. Restore between 0 and 5 health after clearing a room.
 * @author Callil
 *
 */
public class BlessingOfKalamazoo extends Blessing {
	
	@Override
	public String title() {
		return "Blessing of Kalamazoo";
	}
	
	@Override
	public String description() {
		return "On room clear, [GREEN]restore between 1 and 5 hp";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_of_kalamazoo;
	}

	@Override
	public void onRoomCleared(Entity entity, Room room) {
		HealthComponent healthComponent = Mappers.healthComponent.get(entity);
		
		if (healthComponent != null) {
			int healAmount = 1 + RandomSingleton.getInstance().getUnseededRandom().nextInt(5);
			healthComponent.restoreHealth(healAmount);
			
			Journal.addEntry("[GREEN]Blessing of Kalamazoo granted " + healAmount + " hp.");
			AlterationSystem.addAlterationProc(this);
		}
	}

}
