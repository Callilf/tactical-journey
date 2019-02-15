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
import com.dokkaebistudio.tacticaljourney.rendering.HUDRenderer;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Add arrows to the player's quiver.
 * @author Callil
 *
 */
public class ItemArrow extends Item {

	public ItemArrow() {
		super("# arrow[s]", Assets.arrow_item, false, false, 1, 5);
	}

	@Override
	public String getDescription() {
		return "Arrows can be shot from a distance using your bow skill (down left of the screen). If there are too much arrows for your quiver, the remaining "
			+ "arrows will stay on the ground.";
	}
	
	@Override
	public String getActionLabel() {return null;}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {
		ItemComponent itemComponent = Mappers.itemComponent.get(item);
		AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(user);
		int remainingArrows = ammoCarrierComponent.pickUpAmmo(AmmoTypeEnum.ARROWS, itemComponent.getQuantity());
		
		itemComponent.setQuantityPickedUp(itemComponent.getQuantity() - remainingArrows);
		itemComponent.setQuantity(remainingArrows);

		return remainingArrows == 0;
	}
	
	@Override
	public Vector2 getPickupImageMoveDestination() {
		return HUDRenderer.POS_ARROW_SPRITE;
	}
}