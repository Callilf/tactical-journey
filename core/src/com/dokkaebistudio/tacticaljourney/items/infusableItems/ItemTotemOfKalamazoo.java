/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingOfKalamazoo;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * An infusable item that grants the blessing of Kalamazoo.
 * @author Callil
 *
 */
public class ItemTotemOfKalamazoo extends AbstractInfusableItem {
	
	public ItemTotemOfKalamazoo() {
		super(ItemEnum.TOTEM_OF_KALAMAZOO, Assets.totem_of_kalamazoo, false, true);
		
		BlessingOfKalamazoo blessing = new BlessingOfKalamazoo();
		blessings.add(blessing);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_TOTEM_OF_KALAMAZOO_DESCRIPTION;	
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {return true;}
	
}
