/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingOfMarvel;
import com.dokkaebistudio.tacticaljourney.alterations.curses.CurseMarvel;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

public class ItemPager extends AbstractInfusableItem {
	
	public ItemPager() {
		super(ItemEnum.PAGER, Assets.pager, false, true);
		setRecyclePrice(40);

		BlessingOfMarvel blessing = new BlessingOfMarvel();
		blessings.add(blessing);
		CurseMarvel curse = new CurseMarvel();
		curses.add(curse);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_PAGER_DESCRIPTION;	
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {return true;}
}
