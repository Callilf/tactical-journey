/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems.boss;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingOfThePangolin;
import com.dokkaebistudio.tacticaljourney.alterations.curses.CurseOfPangolinMother;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.AbstractInfusableItem;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * An infusable item that grants the blessing of the pangolin and the curse of the pangolin mother.
 * @author Callil
 *
 */
public class ItemPangolinScale extends AbstractInfusableItem {

	public ItemPangolinScale() {
		super(ItemEnum.PANGOLIN_SCALE, Assets.pangolin_scale, false, true);
		
		BlessingOfThePangolin blessing = new BlessingOfThePangolin();
		blessing.setItemSprite(this.getTexture());
		blessings.add(blessing);
		
		CurseOfPangolinMother c = new CurseOfPangolinMother();
		c.setItemSprite(this.getTexture());
		curses.add(c);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_PANGOLIN_SCALE_DESCRIPTION;
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {return true;}
	
}
