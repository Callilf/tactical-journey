package com.dokkaebistudio.tacticaljourney.leveling.infusable;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPoolSingleton;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;
import com.dokkaebistudio.tacticaljourney.items.pools.lootables.PersonalBelongingsItemPool;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class LevelUpAlterationRewardReceiveInfusable extends AbstractLevelUpAlterationReward {

	private ItemEnum item;
	private RandomXS128 random;
	
	public LevelUpAlterationRewardReceiveInfusable(RandomXS128 levelUpSeededRandom) {
		super("Receive an infusable item", "Received #");
		this.random = levelUpSeededRandom;
	}
	
	
	public String getFinalDescription() {
		computeValue();
		
		if (finalDescription != null && valueStr != null) {
			return finalDescription.replace("#", valueStr);
		}
		return finalDescription;
	}

	@Override
	public void select(Entity player, Room room) {
		ItemPoolSingleton.getInstance().removeItemFromPools(item);
		
		Entity clonedItem = room.entityFactory.itemFactory.createItem(item);
		ItemComponent clonedItemCompo = Mappers.itemComponent.get(clonedItem);
		boolean pickedUp = clonedItemCompo.pickUp(player, clonedItem, room);
		if (pickedUp) {
			// nothing to do
		} else {
			// place it on the floor
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(player);
			clonedItemCompo.drop(gridPositionComponent.coord(), clonedItem, room);
		}
	}

	@Override
	public void computeValue() {
		PersonalBelongingsItemPool itemPool = ItemPoolSingleton.getInstance().personalBelongings;
		List<PooledItemDescriptor> itemTypes = itemPool.getItemTypes(1, false, this.random);
		PooledItemDescriptor pooledItemDescriptor = itemTypes.get(0);
		
		item = pooledItemDescriptor.getType();
		valueStr = item.getName();
	}

}
