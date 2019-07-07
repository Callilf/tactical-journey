/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.creeps;

import java.util.Collection;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.FlammableComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ParentEntityComponent;
import com.dokkaebistudio.tacticaljourney.enums.DamageType;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffBurning;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * Lava that burns and deals damages.
 * @author Callil
 *
 */
public class CreepLava extends Creep {
	
	public CreepLava() {
		super("Lava", Assets.creep_lava);
		type = CreepType.LAVA;
	}
	
	

	@Override
	public void onWalk(Entity walker, Entity creep, Room room) {
		if (isImmune(walker)) return;

		ParentEntityComponent parentEntityCompo = Mappers.parentEntityComponent.get(creep);
		Entity parent = parentEntityCompo != null ? parentEntityCompo.getParent() : null;

		HealthComponent healthComponent = Mappers.healthComponent.get(walker);
		if (healthComponent != null) {
			healthComponent.hit(10, creep, parent, DamageType.FIRE);
		}
		
		// burning status
		StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(walker);
		if (statusReceiverComponent != null) {
			statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffBurning(parent));
		}
	}
	
	
	@Override
	public void onAppear(Entity creep, Room room) {
		super.onAppear(creep, room);
		
		ParentEntityComponent parentEntityCompo = Mappers.parentEntityComponent.get(creep);
		Entity parent = parentEntityCompo != null ? parentEntityCompo.getParent() : null;

		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(creep);
		
		// Give the burning status
		Set<Entity> entities = TileUtil.getEntitiesWithComponentOnTile(gridPositionComponent.coord(), StatusReceiverComponent.class, room);
		for (Entity e : entities) {
			if (isImmune(e)) continue;
			StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(e);
			if (statusReceiverComponent != null) {
				statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffBurning(parent));
			}
		}
		
		// Set ablaze any flammable entity on the current tile
		Collection<Entity> flammables = TileUtil.getEntitiesWithComponentOnTile(gridPositionComponent.coord(), FlammableComponent.class, room);
		for (Entity flammable : flammables) {
			FlammableComponent flammableComponent = Mappers.flammableComponent.get(flammable);
			flammableComponent.setBurning(true);
			if (flammableComponent.isDestroy()) {
				
				Image destroyedTexture = flammableComponent.getDestroyedTexture(gridPositionComponent.getWorldPos());
				if (destroyedTexture != null) {
					GameScreen.fxStage.addActor(destroyedTexture);
				}
				
				room.removeEntity(flammable);
			}
		}
		
		entities = TileUtil.getEntitiesWithComponentOnTile(gridPositionComponent.coord(), HealthComponent.class, room);
		for (Entity e : entities) {
			if (isImmune(e)) continue;

			HealthComponent healthComponent = Mappers.healthComponent.get(e);
			if (healthComponent != null) {
				healthComponent.hit(10, creep, parent, DamageType.FIRE);
			}
		}

	}
	
	@Override
	public int getHeuristic(Entity mover) {
		if (isImmune(mover)) {
			return 0;
		} else {
			return 30;
		}
	}

}
