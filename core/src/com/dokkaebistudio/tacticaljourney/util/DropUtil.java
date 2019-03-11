package com.dokkaebistudio.tacticaljourney.util;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.StatueComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.factory.EntityFlagEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

public class DropUtil {

	
	/**
	 * Destroy the given destructible entity.
	 * @param d the destructible entity
	 */
	public static void destroy(Entity d, Room room) {
		DestructibleComponent destructibleComponent = Mappers.destructibleComponent.get(d);
		destructibleComponent.setDestroyed(true);

		if (destructibleComponent.isRemove()) {
			
			// Drop loot
			LootRewardComponent lootRewardComponent = Mappers.lootRewardComponent.get(d);
			if (lootRewardComponent != null && lootRewardComponent.getDrop() != null) {
				// Drop reward
				dropItem(d, lootRewardComponent, room);
			}
			
			room.removeEntity(d);

			
			//Add debris
			if (destructibleComponent != null && destructibleComponent.getDestroyedTexture() != null) {
				GridPositionComponent tilePos = Mappers.gridPositionComponent.get(d);
				room.entityFactory.createSpriteOnTile(tilePos.coord(), 2,destructibleComponent.getDestroyedTexture(), EntityFlagEnum.DESTROYED_SPRITE, room);
			}
		} else {
			SpriteComponent spriteComponent = Mappers.spriteComponent.get(d);
			spriteComponent.setSprite(new Sprite(destructibleComponent.getDestroyedTexture()));
			
			// If it's a statue, set its "destroyed" status so that the statues delivers a curse
			StatueComponent statueComponent = Mappers.statueComponent.get(d);
			if (statueComponent != null) {
				statueComponent.setJustDestroyed(true);
			}
		}
	}
	
	
	
    /**
     * Drop an item after explosion.
     * @param entity the entity that exploded and will drop the item
     * @param lootRewardComponent the lootRewardComponent of the entity
     */
	public static void dropItem(final Entity entity, final LootRewardComponent lootRewardComponent, final Room room) {
		final Entity dropItem = lootRewardComponent.getDrop();
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
	}
	
}
