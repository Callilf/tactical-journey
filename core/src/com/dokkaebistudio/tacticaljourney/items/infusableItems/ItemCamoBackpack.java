/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingOfTheLooter;
import com.dokkaebistudio.tacticaljourney.alterations.curses.CurseSlowness;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * An infusable item that grants the blessing of the looter (more inventory space) + curse of slowness.
 * @author Callil
 *
 */
public class ItemCamoBackpack extends AbstractInfusableItem {

	public ItemCamoBackpack() {
		super(ItemEnum.CAMO_BACKPACK, Assets.camo_backpack, false, true);
		
		BlessingOfTheLooter blessing = new BlessingOfTheLooter();
		blessing.setItemSprite(this.getTexture());
		blessings.add(blessing);
		
		CurseSlowness c = new CurseSlowness();
		c.setItemSprite(this.getTexture());
		curses.add(c);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_CAMO_BACKPACK_DESCRIPTION;	
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {return true;}
	
}
