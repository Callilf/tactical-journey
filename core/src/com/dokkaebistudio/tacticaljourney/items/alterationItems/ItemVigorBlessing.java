/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.alterationItems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingVigor;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent.AlterationActionEnum;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Increases max health.

 * @author Callil
 *
 */
public class ItemVigorBlessing extends AbstractItem {

	public ItemVigorBlessing() {
		super("Blessing of vigor", Assets.blessing_vigor, false, false);
	}

	@Override
	public String getDescription() {
		return "The blessing of vigor enhances the body. The receiver can take more damages before going down.";
	}
	
	@Override
	public String getActionLabel() {return null;}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {
		AlterationReceiverComponent blessingAndCurseReceiverComponent = Mappers.alterationReceiverComponent.get(user);
		if (blessingAndCurseReceiverComponent != null) {
			blessingAndCurseReceiverComponent.requestAction(AlterationActionEnum.RECEIVE_BLESSING, new BlessingVigor());
		}
		
		return true;
	}
	
	@Override
	public Vector2 getPickupImageMoveDestination() {
		return null;
	}
}
