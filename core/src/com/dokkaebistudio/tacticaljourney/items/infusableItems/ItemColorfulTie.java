/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.infusableItems;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingFastLearner;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * An infusable item that grants the blessing of the fast learner, i.e. choose one more level up reward.
 * @author Callil
 *
 */
public class ItemColorfulTie extends AbstractInfusableItem {

	public ItemColorfulTie() {
		super(ItemEnum.COLORFUL_TIE, Assets.colorful_tie, false, true);
		setRecyclePrice(30);

		BlessingFastLearner blessingFastLearner = new BlessingFastLearner();
		blessings.add(blessingFastLearner);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_COLORFUL_TIE_DESCRIPTION;	
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {return true;}
	
}
