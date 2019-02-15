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
import com.dokkaebistudio.tacticaljourney.components.FlammableComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.enums.items.ItemEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;

/**
 * Factory used to create presets of entities.
 * @author Callil
 *
 */
public final class ItemFactory {
	
	/** The gdx pooled engine. */
	public PooledEngine engine;
	
	/** the entity factory. */
	public EntityFactory entityFactory;
	
	/**
	 * Constructor.
	 * @param e the engine
	 */
	public ItemFactory(PooledEngine e, EntityFactory ef) {
		this.engine = e;
		this.entityFactory = ef;
	}
	
	public Entity createItemBase(Room room, Vector2 tilePos, TextureAtlas.AtlasRegion texture, ItemEnum itemType) {
		Entity item = engine.createEntity();

		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite(new Sprite( texture));
		item.add(spriteCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		if (tilePos != null) {
			gridPosition.coord(item, tilePos, room);
			if (room != null) {
				room.getAddedItems().add(item);
			}
		}
		gridPosition.zIndex = ZIndexConstants.ITEM;
		item.add(gridPosition);
		
		ItemComponent itemCompo = engine.createComponent(ItemComponent.class);
		itemCompo.setItemType(itemType);
		item.add(itemCompo);
		
    	DestructibleComponent destructibleCompo = engine.createComponent(DestructibleComponent.class);
    	item.add(destructibleCompo);
		
		engine.addEntity(item);
		
		return item;
	}
	

	/**
	 * Create a money item that is consumed when picked up.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemMoney(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.money_item, ItemEnum.MONEY);
		item.flags = EntityFlagEnum.ITEM_MONEY.getFlag();
		return item;
	}
	
	/**
	 * Create a arrow item that is consumed when picked up.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemArrows(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.arrow_item, ItemEnum.ARROW);
		item.flags = EntityFlagEnum.ITEM_ARROWS.getFlag();
		return item;
	}
	
	/**
	 * Create a bomb item that is consumed when picked up.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemBombs(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.bomb_item, ItemEnum.BOMB);
		item.flags = EntityFlagEnum.ITEM_BOMBS.getFlag();
		return item;
	}
	
	/**
	 * Create a health potion.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemHealthUp(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.health_up_item, ItemEnum.CONSUMABLE_HEALTH_UP);
		item.flags = EntityFlagEnum.ITEM_HEALTH_UP.getFlag();
		return item;
	}
	
	/**
	 * Create a fire potion.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemFirePotion(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.fire_potion_item, ItemEnum.FIRE_POTION);
		item.flags = EntityFlagEnum.ITEM_FIRE_POTION.getFlag();
		return item;
	}
	
	/**
	 * Create a light armor.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemLightArmor(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.armor_up_item, ItemEnum.CONSUMABLE_ARMOR_UP);
		item.flags = EntityFlagEnum.ITEM_ARMOR_UP.getFlag();
		return item;
	}
	
	/**
	 * Create a piece of armor.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemArmorPiece(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.armor_piece_item, ItemEnum.CONSUMABLE_ARMOR_PIECE);
		item.flags = EntityFlagEnum.ITEM_ARMOR_PIECE.getFlag();
		return item;
	}
	
	/**
	 * Create a tutorial page.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemTutorialPage(int pageNumber, Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.tutorial_page_item, ItemEnum.valueOf("TUTORIAL_PAGE_" + pageNumber));
		item.flags = EntityFlagEnum.ITEM_TUTORIAL_PAGE.getFlag();
		
		FlammableComponent flammable = engine.createComponent(FlammableComponent.class);
		flammable.setPropagate(true);
		flammable.setDestroyed(true);
		flammable.setDestroyedTexture(Assets.tutorial_page_item);
		item.add(flammable);

		return item;
	}
	
}
