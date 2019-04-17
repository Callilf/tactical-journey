/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.shinobi.BlessingHangeki;
import com.dokkaebistudio.tacticaljourney.alterations.curses.CurseShinobi;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * An infusable item that grants the Kawarimi blessing.
 * @author Callil
 *
 */
public class ItemRightJikatabi extends AbstractInfusableItem {
	
	public ItemRightJikatabi() {
		super(ItemEnum.RIGHT_JIKATABI, Assets.right_jikatabi, false, true);
		
		BlessingHangeki blessing2 = new BlessingHangeki();
		blessings.add(blessing2);
		CurseShinobi curse = new CurseShinobi();
		curses.add(curse);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_RIGHT_JIKATABI_DESCRIPTION;	
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {return true;}

}
