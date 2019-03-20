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
 * A death orb
 * @author Callil
 *
 */
public class ItemOrbDeath extends ItemOrb {

	public ItemOrbDeath() {
		super(ItemEnum.DEATH_ORB, Assets.death_orb_item);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ORB_DEATH_DESCRIPTION;
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}

	@Override
	public Entity getOrb(Room room) {
		return room.entityFactory.orbFactory.createDeathOrb(null, room);
	}



}
