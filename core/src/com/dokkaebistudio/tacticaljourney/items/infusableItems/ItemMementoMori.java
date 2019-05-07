/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingOfAcceptance;
import com.dokkaebistudio.tacticaljourney.alterations.curses.CurseOfAcceptance;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * An infusable item that grants the blessing of the poisoner, ie poison sector on the wheel.
 * @author Callil
 *
 */
public class ItemMementoMori extends AbstractInfusableItem {

	public ItemMementoMori() {
		super(ItemEnum.MEMENTO_MORI, Assets.memento_mori, false, true);
		setRecyclePrice(25);

		BlessingOfAcceptance blessing = new BlessingOfAcceptance();
		blessings.add(blessing);
		
		CurseOfAcceptance c = new CurseOfAcceptance();
		curses.add(c);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_MEMENTO_MORI_DESCRIPTION;	
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {return true;}
	
}
