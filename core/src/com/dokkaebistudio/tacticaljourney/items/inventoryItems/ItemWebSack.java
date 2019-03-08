/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.items.Item;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * A sack of web that creates a web creep on the floor on the tile it is thrown.
 * @author Callil
 *
 */
public class ItemWebSack extends Item {

	public ItemWebSack() {
		super("Small sack of web", Assets.web_sack_item, false, true);
		this.type = ItemEnum.WEB_SACK;
	}
	
	@Override
	public String getDescription() {
		return "A small sack of spider web. Nothing very interesting about it.";		
	}
	
	@Override
	public boolean isStackable() {
		return true;
	}
	
	/**
	 * Cannot be used ! Only thrown.
	 */
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {
		return true;
	}
	
	@Override
	public void onThrow(Vector2 thrownPosition, Entity thrower, Entity item, Room room) {
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(thrower);
		if (inventoryComponent != null) {
			inventoryComponent.remove(item);
		}
		
		room.entityFactory.creepFactory.createWeb(room, thrownPosition);
	}
}
