/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.FlammableComponent;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.enums.AnimationsEnum;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.enums.creep.CreepEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

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
	
	// textures are stored so we don't fetch them from the atlas each time (atlas.findRegion is SLOW)
	private TextureAtlas.AtlasRegion webTexture;
	private TextureAtlas.AtlasRegion mudTexture;
	private TextureAtlas.AtlasRegion fireTexture;

	/**
	 * Constructor.
	 * @param e the engine
	 */
	public CreepFactory(PooledEngine e, EntityFactory ef) {
		this.engine = e;
		this.entityFactory = ef;
		
		webTexture = Assets.getTexture(Assets.creep_web);
		mudTexture = Assets.getTexture(Assets.mud);
		fireTexture = Assets.getTexture(Assets.creep_fire);
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
		Entity creepEntity = createCreepBase(room, pos, EntityFlagEnum.CREEP_WEB, this.webTexture);
				
		DestructibleComponent destructible = engine.createComponent(DestructibleComponent.class);
		creepEntity.add(destructible);
		
		CreepComponent creepCompo = engine.createComponent(CreepComponent.class);
		creepCompo.setType(CreepEnum.WEB);
		creepCompo.setDuration(3);
		creepEntity.add(creepCompo);
		
		FlammableComponent flammable = engine.createComponent(FlammableComponent.class);
		flammable.setPropagate(true);
		flammable.setDestroyed(true);
		creepEntity.add(flammable);
		
		room.addEntity(creepEntity);		
		creepCompo.onAppear(creepEntity, room);
		return creepEntity;
	}
	
	
	
	public Entity createMud(Room room, Vector2 pos) {
		Entity creepEntity = createCreepBase(room, pos, EntityFlagEnum.CREEP_MUD, this.mudTexture);
    	
    	DestructibleComponent destructibleCompo = engine.createComponent(DestructibleComponent.class);
    	destructibleCompo.setDestroyedTexture(Assets.getTexture(Assets.mud_destroyed));
		creepEntity.add(destructibleCompo);
		
		CreepComponent creepCompo = engine.createComponent(CreepComponent.class);
		creepCompo.setType(CreepEnum.MUD);
		creepCompo.setDuration(0);
		creepEntity.add(creepCompo);
    	
		engine.addEntity(creepEntity);
		creepCompo.onAppear(creepEntity, room);
    	return creepEntity;
	}
	
	
	
	public Entity createFire(Room room, Vector2 pos) {
		Entity creepEntity = createCreepBase(room, pos, EntityFlagEnum.CREEP_FIRE, null);
    			
		CreepComponent creepCompo = engine.createComponent(CreepComponent.class);
		creepCompo.setType(CreepEnum.FIRE);
		creepCompo.setDuration(2);
		creepEntity.add(creepCompo);
		
		AnimationComponent animationCompo = engine.createComponent(AnimationComponent.class);
		animationCompo.animations.put(StatesEnum.FIRE_LOOP.getState(), AnimationsEnum.FIRE.getAnimation());
		creepEntity.add(animationCompo);
		
		StateComponent stateCompo = engine.createComponent(StateComponent.class);
		stateCompo.set(StatesEnum.FIRE_LOOP.getState() );
		creepEntity.add(stateCompo);
    	
		engine.addEntity(creepEntity);
		creepCompo.onAppear(creepEntity, room);
    	return creepEntity;
	}
	
}
