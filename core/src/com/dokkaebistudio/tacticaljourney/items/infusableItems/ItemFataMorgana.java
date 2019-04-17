/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingOfVilma;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * An infusable item that grants the blessing of Vilma, ie regeneration on kill.
 * @author Callil
 *
 */
public class ItemFataMorgana extends AbstractInfusableItem {
	
	public ItemFataMorgana() {
		super(ItemEnum.FATA_MORGANA, Assets.fata_morgana, false, true);
		
		BlessingOfVilma blessing = new BlessingOfVilma();
		blessings.add(blessing);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_FATA_MORGANA_DESCRIPTION;	
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {return true;}
	

}
