/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingIndegistible;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * An infusable item that grants the blessing of the indegistible, ie free movements on web.
 * @author Callil
 *
 */
public class ItemSilkyBeard extends AbstractInfusableItem {
	
	public ItemSilkyBeard() {
		super(ItemEnum.SILKY_BEARD, Assets.silky_beard, false, true);
		setRecyclePrice(20);

		BlessingIndegistible blessing = new BlessingIndegistible();
		blessings.add(blessing);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_SILKY_BEARD_DESCRIPTION;	
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {return true;}
}
