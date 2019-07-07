package com.dokkaebistudio.tacticaljourney.util;

import java.util.List;
import java.util.Map.Entry;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemySpawnerComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate.ItemPoolRarity;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootableComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.StatueComponent;
import com.dokkaebistudio.tacticaljourney.creature.enemies.enums.EnemyTypeEnum;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.factory.EntityFlagEnum;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.items.pools.ItemPool;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.vfx.VFXUtil;

public class LootUtil {

	
	/**
	 * Destroy the given destructible entity.
	 * @param d the destructible entity
	 */
	public static void destroy(Entity d, Room room) {
		DestructibleComponent destructibleComponent = Mappers.destructibleComponent.get(d);
		if (destructibleComponent == null) return;
		
		destructibleComponent.setDestroyed(true);

		if (destructibleComponent.isRemove()) {
			GridPositionComponent tilePos = Mappers.gridPositionComponent.get(d);
			
			boolean spawnedEnemy = spawnEnemy(d, tilePos, room);

			if (!spawnedEnemy) {
				// Drop loot
				LootRewardComponent lootRewardComponent = Mappers.lootRewardComponent.get(d);
				dropItem(d, lootRewardComponent, room);
			}
			
			VFXUtil.createDisappearanceEffect(tilePos.coord(), Mappers.spriteComponent.get(d).getSprite());
			room.removeEntity(d);

			
			//Add debris
			if (destructibleComponent != null && destructibleComponent.getDestroyedTexture() != null) {
				room.entityFactory.createSpriteOnTile(tilePos.coord(), 2,destructibleComponent.getDestroyedTexture(), EntityFlagEnum.DESTROYED_SPRITE, room);
			}
		} else {
			SpriteComponent spriteComponent = Mappers.spriteComponent.get(d);
			spriteComponent.setSprite(destructibleComponent.getDestroyedTexture());
		}
		
		
		// If it's a statue, set its "destroyed" status so that the statues delivers a curse
		StatueComponent statueComponent = Mappers.statueComponent.get(d);
		if (statueComponent != null) {
			statueComponent.setJustDestroyed(true);
		}
	}



	/**
	 * Spawn an enemy from the given entity if possible.
	 * @param spawnerEntity the entity that could spawn an enemy
	 * @param tilePos the position of this entity
	 * @param room the room
	 * @return true if the entity has spawned an enemy
	 */
	private static boolean spawnEnemy(Entity spawnerEntity, GridPositionComponent tilePos, Room room) {
		boolean spawnedEnemy = false;
		EnemySpawnerComponent enemySpawnerComponent = Mappers.enemySpawnerComponent.get(spawnerEntity);
		if (enemySpawnerComponent != null) {
			int randomInt = RandomSingleton.getInstance().nextSeededInt(100);
			if (randomInt < enemySpawnerComponent.getTotalSpawnChance()) {
				EnemyTypeEnum enemyType = enemySpawnerComponent.getActionForRandomInt(randomInt);
				if (enemyType != null) {
					room.entityFactory.enemyFactory.createEnemy(enemyType, room, tilePos.coord());
					spawnedEnemy = true;
				}
			}
		}
		return spawnedEnemy;
	}
	
	
	
    /**
     * Drop an item after explosion.
     * @param entity the entity that exploded and will drop the item
     * @param lootRewardComponent the lootRewardComponent of the entity
     */
	public static void dropItem(final Entity entity, final LootRewardComponent lootRewardComponent, final Room room) {
		if (lootRewardComponent == null) return;
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(entity);

		final Entity dropItem = generateLoot(entity, lootRewardComponent, room.entityFactory);
		if (dropItem != null) {
			final ItemComponent itemComponent = Mappers.itemComponent.get(dropItem);
			final PoolableVector2 dropLocation = PoolableVector2.create(gridPositionComponent.coord());
			
			room.pauseState();
			
			// Drop animation
			Action finishDropAction = new Action(){
			  @Override
			  public boolean act(float delta){
				itemComponent.drop(dropLocation, dropItem, room);
				dropLocation.free();
				room.unpauseState();
			    return true;
			  }
			};
			Image dropAnimationImage = itemComponent.getDropAnimationImage(entity, dropItem, finishDropAction, 0f);
			GameScreen.fxStage.addActor(dropAnimationImage);
			
			lootRewardComponent.setLatestItem(dropItem);
		}
		
		if (lootRewardComponent.getItemToDrop() != null) {
			// Drop animation
			final Entity mandatoryLoot = room.entityFactory.itemFactory.createItem(lootRewardComponent.getItemToDrop());
			final ItemComponent mandatoryItemComponent = Mappers.itemComponent.get(mandatoryLoot);
			final PoolableVector2 dropLoc = PoolableVector2.create(gridPositionComponent.coord());
			room.pauseState();

			Action finishMandatoryDropAction = new Action(){
			  @Override
			  public boolean act(float delta){
				mandatoryItemComponent.drop(dropLoc, mandatoryLoot, room);
				dropLoc.free();
				room.unpauseState();
			    return true;
			  }
			};
			Image mandatoryDropAnimationImage = mandatoryItemComponent.getDropAnimationImage(entity,
					mandatoryLoot, finishMandatoryDropAction, dropItem != null ? 0.3f : 0f);
			
			GameScreen.fxStage.addActor(mandatoryDropAnimationImage);
		}
	}
	
	/**
	 * Generate enemy and destructible's loot.
	 * @param entity the entity that drops the loot
	 * @param lootRewardComponent the loot reward compo of this entity
	 * @param entityFactory the entity factory
	 * @return the item to loot, if any
	 */
	public static Entity generateLoot(Entity entity, LootRewardComponent lootRewardComponent, EntityFactory entityFactory) {
		RandomXS128 random = lootRewardComponent.getDropSeededRandom();
		DropRate dropRate = lootRewardComponent.getDropRate();
		ItemPool itemPool = lootRewardComponent.getItemPool();
		if (random == null || dropRate == null || itemPool == null) return null;
		
		float randomValue = RandomSingleton.getNextChanceWithKarma(random);
		ItemPoolRarity rarity = getRarity(randomValue, dropRate);
		
		if (rarity != null) {
			List<PooledItemDescriptor> itemTypes = itemPool.getItemTypes(1, rarity, random);
			if (!itemTypes.isEmpty()) {
				PooledItemDescriptor itemType = itemTypes.get(0);
				return entityFactory.itemFactory.createItem(itemType.getType(), null, null, random);
			}
		}
		
		return null;
	}



	public static ItemPoolRarity getRarity(float randomValue, DropRate dropRate) {
		if (dropRate == null) return null;
		
		float chance = 0;
		ItemPoolRarity rarity = null;
		for (Entry<ItemPoolRarity, Float> entry : dropRate.getRatePerRarity().entrySet()) {
			if (randomValue >= chance && randomValue < chance + entry.getValue().floatValue()) {
				rarity = entry.getKey();
				break;
			}
			chance += entry.getValue().intValue();
		}
		return rarity;
	}
	
	
	/**
	 * Fill the content of a lootable.
	 * @param lootableComponent the lootable component
	 * @param entityFactory the entity factory
	 */
	public static void fillLootable(LootableComponent lootableComponent, EntityFactory entityFactory) {
		if (lootableComponent == null || entityFactory == null) return;
		
		for(int i=0 ; i<lootableComponent.getMaxNumberOfItems() ; i++) {
			RandomXS128 seededRandom = lootableComponent.getSeededRandom();
			if (seededRandom == null) continue;
			
			float randomValue = RandomSingleton.getNextChanceWithKarma(seededRandom);
			ItemPoolRarity rarity = LootUtil.getRarity(randomValue, lootableComponent.getDropRate());
			if (rarity == null) continue;
			
			RandomXS128 clonedRandom = RandomSingleton.cloneRandom(seededRandom);
			List<PooledItemDescriptor> itemTypes = lootableComponent.getItemPool().getItemTypes(1, rarity, clonedRandom);
			for (PooledItemDescriptor pid : itemTypes) {
				Entity item = entityFactory.itemFactory.createItem(pid.getType(), null, null, clonedRandom);
				lootableComponent.getItems().add(item);
			}
		}
	}
	
	
	public static List<Entity> findLootables(Room room, List<Entity> listToPopulate) {
		for (Entity e : room.getAllEntities()) {
			if (Mappers.lootableComponent.has(e)) {
				listToPopulate.add(e);
			}
		}
		return listToPopulate;
	}
	
}
