/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.BlockVisibilityComponent;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemySpawnerComponent;
import com.dokkaebistudio.tacticaljourney.components.GravityComponent;
import com.dokkaebistudio.tacticaljourney.components.InspectableComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate.ItemPoolRarity;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.creature.enemies.enums.EnemyTypeEnum;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPoolSingleton;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * Factory used to create visual effects.
 * @author Callil
 *
 */
public final class DestructibleFactory {
	
	/** The gdx pooled engine. */
	public PooledEngine engine;
	
	/** the entity factory. */
	public EntityFactory entityFactory;
	

	/**
	 * Constructor.
	 * @param e the engine
	 */
	public DestructibleFactory(PooledEngine e, EntityFactory ef) {
		this.engine = e;
		this.entityFactory = ef;
	}
	
	
	public Entity createDestructibleBase(Room room, Vector2 pos, EntityFlagEnum flag, String title, String desc,
			RegionDescriptor region, RegionDescriptor destroyedRegion) {
		Entity vaseEntity = engine.createEntity();
		vaseEntity.flags = flag.getFlag();

		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(title);
		inspect.setDescription(desc);
		vaseEntity.add(inspect);

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(region);
		vaseEntity.add(spriteCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		gridPosition.coord(vaseEntity, pos, room);
		gridPosition.zIndex = ZIndexConstants.DESTRUCTIBLE;
		vaseEntity.add(gridPosition);
		
		SolidComponent solidComponent = engine.createComponent(SolidComponent.class);
		vaseEntity.add(solidComponent);
    	BlockVisibilityComponent blockVisibilityComponent = engine.createComponent(BlockVisibilityComponent.class);
    	vaseEntity.add(blockVisibilityComponent);

		DestructibleComponent destructibleComponent = engine.createComponent(DestructibleComponent.class);
		destructibleComponent.setDestroyedTexture(destroyedRegion);
		vaseEntity.add(destructibleComponent);
		
		GravityComponent gravityCompo = engine.createComponent(GravityComponent.class);
		vaseEntity.add(gravityCompo);
				
		engine.addEntity(vaseEntity);
		return vaseEntity;
	}

	/**
	 * Create a vase.
	 * @param pos the position
	 * @param moveSpeed the speed
	 * @return the enemy entity
	 */
	public Entity createVase(Room room, Vector2 pos) {
		int nextInt = RandomSingleton.getInstance().nextSeededInt(2);
		RegionDescriptor region = nextInt == 0 ? Assets.destructible_vase : Assets.destructible_vase_big;
		RegionDescriptor destroyedRegion = nextInt == 0 ? Assets.destructible_vase_destroyed : Assets.destructible_vase_big_destroyed;
		
		Entity vaseEntity = createDestructibleBase(room, pos, EntityFlagEnum.DESTRUCTIBLE_VASE,
					Descriptions.VASE_TITLE, Descriptions.VASE_DESCRIPTION, region, destroyedRegion);
			
			
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().vase);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 5f);
		dropRate.add(ItemPoolRarity.COMMON, 50f);
		lootRewardCompo.setDropRate(dropRate);
		lootRewardCompo.setDropSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
		vaseEntity.add(lootRewardCompo);
		
		EnemySpawnerComponent enemySpawnCompo = engine.createComponent(EnemySpawnerComponent.class);
		enemySpawnCompo.addSpawnChance(5, EnemyTypeEnum.SPIDER);
		vaseEntity.add(enemySpawnCompo);

		
		return vaseEntity;
	}
	
	/**
	 * Create a golden vase.
	 * @param pos the position
	 * @param moveSpeed the speed
	 * @return the enemy entity
	 */
	public Entity createGoldenVase(Room room, Vector2 pos) {		
		Entity vaseEntity = createDestructibleBase(room, pos, EntityFlagEnum.DESTRUCTIBLE_VASE,
					Descriptions.VASE_TITLE, Descriptions.VASE_DESCRIPTION, 
					Assets.destructible_golden_vase, Assets.destructible_vase_destroyed);
		
		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().goldenVase);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 10f);
		dropRate.add(ItemPoolRarity.COMMON, 90f);
		lootRewardCompo.setDropRate(dropRate);
		lootRewardCompo.setDropSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
		vaseEntity.add(lootRewardCompo);
		
		return vaseEntity;
	}
	
	/**
	 * Create an ammo crate.
	 * @param pos the position
	 * @param moveSpeed the speed
	 * @return the enemy entity
	 */
	public Entity createAmmoCrate(Room room, Vector2 pos) {
		Entity crateEntity = createDestructibleBase(room, pos, EntityFlagEnum.DESTRUCTIBLE_AMMO_CRATE,
				Descriptions.CRATE_TITLE, Descriptions.CRATE_DESCRIPTION, 
				Assets.destructible_ammo_crate, Assets.destructible_ammo_crate_destroyed);
		
		DestructibleComponent destructibleComponent = Mappers.destructibleComponent.get(crateEntity);
		destructibleComponent.setDestroyableWithWeapon(true);

		LootRewardComponent lootRewardCompo = engine.createComponent(LootRewardComponent.class);
		lootRewardCompo.setItemPool(ItemPoolSingleton.getInstance().ammoCrate);
		DropRate dropRate = new DropRate();
		dropRate.add(ItemPoolRarity.RARE, 5f);
		dropRate.add(ItemPoolRarity.COMMON, 95f);
		lootRewardCompo.setDropRate(dropRate);
		lootRewardCompo.setDropSeededRandom(RandomSingleton.getInstance().getNextSeededRandom());
		crateEntity.add(lootRewardCompo);
		
		EnemySpawnerComponent enemySpawnCompo = engine.createComponent(EnemySpawnerComponent.class);
		enemySpawnCompo.addSpawnChance(1, EnemyTypeEnum.SCORPION);
		enemySpawnCompo.addSpawnChance(2, EnemyTypeEnum.STINGER);
		enemySpawnCompo.addSpawnChance(7, EnemyTypeEnum.SPIDER);
		crateEntity.add(enemySpawnCompo);
		
		return crateEntity;
	}
	
	
}
