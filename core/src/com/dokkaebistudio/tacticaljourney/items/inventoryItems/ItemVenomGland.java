/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.BlockExplosionComponent;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.items.Item;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * A potion that bursts into flames when used or thrown.
 * @author Callil
 *
 */
public class ItemVenomGland extends Item {

	public ItemVenomGland() {
		super("Venom gland", Assets.venom_gland_item, false, true);
		this.type = ItemEnum.VENOM_GLAND;
	}
	
	@Override
	public String getDescription() {
		return "A gland full of venom that will probably explode when thrown.";		
	}
	
	@Override
	public String getActionLabel() {
		return null;
	}
	
	@Override
	public boolean isStackable() {
		return true;
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
		
		room.entityFactory.creepFactory.createPoison(room, thrownPosition, thrower);
		
		List<Tile> adjacentTiles = TileUtil.getAdjacentTiles(thrownPosition, room);
		for (Tile tile : adjacentTiles) {
			if (tile.isThrowable(thrower)) {
				boolean canHavePoison = true;
				Entity creepAlreadyThere = TileUtil.getEntityWithComponentOnTile(tile.getGridPos(), CreepComponent.class, room);
				if (creepAlreadyThere != null) {
					// Replace any creep on this tile
					room.removeEntity(creepAlreadyThere);
				}
				
				Entity wall = TileUtil.getEntityWithComponentOnTile(tile.getGridPos(), BlockExplosionComponent.class, room);
				if (wall != null) {
					canHavePoison = false;
				}
			
				if (canHavePoison) {
					room.entityFactory.creepFactory.createPoison(room, tile.getGridPos(), thrower);
				}
			}
		}
	}
}
