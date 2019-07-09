/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.enums.DamageType;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.LootUtil;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * A scroll that destroys all destructible + deals damages depending on the distance to the player
 * @author Callil
 *
 */
public class ItemScrollOfDestruction extends AbstractItem {
	
	private int maxDamage = 16;
	
	public ItemScrollOfDestruction() {
		super(ItemEnum.SCROLL_DESTRUCTION, Assets.scroll_destruction_item, false, true);
		this.setPaper(true);
		setRecyclePrice(6);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_SCROLL_DESTRUCTION_DESCRIPTION;		
	}
	
	@Override
	public String getActionLabel() {
		return "Read";
	}
	
	@Override
	public boolean isStackable() {
		return true;
	}
	
	@Override
	public boolean use(final Entity user, Entity item, final Room room) {
		Journal.addEntry("A powerful blast surged from your position.");

		GridPositionComponent userPos = Mappers.gridPositionComponent.get(user);
		
		for (Entity e: room.getAllies()) {
			if (e == user) continue;
			damageOneEntity(user, e, userPos, room);
		}
		
		for (Entity e: room.getEnemies()) {
			if (e == user) continue;
			damageOneEntity(user, e, userPos, room);
		}
		
		Array<Entity> copyOfAllEntities = new Array<>(room.getAllEntities());
		copyOfAllEntities.forEach(entity -> LootUtil.destroy(entity, room));

		return true;
	}
	
	
	public void damageOneEntity(Entity user, Entity entityToDamage, GridPositionComponent userPos, Room room) {
		GridPositionComponent entityPos = Mappers.gridPositionComponent.get(entityToDamage);
		int dist = TileUtil.getDistanceBetweenTiles(userPos.coord(), entityPos.coord());

		int damage = Math.max(1, maxDamage - dist);
		room.attackManager.applyDamage(user, entityToDamage, damage, DamageType.EXPLOSION, null);
	}


}
