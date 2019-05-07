/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingMithridatism;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffPoison;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * An infusable item that grants the Mithridatism blessing + can be used to cure poison.
 * @author Callil
 *
 */
public class ItemMithridatium extends AbstractInfusableItem {
	
	public ItemMithridatium() {
		super(ItemEnum.MITHRIDATIUM, Assets.mithridatium, false, true);
		setRecyclePrice(15);

		BlessingMithridatism blessing = new BlessingMithridatism();
		blessings.add(blessing);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_MITHRIDATIUM_DESCRIPTION;	
	}
	
	@Override
	public String getActionLabel() {
		return "[GREEN]Drink";
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {
		Journal.addEntry("You take a sip from the Mithridatium");
		StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(user);
		statusReceiverComponent.removeStatus(user, StatusDebuffPoison.class, room);
		
		room.turnManager.endPlayerTurn();
		
		// Return false to prevent the item from being consumed
		return false;
	}
	
}
