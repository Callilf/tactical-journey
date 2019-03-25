/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingContractKiller;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * An infusable item that grants the blessing of the poisoner, ie poison sector on the wheel.
 * @author Callil
 *
 */
public class ItemVillanelle extends AbstractInfusableItem {
	
	public ItemVillanelle() {
		super(ItemEnum.VILLANELLE, Assets.villanelle, false, true);
		
		BlessingContractKiller blessing = new BlessingContractKiller();
		blessing.setItemSprite(this.getTexture());
		blessings.add(blessing);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_OLD_CROWN_DESCRIPTION;	
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {return true;}
}
