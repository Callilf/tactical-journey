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
 * A void
 * @author Callil
 *
 */
public class ItemOrbVoid extends ItemOrb {

	public ItemOrbVoid() {
		super(ItemEnum.VOID_ORB, Assets.void_orb_item);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ORB_VOID_DESCRIPTION;
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}

	@Override
	public Entity getOrb(Room room) {
		return room.entityFactory.orbFactory.createVoid(null, room);
	}



}
