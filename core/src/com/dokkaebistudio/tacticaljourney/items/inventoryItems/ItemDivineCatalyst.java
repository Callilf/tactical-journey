/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing.BlessingsEnum;
import com.dokkaebistudio.tacticaljourney.alterations.pools.GoddessStatueAlterationPool;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent.AlterationActionEnum;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * A catalyst that has several uses. Breaking it gives a basic blessing, but it can be used by the soulbender to
 * infuse an item, or by other pnj to lift a curse, it can also be sold to the shopkeeper for 50gc.
 * @author Callil
 *
 */
public class ItemDivineCatalyst extends AbstractItem {

	public ItemDivineCatalyst() {
		super(ItemEnum.DIVINE_CATALYST, Assets.divine_catalyst_item, false, true);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_DIVINE_CATALYST_DESCRIPTION;		
	}
	
	@Override
	public String getActionLabel() {
		return "Break";
	}
	
	@Override
	public boolean isStackable() {
		return true;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {
		Journal.addEntry("You broke a Divine catalyst");
		
		// Receive basic blessing
		GoddessStatueAlterationPool alterationPool = new GoddessStatueAlterationPool();
		List<BlessingsEnum> blessingTypes = alterationPool.getBlessingTypes(1);
		Blessing blessing = Blessing.createBlessing(blessingTypes.get(0));
		
		AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(user);
		alterationReceiverComponent.requestAction(AlterationActionEnum.RECEIVE_BLESSING, blessing);
		
		return true;
	}
}
