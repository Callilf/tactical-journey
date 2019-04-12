/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.player.WalletComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
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
	
	public enum MoneyAmountEnum {
		ONE(1, 1, Assets.money_item),
		SMALL(1, 5, Assets.money_item),
		MEDIUM(5, 10, Assets.money_medium_item),
		LARGE(10, 20, Assets.money_medium_item);
		
		public int min;
		public int max;
		public RegionDescriptor region;
		
		private MoneyAmountEnum(int min, int max, RegionDescriptor region) {
			this.min = min;
			this.max = max;
			this.region = region;
		}
	}
	

	public ItemMoney() {
		super("# gold coin[s]", Assets.money_item, false, false, 1, 5);
		this.type = ItemEnum.MONEY;
	}
	
	public ItemMoney(RandomXS128 randomToUse) {
		super("# gold coin[s]", Assets.money_item, false, false, 1, 5, randomToUse);
		this.type = ItemEnum.MONEY;
	}
	
	public ItemMoney(MoneyAmountEnum amount) {
		super("# gold coin[s]", amount.region, false, false, amount.min, amount.max);
		
		switch (amount) {
		case MEDIUM:
			this.type = ItemEnum.MONEY_MEDIUM;
			break;
		case LARGE:
			this.type = ItemEnum.MONEY_BIG;
			break;
			
			default:
				this.type = ItemEnum.MONEY;

		}
	}
	
	public ItemMoney(MoneyAmountEnum amount, RandomXS128 randomToUse) {
		super("# gold coin[s]", amount.region, false, false, amount.min, amount.max, randomToUse);
		
		switch (amount) {
		case MEDIUM:
			this.type = ItemEnum.MONEY_MEDIUM;
			break;
		case LARGE:
			this.type = ItemEnum.MONEY_BIG;
			break;
			
			default:
				this.type = ItemEnum.MONEY;

		}
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
		
		WalletComponent walletComponent = Mappers.walletComponent.get(user);
		walletComponent.receive(itemComponent.getQuantity());

		itemComponent.setQuantityPickedUp(itemComponent.getQuantity());
		itemComponent.setQuantity(0);
		
		Journal.addEntry("You picked up [GOLDENROD]" + itemComponent.getQuantity() + " gold coins.");

		return true;
	}
	
	@Override
	public Vector2 getPickupImageMoveDestination() {
		return HUDRenderer.POS_MONEY;
	}
}
