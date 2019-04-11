package com.dokkaebistudio.tacticaljourney.util;

import java.util.List;
import java.util.Map.Entry;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate;
import com.dokkaebistudio.tacticaljourney.components.loot.DropRate.ItemPoolRarity;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.neutrals.StatueComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.factory.EntityFlagEnum;
import com.dokkaebistudio.tacticaljourney.items.pools.PooledItemDescriptor;
import com.dokkaebistudio.tacticaljourney.items.pools.enemies.EnemyItemPool;
import com.dokkaebistudio.tacticaljourney.room.Room;

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
			
			// Drop loot
			LootRewardComponent lootRewardComponent = Mappers.lootRewardComponent.get(d);
			dropItem(d, lootRewardComponent, room);
			
			room.removeEntity(d);

			
			//Add debris
			if (destructibleComponent != null && destructibleComponent.getDestroyedTexture() != null) {
				GridPositionComponent tilePos = Mappers.gridPositionComponent.get(d);
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
     * Drop an item after explosion.
     * @param entity the entity that exploded and will drop the item
     * @param lootRewardComponent the lootRewardComponent of the entity
     */
	public static void dropItem(final Entity entity, final LootRewardComponent lootRewardComponent, final Room room) {
		if (lootRewardComponent == null) return;
		
		final Entity dropItem = generateLoot(entity, lootRewardComponent, room.entityFactory);
		if (dropItem == null) return;
		
		final ItemComponent itemComponent = Mappers.itemComponent.get(dropItem);
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(entity);
		final PoolableVector2 dropLocation = PoolableVector2.create(gridPositionComponent.coord());
		
		// Drop animation
		Action finishDropAction = new Action(){
		  @Override
		  public boolean act(float delta){
			itemComponent.drop(dropLocation, dropItem, room);
			dropLocation.free();
			
			room.getAddedItems().add(dropItem);
		    return true;
		  }
		};
		Image dropAnimationImage = itemComponent.getDropAnimationImage(entity, dropItem, finishDropAction);
		room.floor.getGameScreen().fxStage.addActor(dropAnimationImage);
		
		lootRewardComponent.setLatestItem(dropItem);
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
		EnemyItemPool itemPool = lootRewardComponent.getItemPool();
		if (random == null || dropRate == null || itemPool == null) return null;
		
		float unit = (float) random.nextInt(100);
		float decimal = random.nextFloat();
		float randomValue = unit + decimal;
		
		// Apply player's karma
		PlayerComponent playerComponent = Mappers.playerComponent.get(GameScreen.player);
		randomValue -= playerComponent.getKarma();
		if (randomValue < 0) randomValue = 0;
		
		int chance = 0;
		ItemPoolRarity rarity = null;
		for (Entry<ItemPoolRarity, Integer> entry : dropRate.getRatePerRarity().entrySet()) {
			if (randomValue >= chance && randomValue < chance + entry.getValue().intValue()) {
				rarity = entry.getKey();
				break;
			}
			chance += entry.getValue().intValue();
		}
		
		if (rarity != null) {
			List<PooledItemDescriptor> itemTypes = itemPool.getItemTypes(1, rarity, random);
			PooledItemDescriptor itemType = itemTypes.get(0);
			
			return entityFactory.itemFactory.createItem(itemType.getType(), null, null, random);
		}
		
		return null;
	}
	
}
