/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.FlammableComponent;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent.CreepReleasedTurnEnum;
import com.dokkaebistudio.tacticaljourney.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.components.player.ParentEntityComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.creeps.CreepFire;
import com.dokkaebistudio.tacticaljourney.creeps.CreepMud;
import com.dokkaebistudio.tacticaljourney.creeps.CreepPoison;
import com.dokkaebistudio.tacticaljourney.creeps.CreepWeb;
import com.dokkaebistudio.tacticaljourney.enums.AnimationsEnum;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Factory used to create presets of entities.
 * @author Callil
 *
 */
public final class CreepFactory {
	
	/** The gdx pooled engine. */
	public PooledEngine engine;
	
	/** the entity factory. */
	public EntityFactory entityFactory;
	
	/**
	 * Constructor.
	 * @param e the engine
	 */
	public CreepFactory(PooledEngine e, EntityFactory ef) {
		this.engine = e;
		this.entityFactory = ef;
	}
	
	
	/**
	 * Create a spider web that slows down and alert all spiders.
	 * @param room the room
	 * @param pos the position
	 * @return the creep entity
	 */
	private Entity createCreepBase(Room room, Vector2 pos, EntityFlagEnum flag, AtlasRegion texture) {
		Entity creepEntity = engine.createEntity();
		creepEntity.flags = flag.getFlag();

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		if (texture != null) spriteCompo.setSprite(new Sprite(texture));
		creepEntity.add(spriteCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(creepEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.CREEP;
		creepEntity.add(gridPosition);
		
		return creepEntity;
	}

	/**
	 * Create a spider web that slows down and alert all spiders.
	 * @param room the room
	 * @param pos the position
	 * @return the creep entity
	 */
	public Entity createWeb(Room room, Vector2 pos) {
		Entity creepEntity = createCreepBase(room, pos, EntityFlagEnum.CREEP_WEB, Assets.creep_web);
				
		DestructibleComponent destructible = engine.createComponent(DestructibleComponent.class);
		creepEntity.add(destructible);
		
		CreepComponent creepCompo = engine.createComponent(CreepComponent.class);
		creepCompo.setType(new CreepWeb());
		creepCompo.setDuration(3);
		CreepReleasedTurnEnum turnReleased = room.getState().isPlayerTurn() ? CreepReleasedTurnEnum.PLAYER : CreepReleasedTurnEnum.ENEMY;
		creepCompo.setReleasedTurn(turnReleased);
		creepEntity.add(creepCompo);
		
		FlammableComponent flammable = engine.createComponent(FlammableComponent.class);
		flammable.setPropagate(true);
		flammable.setDestroyed(true);
		flammable.setDestroyedTexture(Assets.creep_web);
		creepEntity.add(flammable);
		
		room.addEntity(creepEntity);		
		creepCompo.onAppear(creepEntity, room);
		return creepEntity;
	}
	
	
	
	public Entity createMud(Room room, Vector2 pos) {
		Entity creepEntity = createCreepBase(room, pos, EntityFlagEnum.CREEP_MUD, Assets.mud);
    	
    	DestructibleComponent destructibleCompo = engine.createComponent(DestructibleComponent.class);
    	destructibleCompo.setDestroyedTexture(Assets.mud_destroyed);
		creepEntity.add(destructibleCompo);
		
		CreepComponent creepCompo = engine.createComponent(CreepComponent.class);
		creepCompo.setType(new CreepMud());
		creepCompo.setDuration(0);
		creepEntity.add(creepCompo);
    	
		engine.addEntity(creepEntity);
		creepCompo.onAppear(creepEntity, room);
    	return creepEntity;
	}
	
	
	
	public Entity createFire(Room room, Vector2 pos, Entity parentEntity) {
		Entity creepEntity = createCreepBase(room, pos, EntityFlagEnum.CREEP_FIRE, null);
    			
		CreepComponent creepCompo = engine.createComponent(CreepComponent.class);
		creepCompo.setType(new CreepFire());
		creepCompo.setDuration(3);
		CreepReleasedTurnEnum turnReleased = room.getState().isPlayerTurn() ? CreepReleasedTurnEnum.PLAYER : CreepReleasedTurnEnum.ENEMY;
		creepCompo.setReleasedTurn(turnReleased);
		creepEntity.add(creepCompo);
		
		AnimationComponent animationCompo = engine.createComponent(AnimationComponent.class);
		animationCompo.animations.put(StatesEnum.FIRE_LOOP.getState(), AnimationsEnum.FIRE.getAnimation());
		creepEntity.add(animationCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(StatesEnum.FIRE_LOOP.getState() );
		creepEntity.add(stateCompo);
		
		if (parentEntity != null) {
			ParentEntityComponent parentCompo = engine.createComponent(ParentEntityComponent.class);
			parentCompo.setParent(parentEntity);
			creepEntity.add(parentCompo);
		}
    	
		engine.addEntity(creepEntity);
		creepCompo.onAppear(creepEntity, room);
    	return creepEntity;
	}
	
	/**
	 * Create a poison puddle.
	 * @param room the room
	 * @param pos the position
	 * @param duration the duration of the creep
	 * @return the creep entity
	 */
	public Entity createPoison(Room room, Vector2 pos, Entity parentEntity) {
		Entity creepEntity = createCreepBase(room, pos, EntityFlagEnum.CREEP_POISON, Assets.creep_poison);
				
		DestructibleComponent destructible = engine.createComponent(DestructibleComponent.class);
		creepEntity.add(destructible);
		
		CreepComponent creepCompo = engine.createComponent(CreepComponent.class);
		creepCompo.setType(new CreepPoison());
		creepCompo.setDuration(5);
		CreepReleasedTurnEnum turnReleased = room.getState().isPlayerTurn() ? CreepReleasedTurnEnum.PLAYER : CreepReleasedTurnEnum.ENEMY;
		creepCompo.setReleasedTurn(turnReleased);
		creepEntity.add(creepCompo);
		
		if (parentEntity != null) {
			ParentEntityComponent parentCompo = engine.createComponent(ParentEntityComponent.class);
			parentCompo.setParent(parentEntity);
			creepEntity.add(parentCompo);
		}
		
		room.addEntity(creepEntity);		
		creepCompo.onAppear(creepEntity, room);
		return creepEntity;
	}
	
}
