/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootableComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootableComponent.LootableStateEnum;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.enums.LootableEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.lootables.AdventurersSatchelItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.lootables.OldBonesItemPool;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * Factory used to create presets of entities.
 * @author Callil
 *
 */
public final class LootableFactory {
	
	/** The gdx pooled engine. */
	public PooledEngine engine;
	
	/** the entity factory. */
	public EntityFactory entityFactory;
	
	/**
	 * Constructor.
	 * @param e the engine
	 */
	public LootableFactory(PooledEngine e, EntityFactory ef) {
		this.engine = e;
		this.entityFactory = ef;
	}
	
	
	/**
	 * Create a lootable skeleton.
	 * @param room the room
	 * @param pos the tile position
	 * @return the lootable bones
	 */
	public Entity createRemainsBones(Room room, Vector2 pos) {
		Entity remainsEntity = engine.createEntity();
		remainsEntity.flags = EntityFlagEnum.DOOR.getFlag();

    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(remainsEntity, pos, room);
    	movableTilePos.zIndex = ZIndexConstants.LOOTABLE;
    	remainsEntity.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	Sprite s = new Sprite(Assets.remains_bones);
    	spriteCompo.setSprite(s);
    	remainsEntity.add(spriteCompo);

    	LootableComponent lootComponent = engine.createComponent(LootableComponent.class);
    	lootComponent.setType(LootableEnum.BONES);
    	lootComponent.setItemPool(new OldBonesItemPool());
    	lootComponent.setMaxNumberOfItems(2);
    	lootComponent.setLootableState(LootableStateEnum.CLOSED, null);
    	remainsEntity.add(lootComponent);
    	
    	DestructibleComponent destructibleCompo = engine.createComponent(DestructibleComponent.class);
    	remainsEntity.add(destructibleCompo);
    	
		engine.addEntity(remainsEntity);

    	return remainsEntity;
	}
	
	/**
	 * Create a lootable satchel
	 * @param room the room
	 * @param pos the tile position
	 * @return the lootable satchel
	 */
	public Entity createRemainsSatchel(Room room, Vector2 pos) {
		Entity remainsEntity = engine.createEntity();
		remainsEntity.flags = EntityFlagEnum.DOOR.getFlag();

    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(remainsEntity, pos, room);
    	movableTilePos.zIndex = ZIndexConstants.LOOTABLE;
    	remainsEntity.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	Sprite s = new Sprite(LootableEnum.SATCHEL.getClosedTexture());
    	spriteCompo.setSprite(s);
    	remainsEntity.add(spriteCompo);
    	
    	LootableComponent lootComponent = engine.createComponent(LootableComponent.class);
    	lootComponent.setType(LootableEnum.SATCHEL);
    	lootComponent.setItemPool(new AdventurersSatchelItemPool());
    	lootComponent.setMaxNumberOfItems(3);
    	lootComponent.setLootableState(LootableStateEnum.CLOSED, null);
    	remainsEntity.add(lootComponent);
    	
    	DestructibleComponent destructibleCompo = engine.createComponent(DestructibleComponent.class);
    	remainsEntity.add(destructibleCompo);

		engine.addEntity(remainsEntity);

    	return remainsEntity;
	}

	
}
