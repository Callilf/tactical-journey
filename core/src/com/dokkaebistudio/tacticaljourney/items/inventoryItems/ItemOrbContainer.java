/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.orbs.OrbCarrierComponent;
import com.dokkaebistudio.tacticaljourney.items.Item;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * An orb container.
 * @author Callil
 *
 */
public class ItemOrbContainer extends Item {
	
	public ItemOrbContainer() {
		super(ItemEnum.ORB_CONTAINER, Assets.orb_container_item, false, true);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_ORB_CONTAINER_DESCRIPTION;	
	}
	
	@Override
	public String getActionLabel() {
		return "Open";
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {
		Journal.addEntry("You opened the orb container");
		
		OrbCarrierComponent orbCarrierComponent = Mappers.orbCarrierComponent.get(user);
		RandomXS128 unseededRandom = RandomSingleton.getInstance().getUnseededRandom();
		
		int randInt = unseededRandom.nextInt(3);
		if (randInt == 0) {
			orbCarrierComponent.acquire(user, room.entityFactory.orbFactory.createEnergyOrb(null, room));
		} else if (randInt == 1) {
			orbCarrierComponent.acquire(user, room.entityFactory.orbFactory.createVegetalOrb(null, room));
		} else {
			orbCarrierComponent.acquire(user, room.entityFactory.orbFactory.createPoisonOrb(null, room));
		}

		return true;
	}
}
