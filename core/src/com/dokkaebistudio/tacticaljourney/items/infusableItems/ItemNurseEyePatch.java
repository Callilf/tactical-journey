/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingBlackMamba;
import com.dokkaebistudio.tacticaljourney.alterations.curses.CurseBlackMamba;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * An infusable item that grants the black mamba's blessing and curse.
 * @author Callil
 *
 */
public class ItemNurseEyePatch extends AbstractInfusableItem {
	
	public ItemNurseEyePatch() {
		super(ItemEnum.NURSE_EYE_PATCH, Assets.nurse_eye_patch, false, true);
		
		BlessingBlackMamba blessing = new BlessingBlackMamba();
		blessing.setItemSprite(this.getTexture());
		blessings.add(blessing);
		
		CurseBlackMamba c = new CurseBlackMamba();
		c.setItemSprite(this.getTexture());
		curses.add(c);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_NURSE_EYE_PATCH_DESCRIPTION;	
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {return true;}

}
