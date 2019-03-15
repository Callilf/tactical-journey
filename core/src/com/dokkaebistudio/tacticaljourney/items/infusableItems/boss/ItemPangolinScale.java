/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems.boss;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingOfThePangolin;
import com.dokkaebistudio.tacticaljourney.alterations.curses.CurseOfPangolinMother;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent.AlterationActionEnum;
import com.dokkaebistudio.tacticaljourney.items.Item;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * An infusable item that grants the blessing of the pangolin and the curse of the pangolin mother.
 * @author Callil
 *
 */
public class ItemPangolinScale extends Item {
	
	private BlessingOfThePangolin blessing;
	private CurseOfPangolinMother curse;

	public ItemPangolinScale() {
		super(ItemEnum.PANGOLIN_SCALE, Assets.pangolin_scale, false, true);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_PANGOLIN_SCALE_DESCRIPTION;
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
				blessing = new BlessingOfThePangolin();
				alterationReceiverComponent.requestAction(AlterationActionEnum.RECEIVE_BLESSING, blessing);
				
				curse = new CurseOfPangolinMother();
				alterationReceiverComponent.requestAction(AlterationActionEnum.RECEIVE_CURSE, curse);
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
				alterationReceiverComponent.requestAction(AlterationActionEnum.REMOVE_CURSE, curse);
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
			alterationReceiverComponent.requestAction(AlterationActionEnum.REMOVE_CURSE, curse);
		}
	}
}
