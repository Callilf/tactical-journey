/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.BlockExplosionComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.TileComponent;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.creeps.Creep.CreepType;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * A potion that bursts into flames when used or thrown.
 * @author Callil
 *
 */
public class ItemFirePotion extends Item {

	public ItemFirePotion() {
		super("Fire potion", Assets.fire_potion_item, false, true);
	}
	
	@Override
	public String getDescription() {
		return "Contains a very volatile liquid that will burst into flames when released.";		
	}
	
	@Override
	public String getActionLabel() {
		return "Drink";
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {
		//Damage for 10 HP + set ablaze
		HealthComponent healthComponent = Mappers.healthComponent.get(user);
		healthComponent.hit(10, item);
		
		//TODO set ablaze
		return true;
	}
	
	@Override
	public void onThrow(Vector2 thrownPosition, Entity thrower, Entity item, Room room) {
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(thrower);
		if (inventoryComponent != null) {
			inventoryComponent.remove(item);
		}
		
		room.entityFactory.creepFactory.createFire(room, thrownPosition, thrower);
		List<Entity> adjacentTiles = TileUtil.getAdjacentEntitiesWithComponent(thrownPosition, TileComponent.class, room);
		for (Entity tile : adjacentTiles) {
			if (Mappers.tileComponent.get(tile).type.isWalkable()) {
				boolean canCatchFire = true;
				GridPositionComponent tilePos = Mappers.gridPositionComponent.get(tile);
				Entity creepAlreadyThere = TileUtil.getEntityWithComponentOnTile(tilePos.coord(), CreepComponent.class, room);
				if (creepAlreadyThere != null) {
					CreepComponent creepComponent = Mappers.creepComponent.get(creepAlreadyThere);
					if (creepComponent.getType().getType() == CreepType.FIRE) {
						//There is already fire on this tile, do nothing
						canCatchFire = false;
					}
				}
				
				if (canCatchFire) {
					Entity wall = TileUtil.getEntityWithComponentOnTile(tilePos.coord(), BlockExplosionComponent.class, room);
					if (wall != null) {
						canCatchFire = false;
					}
				}
				
				if (canCatchFire) {
					room.entityFactory.creepFactory.createFire(room, Mappers.gridPositionComponent.get(tile).coord(), thrower);
				}
			}
		}
	}
}