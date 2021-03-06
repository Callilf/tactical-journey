/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.orbs;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * A poison orb
 * @author Callil
 *
 */
public class ItemOrbPoison extends ItemOrb {

	public ItemOrbPoison() {
		super(ItemEnum.POISON_ORB, Assets.poison_orb_item);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ORB_POISON_DESCRIPTION;
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}

	@Override
	public Entity getOrb(Room room) {
		return room.entityFactory.orbFactory.createPoisonOrb(null, room);
	}



}
