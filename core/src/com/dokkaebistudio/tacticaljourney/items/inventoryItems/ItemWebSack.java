/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingIndegistible;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.systems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.util.CreepUtil;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * A sack of web that creates a web creep on the floor on the tile it is thrown.
 * @author Callil
 *
 */
public class ItemWebSack extends AbstractItem {

	public ItemWebSack() {
		super(ItemEnum.WEB_SACK, Assets.web_sack_item, false, true);
		setRecyclePrice(1);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_WEB_SACK_DESCRIPTION;		
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
		
		if (CreepUtil.canSpawnWeb(thrownPosition, room)) {
			room.entityFactory.creepFactory.createWeb(room, thrownPosition);
		}
		
		AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(thrower);
		if (alterationReceiverComponent != null) {
			Blessing blessing = alterationReceiverComponent.getBlessing(BlessingIndegistible.class);
			if (blessing != null) {
				AlterationSystem.addAlterationProc(blessing);
				
				// Add more web
				List<Tile> adjacentTiles = TileUtil.getAdjacentTiles(thrownPosition, room);
				for (Tile tile : adjacentTiles) {
					if (CreepUtil.canSpawnWeb(tile.getGridPos(), room)) {
						room.entityFactory.creepFactory.createWeb(room, tile.getGridPos());
					}
				}
			}
		}
		
	}
}
