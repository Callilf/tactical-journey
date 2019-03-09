/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.statuses.debuffs;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.factory.EntityFlagEnum;
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
		return "Entangled";
	}

	@Override
	public AtlasRegion texture() {
		return Assets.status_entangled;
	}

	@Override
	public boolean onReceive(Entity entity, Room room) {
		MoveComponent moveComponent = Mappers.moveComponent.get(entity);
		moveComponent.setMoveModifier(-100);

		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(entity);
		this.vines = room.entityFactory.createSpriteOnTile(
				gridPositionComponent.coord(), ZIndexConstants.VINES, Assets.entangled_vines, EntityFlagEnum.WALL, room);
		
		return true;
	}
	
	@Override
	public void onRemove(Entity entity, Room room) {
		MoveComponent moveComponent = Mappers.moveComponent.get(entity);
		moveComponent.setMoveModifier(0);

		room.removeEntity(vines);
	}
	
	@Override
	public void onStartTurn(Entity entity, Room room) {
		MoveComponent moveComponent = Mappers.moveComponent.get(entity);
		moveComponent.setMoveRemaining(0);
	}
	
}
