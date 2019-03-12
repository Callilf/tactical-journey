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
import com.dokkaebistudio.tacticaljourney.items.ItemArrow;
import com.dokkaebistudio.tacticaljourney.items.ItemBomb;
import com.dokkaebistudio.tacticaljourney.items.ItemKey;
import com.dokkaebistudio.tacticaljourney.items.ItemMoney;
import com.dokkaebistudio.tacticaljourney.items.alterationItems.ItemFrailtyCurse;
import com.dokkaebistudio.tacticaljourney.items.alterationItems.ItemVigorBlessing;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemFataMorgana;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemMithridatium;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemNurseEyePatch;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemRamSkull;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemTotemOfKalamazoo;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemVegetalGarment;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.boss.ItemPangolinScale;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemArmorPiece;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemFirePotion;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemLightArmor;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemOrbContainer;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemPebble;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemRegenPotion;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemSmallHealthPotion;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemTutorialPage;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemVenomGland;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemWebSack;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemWingPotion;
import com.dokkaebistudio.tacticaljourney.items.orbs.ItemOrbEnergy;
import com.dokkaebistudio.tacticaljourney.items.orbs.ItemOrbPoison;
import com.dokkaebistudio.tacticaljourney.items.orbs.ItemOrbVegetal;
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
	 * @return the item created
	 */
	public Entity createItem(ItemEnum type) {
		return createItem(type, null, null);
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
		case MONEY:
			item = createItemMoney(room, tilePos);
			break;
			
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
		case POTION_REGEN:
			item = createItemRegenPotion(room, tilePos);
			break;
		case POTION_WING:
			item = createItemWingPotion(room, tilePos);
			break;
			
		case ORB_CONTAINER:
			item = createItemOrbContainer(room, tilePos);
			break;

			
		case WEB_SACK:
			item = createItemWebSack(room, tilePos);
			break;
		case VENOM_GLAND:
			item = createItemVenomGland(room, tilePos);
			break;
		case PEBBLE:
			item = createItemPebble(room, tilePos);
			break;
		case TOTEM_OF_KALAMAZOO:
			item = createItemTotemOfKalamazoo(room, tilePos);
			break;
			
		case FATA_MORGANA:
			item = createItemFataMorgana(room, tilePos);
			break;
			
		case MITHRIDATIUM:
			item = createItemMithridatium(room, tilePos);
			break;

		case NURSE_EYE_PATCH:
			item = createItemNurseEyePatch(room, tilePos);
			break;

		case VEGETAL_GARMENT:
			item = createItemVegetalGarment(room, tilePos);
			break;
			
		case RAM_SKULL:
			item = createItemRamSkull(room, tilePos);
			break;
			
		case PANGOLIN_SCALE:
			item = createItemPangolinScale(room, tilePos);
			break;
			
			
			
			
			
			
			
		case ENERGY_ORB:
			item = createItemEnergyOrb(room, tilePos);
			break;
		case VEGETAL_ORB:
			item = createItemVegetalOrb(room, tilePos);
			break;
		case POISON_ORB:
			item = createItemPoisonOrb(room, tilePos);
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
	 * Create the key to next floor.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemKey(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.key, new ItemKey());
		item.flags = EntityFlagEnum.ITEM_MONEY.getFlag();
		
		item.remove(DestructibleComponent.class);
		
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
	 * Create a regen potion.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemRegenPotion(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.regen_potion_item, new ItemRegenPotion());
		item.flags = EntityFlagEnum.ITEM_REGEN_POTION.getFlag();
		return item;
	}
	
	/**
	 * Create a wing potion.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemWingPotion(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.wing_potion_item, new ItemWingPotion());
		item.flags = EntityFlagEnum.ITEM_WING_POTION.getFlag();
		return item;
	}
	
	public Entity createItemOrbContainer(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.orb_container_item, new ItemOrbContainer());
		item.flags = EntityFlagEnum.ITEM_ORB_CONTAINER.getFlag();
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
	
	/**
	 * Create a sack of web.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemVenomGland(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.venom_gland_item, new ItemVenomGland());
		item.flags = EntityFlagEnum.ITEM_VENOM_GLAND.getFlag();
		return item;
	}
	
	public Entity createItemPebble(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.pebble_item, new ItemPebble());
		item.flags = EntityFlagEnum.ITEM_PEBBLE.getFlag();
		return item;
	}
	
	

	//*********************
	// Infusables 
	
	public Entity createItemTotemOfKalamazoo(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.totem_of_kalamazoo, new ItemTotemOfKalamazoo());
		item.flags = EntityFlagEnum.ITEM_TOTEM_OF_KALAMAZOO.getFlag();
		
		FlammableComponent flammable = engine.createComponent(FlammableComponent.class);
		flammable.setPropagate(true);
		flammable.setDestroyed(false);
		item.add(flammable);
		
		return item;
	}
	

	public Entity createItemFataMorgana(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.fata_morgana, new ItemFataMorgana());
		item.flags = EntityFlagEnum.ITEM_FATA_MORGANA.getFlag();
		return item;
	}
	

	public Entity createItemMithridatium(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.mithridatium, new ItemMithridatium());
		item.flags = EntityFlagEnum.ITEM_MITHRIDATIUM.getFlag();
		return item;
	}
	
	

	public Entity createItemNurseEyePatch(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.nurse_eye_patch, new ItemNurseEyePatch());
		item.flags = EntityFlagEnum.ITEM_NURSE_EYE_PATCH.getFlag();
		return item;
	}
	

	public Entity createItemVegetalGarment(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.vegetal_garment, new ItemVegetalGarment());
		item.flags = EntityFlagEnum.ITEM_VEGETAL_GARMENT.getFlag();
		return item;
	}
	
	public Entity createItemRamSkull(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.ram_skull, new ItemRamSkull());
		item.flags = EntityFlagEnum.ITEM_RAM_SKULL.getFlag();
		return item;
	}

	public Entity createItemPangolinScale(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.pangolin_scale, new ItemPangolinScale());
		item.flags = EntityFlagEnum.ITEM_PANGOLIN_SCALE.getFlag();
		return item;
	}
	
	
	
	
	
	
	//************
	// Orbs
	
	public Entity createItemEnergyOrb(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.energy_orb_item, new ItemOrbEnergy());
		item.flags = EntityFlagEnum.ITEM_ORB.getFlag();
		return item;
	}
	
	public Entity createItemVegetalOrb(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.vegetal_orb_item, new ItemOrbVegetal());
		item.flags = EntityFlagEnum.ITEM_ORB.getFlag();
		return item;
	}
	
	public Entity createItemPoisonOrb(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.poison_orb_item, new ItemOrbPoison());
		item.flags = EntityFlagEnum.ITEM_ORB.getFlag();
		return item;
	}
	
	// TODO change
	
	public Entity createItemVigor(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.blessing_vigor, new ItemVigorBlessing());
		item.flags = EntityFlagEnum.ITEM_WEB_SACK.getFlag();
		
		return item;
	}
	public Entity createItemFrailty(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.curse_frailty, new ItemFrailtyCurse());
		item.flags = EntityFlagEnum.ITEM_WEB_SACK.getFlag();
		
		return item;
	}
	
}
