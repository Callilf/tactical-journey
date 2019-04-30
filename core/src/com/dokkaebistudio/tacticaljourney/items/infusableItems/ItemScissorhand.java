/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingUnfinished;
import com.dokkaebistudio.tacticaljourney.alterations.curses.CurseUnfinished;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * An infusable item that grants the blessing of the unfinished, ie destroy webs, bushes, immune to entangled.
 * @author Callil
 *
 */
public class ItemScissorhand extends AbstractInfusableItem {
	
	public ItemScissorhand() {
		super(ItemEnum.SCISSORHAND, Assets.scissorhand, false, true);
		
		BlessingUnfinished blessing = new BlessingUnfinished();
		blessings.add(blessing);
		CurseUnfinished curse = new CurseUnfinished();
		curses.add(curse);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_SCISSORHAND_DESCRIPTION;	
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {return true;}
}
