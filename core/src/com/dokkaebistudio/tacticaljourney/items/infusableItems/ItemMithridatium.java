/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingMithridatism;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent.AlterationActionEnum;
import com.dokkaebistudio.tacticaljourney.items.Item;
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
public class ItemMithridatium extends Item {
	
	private BlessingMithridatism blessing;

	public ItemMithridatium() {
		super(ItemEnum.MITHRIDATIUM, Assets.mithridatium, false, true);
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
	
	@Override
	public boolean pickUp(Entity picker, Entity item, Room room) {
		boolean pickedUp = super.pickUp(picker, item, room);
		
		if (pickedUp) {
			AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(picker);
			if (alterationReceiverComponent != null) {
				blessing = new BlessingMithridatism();
				alterationReceiverComponent.requestAction(AlterationActionEnum.RECEIVE_BLESSING, blessing);
			}
		}
		
		return pickedUp;
	}
	
	
	@Override
	public boolean drop(Entity dropper, Entity item, Room room) {
		boolean dropped = super.drop(dropper, item, room);
	
		if (dropped) {
			AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(dropper);
			if (alterationReceiverComponent != null) {
				alterationReceiverComponent.requestAction(AlterationActionEnum.REMOVE_BLESSING, blessing);
			}
		}
		
		return dropped;
	}
	
	@Override
	public void onThrow(Vector2 thrownPosition, Entity thrower, Entity item, Room room) {
		super.onThrow(thrownPosition, thrower, item, room);
		
		AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(thrower);
		if (alterationReceiverComponent != null) {
			alterationReceiverComponent.requestAction(AlterationActionEnum.REMOVE_BLESSING, blessing);
		}
	}
}
