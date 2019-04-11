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
import com.dokkaebistudio.tacticaljourney.components.player.WalletComponent;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.rendering.HUDRenderer;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Add money to the player's wallet.

 * @author Callil
 *
 */
public class ItemMoney extends AbstractItem {

	public ItemMoney() {
		super("# gold coin[s]", Assets.money_item, true, false, 1, 5);
		this.type = ItemEnum.MONEY;
	}
	
	public ItemMoney(RandomXS128 randomToUse) {
		super("# gold coin[s]", Assets.money_item, true, false, 1, 5, randomToUse);
		this.type = ItemEnum.MONEY;
	}

	@Override
	public String getDescription() {
		return Descriptions.ITEM_MONEY_DESCRIPTION;
	}
	
	@Override
	public String getActionLabel() {return null;}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {
		ItemComponent itemComponent = Mappers.itemComponent.get(item);
		itemComponent.setQuantityPickedUp(itemComponent.getQuantity());
		WalletComponent walletComponent = Mappers.walletComponent.get(user);
		walletComponent.receive(itemComponent.getQuantity());
		
		Journal.addEntry("You picked up [GOLDENROD]" + itemComponent.getQuantity() + " gold coins.");

		return true;
	}
	
	@Override
	public Vector2 getPickupImageMoveDestination() {
		return HUDRenderer.POS_MONEY;
	}
}
