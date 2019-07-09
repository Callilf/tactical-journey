package com.dokkaebistudio.tacticaljourney.leveling.infusable;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class LevelUpAlterationRewardPurityPotion extends AbstractLevelUpAlterationReward {

	
	public LevelUpAlterationRewardPurityPotion(RandomXS128 levelUpSeededRandom) {
		super("Receive a potion of purity", "Received a potion of purity");
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
		Entity potion = room.entityFactory.itemFactory.createItem(ItemEnum.POTION_PURITY);
		ItemComponent potionItemCompo = Mappers.itemComponent.get(potion);
		boolean pickedUp = potionItemCompo.pickUp(player, potion, room);
		if (pickedUp) {
			// nothing to do
		} else {
			// place it on the floor
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(player);
			potionItemCompo.drop(gridPositionComponent.coord(), potion, room);
		}
	}


	@Override
	public void computeValue() {}

}
