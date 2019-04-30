/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory;

import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.FlammableComponent;
import com.dokkaebistudio.tacticaljourney.components.InspectableComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate.ItemPoolRarity;
import com.dokkaebistudio.tacticaljourney.components.loot.LootableComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootableComponent.LootableStateEnum;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.enums.LootableEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPoolSingleton;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.LootUtil;

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
	public Entity createBones(Room room, Vector2 pos) {
		Entity remainsEntity = engine.createEntity();
		remainsEntity.flags = EntityFlagEnum.LOOTABLE_BONES.getFlag();

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.LOOTABLE_OLD_BONES_TITLE);
		inspect.setDescription(Descriptions.LOOTABLE_OLD_BONES_DESCRIPTION);
		remainsEntity.add(inspect);
		
    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(remainsEntity, pos, room);
    	movableTilePos.zIndex = ZIndexConstants.LOOTABLE;
    	remainsEntity.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(LootableEnum.BONES.getClosedTexture());
    	remainsEntity.add(spriteCompo);

    	LootableComponent lootComponent = engine.createComponent(LootableComponent.class);
    	lootComponent.setType(LootableEnum.BONES);
    	lootComponent.setItemPool(ItemPoolSingleton.getInstance().oldBones);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 5);
		dropRate.add(ItemPoolRarity.COMMON, 50);
		lootComponent.setDropRate(dropRate);
		lootComponent.setSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
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
	public Entity createSatchel(Room room, Vector2 pos) {
		Entity remainsEntity = engine.createEntity();
		remainsEntity.flags = EntityFlagEnum.LOOTABLE_SATCHEL.getFlag();

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.LOOTABLE_SATCHEL_TITLE);
		inspect.setDescription(Descriptions.LOOTABLE_SATCHEL_DESCRIPTION);
		remainsEntity.add(inspect);
		
    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(remainsEntity, pos, room);
    	movableTilePos.zIndex = ZIndexConstants.LOOTABLE;
    	remainsEntity.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(LootableEnum.SATCHEL.getClosedTexture());
    	remainsEntity.add(spriteCompo);
    	
    	LootableComponent lootComponent = engine.createComponent(LootableComponent.class);
    	lootComponent.setType(LootableEnum.SATCHEL);
    	lootComponent.setItemPool(ItemPoolSingleton.getInstance().satchel);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 5);
		dropRate.add(ItemPoolRarity.COMMON, 70);
		lootComponent.setDropRate(dropRate);
		lootComponent.setSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
    	lootComponent.setMaxNumberOfItems(3);
    	lootComponent.setLootableState(LootableStateEnum.CLOSED, null);
    	remainsEntity.add(lootComponent);
    	
    	DestructibleComponent destructibleCompo = engine.createComponent(DestructibleComponent.class);
    	remainsEntity.add(destructibleCompo);

		engine.addEntity(remainsEntity);

    	return remainsEntity;
	}

	/**
	 * Create personal belongings
	 * @param room the room
	 * @param pos the tile position
	 * @return the lootable
	 */
	public Entity createPersonalBelongings(Room room, Vector2 pos) {
		Entity lootable = engine.createEntity();
		lootable.flags = EntityFlagEnum.LOOTABLE_BELONGINGS.getFlag();

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.LOOTABLE_PERSONAL_BELONGINGS_TITLE);
		inspect.setDescription(Descriptions.LOOTABLE_PERSONAL_BELONGINGS_DESCRIPTION);
		lootable.add(inspect);
		
    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(lootable, pos, room);
    	movableTilePos.zIndex = ZIndexConstants.LOOTABLE;
    	lootable.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(LootableEnum.PERSONAL_BELONGINGS.getClosedTexture());
    	lootable.add(spriteCompo);
    	
    	LootableComponent lootComponent = engine.createComponent(LootableComponent.class);
    	lootComponent.setType(LootableEnum.PERSONAL_BELONGINGS);
    	lootComponent.setItemPool(ItemPoolSingleton.getInstance().personalBelongings);
		DropRate dropRate = new DropRate();
//		dropRate.add(ItemPoolRarity.RARE, 0);
		dropRate.add(ItemPoolRarity.COMMON, 100);
		lootComponent.setDropRate(dropRate);
		lootComponent.setSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
    	lootComponent.setMaxNumberOfItems(1);
    	lootComponent.setLootableState(LootableStateEnum.CLOSED, null);
    	lootable.add(lootComponent);
    	
    	DestructibleComponent destructibleCompo = engine.createComponent(DestructibleComponent.class);
    	lootable.add(destructibleCompo);
    	
		FlammableComponent flammable = engine.createComponent(FlammableComponent.class);
		flammable.setPropagate(true);
		flammable.setDestroy(false);
		lootable.add(flammable);

		engine.addEntity(lootable);

    	return lootable;
	}
	
	
	/**
	 * Create an orb bag
	 * @param room the room
	 * @param pos the tile position
	 * @return the lootable
	 */
	public Entity createOrbBag(Room room, Vector2 pos) {
		Entity lootable = engine.createEntity();
		lootable.flags = EntityFlagEnum.LOOTABLE_ORB_BAG.getFlag();

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.LOOTABLE_ORB_BAG_TITLE);
		inspect.setDescription(Descriptions.LOOTABLE_ORB_BAG_DESCRIPTION);
		lootable.add(inspect);
		
    	GridPositionComponent movableTilePos = engine.createComponent(GridPositionComponent.class);
    	movableTilePos.coord(lootable, pos, room);
    	movableTilePos.zIndex = ZIndexConstants.LOOTABLE;
    	lootable.add(movableTilePos);
    	
    	SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
    	spriteCompo.setSprite(LootableEnum.ORB_BAG.getClosedTexture());
    	lootable.add(spriteCompo);
    	
    	LootableComponent lootComponent = engine.createComponent(LootableComponent.class);
    	lootComponent.setType(LootableEnum.ORB_BAG);
    	lootComponent.setItemPool(ItemPoolSingleton.getInstance().orbBag);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 2);
		dropRate.add(ItemPoolRarity.COMMON, 70);
		lootComponent.setDropRate(dropRate);
		lootComponent.setSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
    	lootComponent.setMaxNumberOfItems(3);
    	lootComponent.setLootableState(LootableStateEnum.CLOSED, null);
    	lootable.add(lootComponent);
    	
    	DestructibleComponent destructibleCompo = engine.createComponent(DestructibleComponent.class);
    	lootable.add(destructibleCompo);

		engine.addEntity(lootable);

    	return lootable;
	}
	
}
