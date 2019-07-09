/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.orbs;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.ces.components.orbs.OrbCarrierComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * A potion that bursts into flames when used or thrown.
 * @author Callil
 *
 */
public abstract class ItemOrb extends AbstractItem {

	public ItemOrb(ItemEnum type, RegionDescriptor asset) {
		super(type, asset, false, false);
	}
	
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean isStackable() {
		return false;
	}
	
	
	public abstract Entity getOrb(Room room);
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {
		OrbCarrierComponent orbCarrierComponent = Mappers.orbCarrierComponent.get(user);
		orbCarrierComponent.acquire(user, getOrb(room));
		return true;
	}
}
