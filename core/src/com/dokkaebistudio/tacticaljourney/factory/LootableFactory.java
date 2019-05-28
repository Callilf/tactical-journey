/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.FlammableComponent;
import com.dokkaebistudio.tacticaljourney.components.GravityComponent;
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
	 * Create the base of a lootable.
	 * @param room the room
	 * @param pos the position
	 * @return the lootable entity
	 */
	private Entity createLootableBase(Room room, Vector2 pos, EntityFlagEnum flag, LootableEnum lootableEnum) {
		Entity lootableEntity = engine.createEntity();
		lootableEntity.flags = flag.getFlag();

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		if (lootableEnum != null) spriteCompo.setSprite(lootableEnum.getClosedTexture());
		lootableEntity.add(spriteCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(lootableEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.LOOTABLE;
		lootableEntity.add(gridPosition);
		
		GravityComponent gravityCompo = engine.createComponent(GravityComponent.class);
		lootableEntity.add(gravityCompo);
		
		return lootableEntity;
	}
	
	
	/**
	 * Create a lootable skeleton.
	 * @param room the room
	 * @param pos the tile position
	 * @return the lootable bones
	 */
	public Entity createBones(Room room, Vector2 pos) {
		Entity remainsEntity = createLootableBase(room, pos, EntityFlagEnum.LOOTABLE_BONES, LootableEnum.BONES);
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.LOOTABLE_OLD_BONES_TITLE);
		inspect.setDescription(Descriptions.LOOTABLE_OLD_BONES_DESCRIPTION);
		remainsEntity.add(inspect);

    	LootableComponent lootComponent = engine.createComponent(LootableComponent.class);
    	lootComponent.setType(LootableEnum.BONES);
    	lootComponent.setItemPool(ItemPoolSingleton.getInstance().oldBones);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 5f);
		dropRate.add(ItemPoolRarity.COMMON, 50f);
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
		Entity remainsEntity = createLootableBase(room, pos, EntityFlagEnum.LOOTABLE_SATCHEL, LootableEnum.SATCHEL);

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.LOOTABLE_SATCHEL_TITLE);
		inspect.setDescription(Descriptions.LOOTABLE_SATCHEL_DESCRIPTION);
		remainsEntity.add(inspect);
    	
    	LootableComponent lootComponent = engine.createComponent(LootableComponent.class);
    	lootComponent.setType(LootableEnum.SATCHEL);
    	lootComponent.setItemPool(ItemPoolSingleton.getInstance().satchel);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 5f);
		dropRate.add(ItemPoolRarity.COMMON, 70f);
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
		Entity lootable = createLootableBase(room, pos, EntityFlagEnum.LOOTABLE_BELONGINGS, LootableEnum.PERSONAL_BELONGINGS);

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.LOOTABLE_PERSONAL_BELONGINGS_TITLE);
		inspect.setDescription(Descriptions.LOOTABLE_PERSONAL_BELONGINGS_DESCRIPTION);
		lootable.add(inspect);
    	
    	LootableComponent lootComponent = engine.createComponent(LootableComponent.class);
    	lootComponent.setType(LootableEnum.PERSONAL_BELONGINGS);
    	lootComponent.setItemPool(ItemPoolSingleton.getInstance().personalBelongings);
		DropRate dropRate = new DropRate();
//		dropRate.add(ItemPoolRarity.RARE, 0);
		dropRate.add(ItemPoolRarity.COMMON, 100f);
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
		Entity lootable = createLootableBase(room, pos, EntityFlagEnum.LOOTABLE_ORB_BAG, LootableEnum.ORB_BAG);

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.LOOTABLE_ORB_BAG_TITLE);
		inspect.setDescription(Descriptions.LOOTABLE_ORB_BAG_DESCRIPTION);
		lootable.add(inspect);
    	
    	LootableComponent lootComponent = engine.createComponent(LootableComponent.class);
    	lootComponent.setType(LootableEnum.ORB_BAG);
    	lootComponent.setItemPool(ItemPoolSingleton.getInstance().orbBag);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 2f);
		dropRate.add(ItemPoolRarity.COMMON, 70f);
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
	
	
	/**
	 * Create a lootable spellbook.
	 * @param room the room
	 * @param pos the tile position
	 * @return the lootable bones
	 */
	public Entity createSpellBook(Room room, Vector2 pos) {
		Entity remainsEntity = createLootableBase(room, pos, EntityFlagEnum.LOOTABLE_SPELLBOOK, LootableEnum.SPELL_BOOK);

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(Descriptions.LOOTABLE_SPELL_BOOK_TITLE);
		inspect.setDescription(Descriptions.LOOTABLE_SPELL_BOOK_DESCRIPTION);
		remainsEntity.add(inspect);

    	LootableComponent lootComponent = engine.createComponent(LootableComponent.class);
    	lootComponent.setType(LootableEnum.SPELL_BOOK);
    	lootComponent.setItemPool(ItemPoolSingleton.getInstance().spellBook);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 20f);
		dropRate.add(ItemPoolRarity.COMMON, 70f);
		lootComponent.setDropRate(dropRate);
		lootComponent.setSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
    	lootComponent.setMaxNumberOfItems(2);
    	lootComponent.setLootableState(LootableStateEnum.CLOSED, null);
    	remainsEntity.add(lootComponent);
    	
    	DestructibleComponent destructibleCompo = engine.createComponent(DestructibleComponent.class);
    	remainsEntity.add(destructibleCompo);
    	
		FlammableComponent flammable = engine.createComponent(FlammableComponent.class);
		flammable.setPropagate(true);
		flammable.setDestroy(true);
		remainsEntity.add(flammable);
    	
		engine.addEntity(remainsEntity);

    	return remainsEntity;
	}
}
