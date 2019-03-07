/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.items.Item;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * A consumable item that restore 10 Armor.
 * @author Callil
 *
 */
public class ItemArmorPiece extends Item {

	public ItemArmorPiece() {
		super("Piece of armor", Assets.armor_piece_item, false, true);
	}
	
	@Override
	public String getDescription() {
		return "Gives 10 armor upon use.\n"
				+ "The armor protects your health by taking damage. Some kinds of damage however will bypass the armor and lower the health directly.";		
	}
	
	@Override
	public String getActionLabel() {
		return "[CYAN]Equip";
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {
		Journal.addEntry("You equiped the armor piece.");

		//Restore 10 Armor !
		HealthComponent healthComponent = Mappers.healthComponent.get(user);
		healthComponent.restoreArmor(10);
		return true;
	}
}
