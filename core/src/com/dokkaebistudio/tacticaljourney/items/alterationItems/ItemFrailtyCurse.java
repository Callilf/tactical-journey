/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.alterationItems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingVigor;
import com.dokkaebistudio.tacticaljourney.alterations.curses.CurseFrailty;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.items.Item;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Increases max health.

 * @author Callil
 *
 */
public class ItemFrailtyCurse extends Item {

	public ItemFrailtyCurse() {
		super("Curse of frailty", Assets.curse_frailty, false, false);
	}

	@Override
	public String getDescription() {
		return "The curse of frailty weakens the body of the receiver.";
	}
	
	@Override
	public String getActionLabel() {return null;}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {
		AlterationReceiverComponent blessingAndCurseReceiverComponent = Mappers.alterationReceiverComponent.get(user);
		if (blessingAndCurseReceiverComponent != null) {
			blessingAndCurseReceiverComponent.addCurse(user, new CurseFrailty());
		}
		
		return true;
	}
	
	@Override
	public Vector2 getPickupImageMoveDestination() {
		return null;
	}
}
