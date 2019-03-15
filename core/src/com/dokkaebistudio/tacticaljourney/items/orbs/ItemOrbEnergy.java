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
 * An energy orb
 * @author Callil
 *
 */
public class ItemOrbEnergy extends ItemOrb {

	public ItemOrbEnergy() {
		super(ItemEnum.ENERGY_ORB, Assets.energy_orb_item);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ORB_ENERGY_DESCRIPTION;		
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}

	@Override
	public Entity getOrb(Room room) {
		return room.entityFactory.orbFactory.createEnergyOrb(null, room);
	}



}
