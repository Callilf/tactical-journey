/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
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
		super(ItemEnum.PEBBLE, Assets.pebble_item, false, true);
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
