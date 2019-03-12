/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.orbs;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * An energy orb
 * @author Callil
 *
 */
public class ItemOrbEnergy extends ItemOrb {

	public ItemOrbEnergy() {
		super("Energy orb", Assets.energy_orb_item);
		this.type = ItemEnum.ENERGY_ORB;
	}
	
	@Override
	public String getDescription() {
		return "An orb that deals 10 damages on contact.";		
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
