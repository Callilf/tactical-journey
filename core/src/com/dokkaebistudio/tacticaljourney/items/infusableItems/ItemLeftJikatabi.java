/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.shinobi.BlessingKawarimi;
import com.dokkaebistudio.tacticaljourney.alterations.curses.CurseShinobi;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * An infusable item that grants the Kawarimi blessing.
 * @author Callil
 *
 */
public class ItemLeftJikatabi extends AbstractInfusableItem {
	
	public ItemLeftJikatabi() {
		super(ItemEnum.LEFT_JIKATABI, Assets.left_jikatabi, false, true);
		setRecyclePrice(20);

		BlessingKawarimi blessing2 = new BlessingKawarimi();
		blessings.add(blessing2);
		CurseShinobi curse = new CurseShinobi();
		curses.add(curse);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_LEFT_JIKATABI_DESCRIPTION;	
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {return true;}

}
