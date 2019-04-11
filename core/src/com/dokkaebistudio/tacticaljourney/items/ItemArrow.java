/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.enums.AmmoTypeEnum;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.rendering.HUDRenderer;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Add arrows to the player's quiver.
 * @author Callil
 *
 */
public class ItemArrow extends AbstractItem {

	public ItemArrow() {
		super("# arrow[s]", Assets.arrow_item, false, false, 1, 4);
		this.type = ItemEnum.AMMO_ARROW;
	}
	
	public ItemArrow(RandomXS128 randomToUse) {
		super("# arrow[s]", Assets.arrow_item, false, false, 1, 4, randomToUse);
		this.type = ItemEnum.AMMO_ARROW;
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_ARROWS_DESCRIPTION;
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

		if (itemComponent.getQuantityPickedUp() >0) {
			Journal.addEntry("You picked up " + itemComponent.getQuantityPickedUp() + " arrow(s).");
		} else {
			Journal.addEntry("[SCARLET]Your quiver is already full");
		}

		return remainingArrows == 0;
	}
	
	@Override
	public Vector2 getPickupImageMoveDestination() {
		return HUDRenderer.POS_ARROW_SPRITE;
	}
}
