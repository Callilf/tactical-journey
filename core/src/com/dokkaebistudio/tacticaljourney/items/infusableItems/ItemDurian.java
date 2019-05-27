/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingOfTheOrangutan;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * An infusable item that grants the blessing of the orangutan.
 * @author Callil
 *
 */
public class ItemDurian extends AbstractInfusableItem {
	
	public ItemDurian() {
		super(ItemEnum.DURIAN, Assets.durian, false, true);
		setRecyclePrice(35);

		BlessingOfTheOrangutan blessing = new BlessingOfTheOrangutan();
		blessings.add(blessing);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_DURIAN_DESCRIPTION;	
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {return true;}
}
