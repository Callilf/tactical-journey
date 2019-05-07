/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.shinobi.BlessingShurikenjutsu;
import com.dokkaebistudio.tacticaljourney.alterations.curses.CurseShinobi;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * An infusable item that grants the Shurikenjutsu blessing.
 * @author Callil
 *
 */
public class ItemHandProsthesis extends AbstractInfusableItem {
	
	public ItemHandProsthesis() {
		super(ItemEnum.HAND_PROSTHESIS, Assets.hand_prothesis, false, true);
		setRecyclePrice(20);

		BlessingShurikenjutsu blessing = new BlessingShurikenjutsu();
		blessings.add(blessing);
		CurseShinobi curse = new CurseShinobi();
		curses.add(curse);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_HAND_PROSTHESIS_DESCRIPTION;	
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {return true;}

}
