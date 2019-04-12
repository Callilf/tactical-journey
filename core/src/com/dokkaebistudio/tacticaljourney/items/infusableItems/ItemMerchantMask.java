/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingMaskMerchant;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * An infusable item that grants the blessing of the fast learner, i.e. choose one more level up reward.
 * @author Callil
 *
 */
public class ItemMerchantMask extends AbstractInfusableItem {

	public ItemMerchantMask() {
		super(ItemEnum.MERCHANT_MASK, Assets.merchant_mask, false, true);
		
		BlessingMaskMerchant blessing = new BlessingMaskMerchant();
		blessing.setItemSprite(this.getTexture());
		blessings.add(blessing);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_MERCHANT_MASK_DESCRIPTION;	
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {return true;}
	
}
