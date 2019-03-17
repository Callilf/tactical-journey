/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.items.Item;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
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
		super(ItemEnum.ARMOR_PIECE, Assets.armor_piece_item, false, true);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_ARMOR_PIECE_DESCRIPTION;		
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
