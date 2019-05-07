/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingOfFireArrows;
import com.dokkaebistudio.tacticaljourney.alterations.curses.CurseHeavyArrows;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * An infusable item that grants the blessing of fire arrows and curse of heavy arrows (range -1).
 * @author Callil
 *
 */
public class ItemPowderFlask extends AbstractInfusableItem {

	public ItemPowderFlask() {
		super(ItemEnum.POWDER_FLASK, Assets.powder_flask, false, true);
		setRecyclePrice(20);

		BlessingOfFireArrows blessing = new BlessingOfFireArrows();
		blessings.add(blessing);
		
		CurseHeavyArrows c = new CurseHeavyArrows();
		curses.add(c);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_POWDER_FLASK_DESCRIPTION;	
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {return true;}
	
}
