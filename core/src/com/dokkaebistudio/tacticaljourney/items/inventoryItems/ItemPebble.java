/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import java.util.Optional;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
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
public class ItemPebble extends AbstractItem {

	public ItemPebble() {
		super(ItemEnum.PEBBLE, Assets.pebble_item, false, true);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_PEBBLE_DESCRIPTION;		
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
		
		Optional<Entity> enemyOpt = TileUtil.getEntityWithComponentOnTile(thrownPosition, EnemyComponent.class, room);
		if (enemyOpt.isPresent()) {
			Entity enemy = enemyOpt.get();
			Journal.addEntry(Journal.getLabel(enemy) + " looks mad at you");
	
			Mappers.aiComponent.get(enemy).setAlerted(true, enemy, thrower);
			room.removeEntity(item);
		}
	}
}
