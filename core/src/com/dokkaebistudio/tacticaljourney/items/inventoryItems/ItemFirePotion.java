/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import java.util.List;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffBurning;
import com.dokkaebistudio.tacticaljourney.util.CreepUtil;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * A potion that bursts into flames when used or thrown.
 * @author Callil
 *
 */
public class ItemFirePotion extends AbstractItem {

	public ItemFirePotion() {
		super(ItemEnum.POTION_FIRE, Assets.fire_potion_item, false, true);
		setRecyclePrice(3);
	}
	
	@Override
	public String getDescription() {
		return "Contains a very volatile liquid that will burst into flames when released.";		
	}
	
	@Override
	public String getActionLabel() {
		return "[SCARLET]Drink";
	}
	
	@Override
	public boolean isStackable() {
		return true;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {
		Journal.addEntry("You drank the fire potion.");

		//set ablaze	
		StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(user);
		statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffBurning());
		
		return true;
	}
	
	@Override
	public void onThrow(Vector2 thrownPosition, Entity thrower, Entity item, Room room) {
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(thrower);
		if (inventoryComponent != null) {
			inventoryComponent.remove(item);
		}
		
		// Burning status on entities
		Set<Entity> entities = TileUtil.getEntitiesWithComponentOnTile(thrownPosition, StatusReceiverComponent.class, room);
		for (Entity entity : entities) {
			StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(entity);
			if (statusReceiverComponent != null) {
				statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffBurning(thrower));
			}
		}
		
		// Create a fire on the tile
		room.entityFactory.creepFactory.createFire(room, thrownPosition, thrower);
		
		// Create fires on adjacent tiles
		List<Tile> adjacentTiles = TileUtil.getAdjacentTiles(thrownPosition, room);
		for (Tile tile : adjacentTiles) {
			if (tile.isUnblockedGround(thrower)) {
				boolean canCatchFire = CreepUtil.canCatchFire(tile.getGridPos(), room);
				if (canCatchFire) {
					room.entityFactory.creepFactory.createFire(room, tile.getGridPos(), thrower);
				}
			}
		}
	}
}
