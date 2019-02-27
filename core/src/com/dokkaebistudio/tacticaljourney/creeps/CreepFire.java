/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.creeps;

import java.util.Collection;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.FlammableComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ParentEntityComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffBurning;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * Fire that burns entities and propagate is inflammable entities are nearby.
 * @author Callil
 *
 */
public class CreepFire extends Creep {
	
	public CreepFire() {
		super("Fire", Assets.creep_fire);
		type = CreepType.FIRE;
	}
	
	
	@Override
	public boolean isImmune(Entity entity) {
		return Mappers.flyComponent.has(entity);
	}
	
	

	@Override
	public void onWalk(Entity walker, Entity creep, Room room) {
		if (isImmune(walker)) return;

//		HealthComponent healthComponent = Mappers.healthComponent.get(walker);
//		if (healthComponent != null) {
//			healthComponent.hit(3, creep);
//		}
		
		// 50% chances to get the burning status
		StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(walker);
		if (statusReceiverComponent != null) {
			RandomXS128 unseededRandom = RandomSingleton.getInstance().getUnseededRandom();
			int nextInt = unseededRandom.nextInt(100);
			if (nextInt < 50) {
				statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffBurning());
			}
		}
	}
	
	@Override
	public void onStop(Entity walker, Entity creep, Room room) {
		if (isImmune(walker)) return;
		
//		HealthComponent healthComponent = Mappers.healthComponent.get(walker);
//		if (healthComponent != null) {
//			healthComponent.hit(5, creep);
//		}
		
		// 100% chance to receive burning status
		StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(walker);
		if (statusReceiverComponent != null) {
			statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffBurning());
		}
	}
	
	/** Propagate. */
	@Override
	public void onEndTurn(Entity creep, Room room) {
		ParentEntityComponent parentEntityCompo = Mappers.parentEntityComponent.get(creep);

		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(creep);
		Collection<Entity> flammables = TileUtil.getAdjacentEntitiesWithComponent(gridPositionComponent.coord(), FlammableComponent.class, room);
		for (Entity flammable : flammables) {
			FlammableComponent flammableComponent = Mappers.flammableComponent.get(flammable);
			if (flammableComponent.isPropagate() && !flammableComponent.isBurning()) {
				flammableComponent.setBurning(true);
				GridPositionComponent flammablePos = Mappers.gridPositionComponent.get(flammable);
				Entity fire = room.entityFactory.creepFactory.createFire(	room, flammablePos.coord(), parentEntityCompo.getParent());
				
				// This is called at the end of the turn, so increase duration by one
				CreepComponent creepComponent = Mappers.creepComponent.get(fire);
				creepComponent.setDuration(creepComponent.getDuration() + 1);
			}
		}
		
		// Set ablaze any flammable entity on the current tile
		flammables = TileUtil.getEntitiesWithComponentOnTile(gridPositionComponent.coord(), FlammableComponent.class, room);
		for (Entity flammable : flammables) {
			FlammableComponent flammableComponent = Mappers.flammableComponent.get(flammable);
			flammableComponent.setBurning(true);
			if (flammableComponent.isDestroyed()) {
				
				Image destroyedTexture = flammableComponent.getDestroyedTexture(gridPositionComponent.getWorldPos());
				if (destroyedTexture != null) {
					room.floor.getGameScreen().fxStage.addActor(destroyedTexture);
				}
				
				room.removeEntity(flammable);
			}
		}
	}
	
	@Override
	public void onAppear(Entity creep, Room room) {
		ParentEntityComponent parentEntityCompo = Mappers.parentEntityComponent.get(creep);

		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(creep);
		
		// Damage entities on the current tile
		Set<Entity> healthEntities = TileUtil.getEntitiesWithComponentOnTile(gridPositionComponent.coord(), HealthComponent.class, room);
		for (Entity healthEntity : healthEntities) {
			if (isImmune(healthEntity)) continue;
			
			HealthComponent healthComponent = Mappers.healthComponent.get(healthEntity);
			if (healthComponent != null) {
				healthComponent.hit(3, creep);
			}
		}
		
		// Set ablaze any flammable entity on the current tile
		Collection<Entity> flammables = TileUtil.getEntitiesWithComponentOnTile(gridPositionComponent.coord(), FlammableComponent.class, room);
		for (Entity flammable : flammables) {
			FlammableComponent flammableComponent = Mappers.flammableComponent.get(flammable);
			flammableComponent.setBurning(true);
			if (flammableComponent.isDestroyed()) {
				
				Image destroyedTexture = flammableComponent.getDestroyedTexture(gridPositionComponent.getWorldPos());
				if (destroyedTexture != null) {
					room.floor.getGameScreen().fxStage.addActor(destroyedTexture);
				}
				
				room.removeEntity(flammable);
			}
		}
		
		// Set ablaze any flammable entity on adjacent tile
		flammables = TileUtil.getAdjacentEntitiesWithComponent(gridPositionComponent.coord(), FlammableComponent.class, room);
		for (Entity flammable : flammables) {
			FlammableComponent flammableComponent = Mappers.flammableComponent.get(flammable);
			if (flammableComponent != null && flammableComponent.isPropagate() && !flammableComponent.isBurning()) {
				flammableComponent.setBurning(true);
				GridPositionComponent flammablePos = Mappers.gridPositionComponent.get(flammable);
				Entity fire = room.entityFactory.creepFactory.createFire(	room, flammablePos.coord(), parentEntityCompo.getParent());
			}
		}
	}
	
	@Override
	public int getHeuristic(Entity mover) {
		if (isImmune(mover)) {
			return 0;
		} else {
			return 10;
		}
	}

}
