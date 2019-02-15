/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * A consumable item that heals 25 HP.
 * @author Callil
 *
 */
public class ItemSmallHealthPotion extends Item {

	public ItemSmallHealthPotion() {
		super("Small health potion", Assets.health_up_item, false, true);
	}
	
	@Override
	public String getDescription() {
		return "Heal 25 HP upon use.\n"
				+ "Remember that drinking this potion will take a turn, so don't stay too close from the enemy while doing it.";		
	}
	
	@Override
	public String getActionLabel() {
		return "Drink";
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {
		//Heal the picker for 25 HP !
		HealthComponent healthComponent = Mappers.healthComponent.get(user);
		healthComponent.restoreHealth(25);
		return true;
	}
}
