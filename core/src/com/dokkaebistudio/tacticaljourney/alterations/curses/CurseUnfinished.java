/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.curses;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.vfx.VFXUtil;

/**
 * Curse of the unfinished: destroy papers/scrolls on contact.
 * @author Callil
 *
 */
public class CurseUnfinished extends Curse {

	@Override
	public String title() {
		return "Curse of the unfinished";
	}
	
	@Override
	public String description() {
		return "Paper items such as pages or scrolls cannot be manipulated and are destroyed on pickup.";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.curse_unfinished;
	}

	
	@Override
	public int onPickupItem(Entity picker, Entity item, Room room) {
		int result = checkItemType(picker, item, room);
		if (result < 0) {
			room.removeEntity(item);
		}
		return result;
	}

	
	public int onUseItem(Entity user, Entity item, Room room) {
		int result = checkItemType(user, item, room);
		if(result < 0) {
			Mappers.inventoryComponent.get(user).remove(item);
			room.removeEntity(item);
		}
		return result;
	}
	

	// Util methods
	
	private int checkItemType(Entity picker, Entity item, Room room) {
		int result = 0;
		AbstractItem itemType = Mappers.itemComponent.get(item).getItemType();
		if (itemType.isPaper()) {
			VFXUtil.createDisappearanceEffect(Mappers.gridPositionComponent.get(picker).coord(), Mappers.spriteComponent.get(item).getSprite());
			AlterationSystem.addAlterationProc(this);
			Journal.addEntry("[RED]Curse of the Unfinished destroyed the " + Journal.getLabel(item));
			result = -100;
		}
		return result;
	}
}
