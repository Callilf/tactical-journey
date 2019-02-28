/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.items.Item;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.buffs.StatusBuffFlight;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * A consumable item that heals 25 HP.
 * @author Callil
 *
 */
public class ItemWingPotion extends Item {

	public ItemWingPotion() {
		super("Wing potion", Assets.wing_potion_item, false, true);
	}
	
	@Override
	public String getDescription() {
		return "Upon drink, grants flight for 30 turns.\n"
				+ "Add lore.....";		
	}
	
	@Override
	public String getActionLabel() {
		return "Drink";
	}
	
	@Override
	public boolean isStackable() {
		return true;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {
		StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(user);
		statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusBuffFlight(30, room.engine));
		
		return true;
	}
}