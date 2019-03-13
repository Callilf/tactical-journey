/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.leveling.items;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.leveling.LevelUpRewardEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * @author Callil
 *
 */
public class LevelUpItemRewardInventorySlot extends AbstractLevelUpItemReward {

	
	
	public LevelUpItemRewardInventorySlot() {
		super(1, 1, "Add 1 inventory\nslot", "Added 1 slot \nin inventory");
		this.type = LevelUpRewardEnum.INVENTORY_SLOT;
	}

	/* (non-Javadoc)
	 * @see com.dokkaebistudio.tacticaljourney.leveling.statsUp.AbstractLevelUpStatsUpReward#select(com.badlogic.ashley.core.Entity)
	 */
	@Override
	public void select(Entity player, Room room) {
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(player);
		inventoryComponent.setNumberOfSlots(inventoryComponent.getNumberOfSlots() + 1);
	}
	

}
