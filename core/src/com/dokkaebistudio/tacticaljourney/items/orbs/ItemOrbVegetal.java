/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.orbs;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * A vegetal orb
 * @author Callil
 *
 */
public class ItemOrbVegetal extends ItemOrb {

	public ItemOrbVegetal() {
		super("Vegetal orb", Assets.vegetal_orb_item);
		this.type = ItemEnum.VEGETAL_ORB;
	}
	
	@Override
	public String getDescription() {
		return "An orb that entangles for 5 turns on contact.";
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}

	@Override
	public Entity getOrb(Room room) {
		return room.entityFactory.orbFactory.createVegetalOrb(null, room);
	}



}
