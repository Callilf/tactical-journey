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
public class ItemOrbPoison extends ItemOrb {

	public ItemOrbPoison() {
		super("Poison orb", Assets.poison_orb_item);
		this.type = ItemEnum.POISON_ORB;
	}
	
	@Override
	public String getDescription() {
		return "An orb that poison for 5 turns on contact.";
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
