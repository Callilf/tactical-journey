/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingKawarimi;
import com.dokkaebistudio.tacticaljourney.alterations.curses.CurseShinobi;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * An infusable item that grants the Kawarimi blessing.
 * @author Callil
 *
 */
public class ItemJikatabi extends AbstractInfusableItem {
	
	public ItemJikatabi() {
		super(ItemEnum.JIKATABI, Assets.jikatabi, false, true);
		
		BlessingKawarimi blessing2 = new BlessingKawarimi();
		blessing2.setItemSprite(this.getTexture());
		blessings.add(blessing2);
		CurseShinobi curse = new CurseShinobi();
		curse.setItemSprite(this.getTexture());
		curses.add(curse);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_JIKATABI_DESCRIPTION;	
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {return true;}

}
