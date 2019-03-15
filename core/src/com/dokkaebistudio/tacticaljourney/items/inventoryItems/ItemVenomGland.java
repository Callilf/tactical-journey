/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import java.util.List;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.components.BlockExplosionComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.items.Item;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffPoison;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * A potion that bursts into flames when used or thrown.
 * @author Callil
 *
 */
public class ItemVenomGland extends Item {

	public ItemVenomGland() {
		super(ItemEnum.VENOM_GLAND, Assets.venom_gland_item, false, true);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_VENOM_GLAND_DESCRIPTION;		
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

		// Create a puddle of poison on this location
		room.entityFactory.creepFactory.createPoison(room, thrownPosition, thrower);

		Set<Entity> entitiesWithComponentOnTile = TileUtil.getEntitiesWithComponentOnTile(thrownPosition, StatusReceiverComponent.class, room);
		if (entitiesWithComponentOnTile.isEmpty()) {
			// Nothing on this tile : Add poison puddles in the 4 adjacent tiles
						
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
			
		} else {
			// Entity on this tile, apply poison
			
			for (Entity e : entitiesWithComponentOnTile) {
				StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(e);
				statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffPoison(3, thrower));
			}
			
		}
	}
}
