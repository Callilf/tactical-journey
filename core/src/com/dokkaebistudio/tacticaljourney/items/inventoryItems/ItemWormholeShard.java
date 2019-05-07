/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * A shard that creates a portal when thrown bewteen the thower position and the targeted position.
 * @author Callil
 *
 */
public class ItemWormholeShard extends AbstractItem {

	public ItemWormholeShard() {
		super(ItemEnum.WORMHOLE_SHARD, Assets.wormhole_shard_item, false, true);
		setRecyclePrice(5);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_WORMHOLE_SHARD_DESCRIPTION;		
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
		
		GridPositionComponent throwerPos = Mappers.gridPositionComponent.get(thrower);
		room.entityFactory.createWormhole(room, thrownPosition, throwerPos.coord());
		room.entityFactory.createWormhole(room, throwerPos.coord(), thrownPosition);
	}
}
