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
 * A vegetal orb
 * @author Callil
 *
 */
public class ItemOrbVegetal extends ItemOrb {

	public ItemOrbVegetal() {
		super(ItemEnum.VEGETAL_ORB, Assets.vegetal_orb_item);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ORB_VEGETAL_DESCRIPTION;
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
