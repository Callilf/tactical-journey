/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.statuses.debuffs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.enums.HealthChangeEnum;
import com.dokkaebistudio.tacticaljourney.factory.EntityFlagEnum;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemVegetalGarment;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * The entity is stuck on the current tile.
 * @author Callil
 *
 */
public class StatusDebuffEntangled extends Status {

	private Entity vines;
	
	public StatusDebuffEntangled(int duration) {
		this.setDuration(duration);
	}
	
	
	@Override
	public String title() {
		return "[FOREST]Entangled[]";
	}

	@Override
	public String description() {
		return "Cannot move, but can still attack.";
	}
	
	@Override
	public AtlasRegion texture() {
		return Assets.status_entangled;
	}
	@Override
	public AtlasRegion fullTexture() {
		return Assets.status_entangled_full;
	}
	
	@Override
	public boolean onReceive(Entity entity, Room room) {

		boolean immune = false;
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(entity);
		immune = inventoryComponent != null && inventoryComponent.contains(ItemVegetalGarment.class);

		if (immune) {
			HealthComponent healthComponent = Mappers.healthComponent.get(entity);
			if (healthComponent != null) {
				healthComponent.healthChangeMap.put(HealthChangeEnum.RESISTANT, "ENTANGLED IMMUNITY");
			}
			return false;
		}
		
		
		MoveComponent moveComponent = Mappers.moveComponent.get(entity);
		moveComponent.setFrozen(true);

		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(entity);
		this.vines = room.entityFactory.createSpriteOnTile(
				gridPositionComponent.coord(), ZIndexConstants.VINES, Assets.entangled_vines, EntityFlagEnum.WALL, room);
		
		return true;
	}
	
	
	@Override
	public void onEndTurn(Entity entity, Room room) {
		boolean immune = false;
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(entity);
		immune = inventoryComponent != null && inventoryComponent.contains(ItemVegetalGarment.class);

		if (immune) {
			HealthComponent healthComponent = Mappers.healthComponent.get(entity);
			if (healthComponent != null) {
				healthComponent.healthChangeMap.put(HealthChangeEnum.RESISTANT, "ENTANGLED IMMUNITY");
			}
			Mappers.statusReceiverComponent.get(entity).requestAction(StatusActionEnum.REMOVE_STATUS, this);
		}
	}
	
	
	@Override
	public void onRemove(Entity entity, Room room) {
		MoveComponent moveComponent = Mappers.moveComponent.get(entity);
		moveComponent.setFrozen(false);

		room.removeEntity(vines);
	}
	
	@Override
	public void onDeath(Entity entity, Room room) {
		room.removeEntity(vines);
	}
	
}
