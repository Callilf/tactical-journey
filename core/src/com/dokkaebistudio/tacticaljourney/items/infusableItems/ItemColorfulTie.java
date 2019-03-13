/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingFastLearner;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent.AlterationActionEnum;
import com.dokkaebistudio.tacticaljourney.items.Item;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * An infusable item that grants the blessing of the fast learner, i.e. choose one more level up reward.
 * @author Callil
 *
 */
public class ItemColorfulTie extends Item {
	
	private BlessingFastLearner blessing;

	public ItemColorfulTie() {
		super(ItemEnum.COLORFUL_TIE, Assets.colorful_tie, false, true);
	}
	
	@Override
	public String getDescription() {
		return "A neck-tie with old-fashioned colors, probably a piece of a school uniform.\n"
				+ "Grants the blessing of the fast learner while held in the inventory.";	
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {return true;}
	
	@Override
	public boolean pickUp(Entity picker, Entity item, Room room) {
		boolean pickedUp = super.pickUp(picker, item, room);
		
		if (pickedUp) {
			AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(picker);
			if (alterationReceiverComponent != null) {
				blessing = new BlessingFastLearner();
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