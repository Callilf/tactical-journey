/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.creep.CreepComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
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

	/**
	 * Constructor.
	 * @param e the engine
	 */
	public CreepFactory(PooledEngine e, EntityFactory ef) {
		this.engine = e;
		this.entityFactory = ef;
		
		webTexture = Assets.getTexture(Assets.creep_web);
	}
	

	/**
	 * Create a spider web that slows down and alert all spiders.
	 * @param room the room
	 * @param pos the position
	 * @return the creep entity
	 */
	public Entity createWeb(Room room, Vector2 pos) {
		Entity creepEntity = engine.createEntity();
		creepEntity.flags = EntityFlagEnum.CREEP_WEB.getFlag();

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(new Sprite(this.webTexture));
		creepEntity.add(spriteCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(creepEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.CREEP;
		creepEntity.add(gridPosition);
		
		DestructibleComponent destructible = engine.createComponent(DestructibleComponent.class);
		creepEntity.add(destructible);
		
		CreepComponent creepCompo = engine.createComponent(CreepComponent.class);
		creepCompo.setType(CreepEnum.WEB);
		creepCompo.setDuration(3);
		creepEntity.add(creepCompo);
		
		room.addEntity(creepEntity);
		
		return creepEntity;
	}
	
	
	
	public Entity createMud(Room room, Vector2 pos) {
		Entity creepEntity = engine.createEntity();
		creepEntity.flags = EntityFlagEnum.CREEP_WEB.getFlag();

    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(creepEntity, pos, room);
    	movableTilePos.zIndex = ZIndexConstants.CREEP;
    	creepEntity.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	Sprite s = new Sprite(Assets.getTexture(Assets.mud));
    	spriteCompo.setSprite(s);
    	creepEntity.add(spriteCompo);
    	
    	DestructibleComponent destructibleCompo = engine.createComponent(DestructibleComponent.class);
    	destructibleCompo.setDestroyedTexture(Assets.getTexture(Assets.mud_destroyed));
		creepEntity.add(destructibleCompo);
		
		CreepComponent creepCompo = engine.createComponent(CreepComponent.class);
		creepCompo.setType(CreepEnum.MUD);
		creepCompo.setDuration(0);
		creepEntity.add(creepCompo);

    	
		engine.addEntity(creepEntity);

    	return creepEntity;
	}
	
	
}
