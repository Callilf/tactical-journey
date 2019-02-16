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
import com.dokkaebistudio.tacticaljourney.items.Item;
import com.dokkaebistudio.tacticaljourney.items.ItemArmorPiece;
import com.dokkaebistudio.tacticaljourney.items.ItemArrow;
import com.dokkaebistudio.tacticaljourney.items.ItemBomb;
import com.dokkaebistudio.tacticaljourney.items.ItemFirePotion;
import com.dokkaebistudio.tacticaljourney.items.ItemLightArmor;
import com.dokkaebistudio.tacticaljourney.items.ItemMoney;
import com.dokkaebistudio.tacticaljourney.items.ItemSmallHealthPotion;
import com.dokkaebistudio.tacticaljourney.items.ItemTutorialPage;
import com.dokkaebistudio.tacticaljourney.items.ItemWebSack;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
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
	
	public Entity createItemBase(Room room, Vector2 tilePos, TextureAtlas.AtlasRegion texture, Item itemType) {
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
	 * Create an item of the given type at the given position in the given room.
	 * @param type the type of item
	 * @param room the room
	 * @param tilePos the position
	 * @return the item created
	 */
	public Entity createItem(ItemEnum type, Room room, Vector2 tilePos) {
		Entity item = null;
		
		switch (type) {
		case AMMO_ARROW:
			item = createItemArrows(room, tilePos);
			break;
		case AMMO_BOMB:
			item = createItemBombs(room, tilePos);
			break;
			
		case ARMOR_LIGHT:
			item = createItemLightArmor(room, tilePos);
			break;
		case ARMOR_PIECE:
			item = createItemArmorPiece(room, tilePos);
			break;
			
		case POTION_FIRE:
			item = createItemFirePotion(room, tilePos);
			break;
		case POTION_SMALL_HEALTH:
			item = createItemHealthUp(room, tilePos);
			break;
			
			
		case WEB_SACK:
			item = createItemWebSack(room, tilePos);
			break;

			default:
				System.out.println("Item type " + type.name() + " not handled in ItemFactory.");
				
		}
		
		return item;
	}

	/**
	 * Create a money item that is consumed when picked up.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemMoney(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.money_item, new ItemMoney());
		item.flags = EntityFlagEnum.ITEM_MONEY.getFlag();
		return item;
	}
	
	/**
	 * Create a arrow item that is consumed when picked up.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemArrows(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.arrow_item, new ItemArrow());
		item.flags = EntityFlagEnum.ITEM_ARROWS.getFlag();
		return item;
	}
	
	/**
	 * Create a bomb item that is consumed when picked up.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemBombs(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.bomb_item, new ItemBomb());
		item.flags = EntityFlagEnum.ITEM_BOMBS.getFlag();
		return item;
	}
	
	/**
	 * Create a health potion.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemHealthUp(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.health_up_item, new ItemSmallHealthPotion());
		item.flags = EntityFlagEnum.ITEM_HEALTH_UP.getFlag();
		return item;
	}
	
	/**
	 * Create a fire potion.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemFirePotion(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.fire_potion_item, new ItemFirePotion());
		item.flags = EntityFlagEnum.ITEM_FIRE_POTION.getFlag();
		return item;
	}
	
	/**
	 * Create a light armor.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemLightArmor(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.armor_up_item, new ItemLightArmor());
		item.flags = EntityFlagEnum.ITEM_ARMOR_UP.getFlag();
		return item;
	}
	
	/**
	 * Create a piece of armor.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemArmorPiece(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.armor_piece_item, new ItemArmorPiece());
		item.flags = EntityFlagEnum.ITEM_ARMOR_PIECE.getFlag();
		return item;
	}
	
	/**
	 * Create a tutorial page.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemTutorialPage(int pageNumber, Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.tutorial_page_item, new ItemTutorialPage(pageNumber));
		item.flags = EntityFlagEnum.ITEM_TUTORIAL_PAGE.getFlag();
		
		FlammableComponent flammable = engine.createComponent(FlammableComponent.class);
		flammable.setPropagate(true);
		flammable.setDestroyed(true);
		flammable.setDestroyedTexture(Assets.tutorial_page_item);
		item.add(flammable);

		return item;
	}
	
	/**
	 * Create a sack of web.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemWebSack(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.web_sack_item, new ItemWebSack());
		item.flags = EntityFlagEnum.ITEM_WEB_SACK.getFlag();
		
		FlammableComponent flammable = engine.createComponent(FlammableComponent.class);
		flammable.setPropagate(true);
		flammable.setDestroyed(true);
		flammable.setDestroyedTexture(Assets.web_sack_item);
		item.add(flammable);
		
		return item;
	}
	
	
}
