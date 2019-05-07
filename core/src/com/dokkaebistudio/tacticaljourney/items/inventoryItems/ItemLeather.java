/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * A piece of leather hide. Crafting material for the sewing machine.
 * @author Callil
 *
 */
public class ItemLeather extends AbstractItem {

	public ItemLeather() {
		super(ItemEnum.LEATHER, Assets.leather_item, false, true);
		setRecyclePrice(15);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_LEATHER_DESCRIPTION;		
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
	public boolean use(Entity user, Entity item, Room room) {
		return true;
	}
}
