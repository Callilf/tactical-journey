/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.items.Item;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * A potion that bursts into flames when used or thrown.
 * @author Callil
 *
 */
public class ItemPebble extends Item {

	public ItemPebble() {
		super("Pebble", Assets.pebble_item, false, true, 1, 4);
		this.type = ItemEnum.PEBBLE;
	}
	
	@Override
	public String getDescription() {
		return "A common pebble, nothing interesting about it. You could try throwing them at enemies to piss them off though.";		
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
	public boolean pickUp(Entity picker, Entity item, Room room) {
		Integer quantity = Mappers.itemComponent.get(item).getQuantity();
		boolean pickedUp = false;
		for (int i=0 ; i < quantity ; i++) {
			Entity pebble = null;
			if (i == quantity - 1) {
				pebble = item;
			} else {
				pebble = room.entityFactory.itemFactory.createItemPebble(room, null);
				ItemComponent itemComponent = Mappers.itemComponent.get(pebble);
				itemComponent.setQuantity(1);
			}
			pickedUp = super.pickUp(picker, pebble, room);
			if (!pickedUp) break;
		}
		return pickedUp;
	}
	
	@Override
	public void onThrow(Vector2 thrownPosition, Entity thrower, Entity item, Room room) {
		super.onThrow(thrownPosition, thrower, item, room);
		
		Entity enemy = TileUtil.getEntityWithComponentOnTile(thrownPosition, EnemyComponent.class, room);
		if (enemy != null) {
			EnemyComponent enemyComponent = Mappers.enemyComponent.get(enemy);
			Journal.addEntry(enemyComponent.getType().title() + " looks mad at you");
	
			enemyComponent.setAlerted(true);
			room.removeEntity(item);
		}
	}
}
