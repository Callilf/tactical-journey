package com.dokkaebistudio.tacticaljourney.enums.creep;

import java.util.Collection;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.FlammableComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ParentEntityComponent;
import com.dokkaebistudio.tacticaljourney.enums.enemy.EnemyFactionEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;


/**
 * The enum of all the creep..
 * @author Callil
 *
 */
public enum CreepEnum {
		
	/** Spider web that slows player, gives free movement to spiders and alert all spiders of the room. */
	WEB("Spider web", Assets.creep_web) {
		
		@Override
		public void onWalk(Entity walker, Entity creep, Room room) {
			// If the player walks on it, all spiders are alerted
			if (Mappers.playerComponent.has(walker)) {
				for(Entity e : room.getEnemies()) {
					EnemyComponent enemyComponent = Mappers.enemyComponent.get(e);
					if (enemyComponent != null && enemyComponent.getFaction() == EnemyFactionEnum.SPIDERS) {
						enemyComponent.setAlerted(true);
					}
				}
			}
		}
		
		@Override
		public int getMovementConsumed(Entity mover) {
			if (Mappers.enemyComponent.has(mover)) {
				if (Mappers.enemyComponent.get(mover).getFaction() == EnemyFactionEnum.SPIDERS) return -1;
			}
			return 100;
		}

	},
	
	/** Mud that slows movements. */
	MUD("mud", Assets.mud) {
		
		@Override
		public void onWalk(Entity walker, Entity creep, Room room) {}
		
		@Override
		public int getMovementConsumed(Entity mover) {
			return 1;
		}

	},
	
	/** Fire that burns entities and propagate is inflammable entities are nearby. */
	FIRE("Fire", Assets.creep_fire) {
		
		@Override
		public void onWalk(Entity walker, Entity creep, Room room) {
			HealthComponent healthComponent = Mappers.healthComponent.get(walker);
			if (healthComponent != null) {
				healthComponent.hit(3, creep);
			}
		}
		
		@Override
		public void onStop(Entity walker, Entity creep, Room room) {
			HealthComponent healthComponent = Mappers.healthComponent.get(walker);
			if (healthComponent != null) {
				healthComponent.hit(10, creep);
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
			return 100;
		}

	};
	
	
	
	
	
	/** The name displayed. */
	private String label;
	/** The name of the image in the assets. */
	private AtlasRegion texture;
	
	/**
	 * Constructor for creep
	 * @param label
	 * @param texture
	 */
	CreepEnum(String label, AtlasRegion texture) {
		this.setLabel(label);
		this.setTexture(texture);
	}
	
	
	// Abstract methods
	
	/** Called when the item is used. */
	public abstract void onWalk(Entity walker, Entity creep, Room room);
	
	/** Called when the item is used. */
	public void onStop(Entity walker, Entity creep, Room room) {};
	
	/** Emit the creep. */
	public void onEmit(Entity emitter, Entity emittedCreep, Room room) {};
	
	/** Called when a turn is ended. */
	public void onEndTurn(Entity creep, Room room) {};
	
	/** Called when the creep is added to the game. */
	public void onAppear(Entity creep, Room room) {};
	
	/** Called when the creep disappears from the game. */
	public void onDisappear(Entity creep, Room room) {};
	
	/**
	 * Get the movement consumed when walking on this creep.
	 * @param mover the moving entity
	 * @return the number of movement consumed
	 */
	public int getMovementConsumed(Entity mover) {
		return 1;
	}
	
	/**
	 * Get the heuristic influence of walking on this creep.
	 * 0 means no influence
	 * a negative value is a good influence and the pathfinding will tend to use this tile
	 * a positive value is a bad influence and the pathfinding will tend to avoid this tile
	 * @param mover the moving entity
	 * @return the influence of this creep on the heuristic for the pathfinding.
	 */
	public int getHeuristic(Entity mover) {
		return 0;
	}
	
	
	
	// Getters and Setters

	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public AtlasRegion getTexture() {
		return texture;
	}


	public void setTexture(AtlasRegion texture) {
		this.texture = texture;
	}

}
