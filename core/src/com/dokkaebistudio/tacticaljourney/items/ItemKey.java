/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.rendering.HUDRenderer;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Add arrows to the player's quiver.
 * @author Callil
 *
 */
public class ItemKey extends Item {

	public ItemKey() {
		super("Staircase key", Assets.key, false, false);
	}

	@Override
	public String getDescription() {
		return "The key to the lower floor.";
	}
	
	@Override
	public String getActionLabel() {return null;}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(user);
		inventoryComponent.getKey();
		
		Journal.addEntry("[GREEN]You picked up the staircase key.");

		return true;
	}
	
	@Override
	public Vector2 getPickupImageMoveDestination() {
		return HUDRenderer.POS_KEY_SLOT;
	}
}
