/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * The universal cure. Picking it up launched the end game popin.
 * @author Callil
 *
 */
public class ItemUniversalCure extends AbstractItem {

	public ItemUniversalCure() {
		super(ItemEnum.UNIVERSAL_CURE, Assets.universal_cure, false, true);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_UNIVERSAL_CURE_DESCRIPTION;		
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean isStackable() {
		return true;
	}
	
	
	
	@Override
	public boolean pickUp(Entity picker, Entity item, Room room) {
		super.pickUp(picker, item, room);
		//TODO launch the end game
		Journal.addEntry("[GREEN] You picked up the universal cure!!!!!");
		return true;
	}

	@Override
	public boolean use(Entity user, Entity item, Room room) {
		return false;
	}
	
}
