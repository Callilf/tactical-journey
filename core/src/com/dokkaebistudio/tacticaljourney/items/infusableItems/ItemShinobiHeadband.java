/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.shinobi.BlessingBunshin;
import com.dokkaebistudio.tacticaljourney.alterations.curses.CurseShinobi;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * An infusable item that grants the Bunshin no jutsu blessing.
 * @author Callil
 *
 */
public class ItemShinobiHeadband extends AbstractInfusableItem {
	
	public ItemShinobiHeadband() {
		super(ItemEnum.SHINOBI_HEADBAND, Assets.shinobi_headband, false, true);
		setRecyclePrice(20);

		BlessingBunshin blessing2 = new BlessingBunshin();
		blessings.add(blessing2);
		CurseShinobi curse = new CurseShinobi();
		curses.add(curse);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_SHINOBI_HEADBAND_DESCRIPTION;	
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {return true;}

}
