/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.enums.AmmoTypeEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.rendering.HUDRenderer;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Add bombs to the player's bag.
 * @author Callil
 *
 */
public class ItemBomb extends Item {

	public ItemBomb() {
		super("# bomb[s]", Assets.bomb_item, false, false, 1, 2);
	}

	@Override
	public String getDescription() {
		return "Bombs can be thrown on the ground by using your bomb skill and explode after some turns. Be sure to stay away from the blast. "
				+ "If there are too much bombs for your bag, the remaining bombs will stay on the ground.";
	}
	
	@Override
	public String getActionLabel() {return null;}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {
		ItemComponent itemComponent = Mappers.itemComponent.get(item);
		AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(user);
		int remainingBombs = ammoCarrierComponent.pickUpAmmo(AmmoTypeEnum.BOMBS, itemComponent.getQuantity());
		
		itemComponent.setQuantityPickedUp(itemComponent.getQuantity() - remainingBombs);
		itemComponent.setQuantity(remainingBombs);
		
		Journal.addEntry("Picked up " + itemComponent.getQuantityPickedUp() + " bomb(s).");

		return remainingBombs == 0;
	}
	
	@Override
	public Vector2 getPickupImageMoveDestination() {
		return HUDRenderer.POS_BOMB_SPRITE;
	}
}
