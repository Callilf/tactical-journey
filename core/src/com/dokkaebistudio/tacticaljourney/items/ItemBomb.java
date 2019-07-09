/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.ces.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.enums.AmmoTypeEnum;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.rendering.HUDRenderer;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Add bombs to the player's bag.
 * @author Callil
 *
 */
public class ItemBomb extends AbstractItem {

	public ItemBomb() {
		super("# bomb[s]", Assets.bomb_item, false, false, 1, 1);
		this.type = ItemEnum.AMMO_BOMB;
		setRecyclePrice(2);
	}
	
	public ItemBomb(RandomXS128 randomToUse) {
		super("# bomb[s]", Assets.bomb_item, false, false, 1, 1, randomToUse);
		this.type = ItemEnum.AMMO_BOMB;
		setRecyclePrice(2);
	}

	@Override
	public String getDescription() {
		return Descriptions.ITEM_BOMBS_DESCRIPTION;
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
		
		if (itemComponent.getQuantityPickedUp() >0) {
			Journal.addEntry("You picked up " + itemComponent.getQuantityPickedUp() + " bomb(s).");
		} else {
			Journal.addEntry("[SCARLET]Your bomb bag is already full");
		}
		
		return remainingBombs == 0;
	}
	
	@Override
	public Vector2 getPickupImageMoveDestination() {
		return HUDRenderer.POS_BOMB_SPRITE;
	}
}
