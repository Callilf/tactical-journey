/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.orbs;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * A poison orb
 * @author Callil
 *
 */
public class ItemOrbFire extends ItemOrb {

	public ItemOrbFire() {
		super(ItemEnum.FIRE_ORB, Assets.fire_orb_item);
	}
	
	@Override
	public String getDescription() {
		return "An orb that inclicts the [ORANGE]burning[] status effect on contact, and generate [ORANGE]fire creep[] in a large area in front of the orb holder.";
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}

	@Override
	public Entity getOrb(Room room) {
		return room.entityFactory.orbFactory.createFireOrb(null, room);
	}



}
