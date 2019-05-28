/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.ashley.PublicEntity;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.FlammableComponent;
import com.dokkaebistudio.tacticaljourney.components.GravityComponent;
import com.dokkaebistudio.tacticaljourney.components.InspectableComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.items.ItemArrow;
import com.dokkaebistudio.tacticaljourney.items.ItemBomb;
import com.dokkaebistudio.tacticaljourney.items.ItemFourLeafClover;
import com.dokkaebistudio.tacticaljourney.items.ItemKey;
import com.dokkaebistudio.tacticaljourney.items.ItemMoney;
import com.dokkaebistudio.tacticaljourney.items.ItemMoney.MoneyAmountEnum;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemCamoBackpack;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemColorfulTie;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemDurian;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemFataMorgana;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemHandProsthesis;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemHeadband;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemLeftJikatabi;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemMementoMori;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemMerchantMask;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemMithridatium;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemNurseEyePatch;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemOldCrown;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemPowderFlask;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemRamSkull;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemRightJikatabi;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemScissorhand;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemShinobiHeadband;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemSilkyBeard;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemTotemOfKalamazoo;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemVegetalGarment;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemVillanelle;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.boss.ItemPangolinScale;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemArmorPiece;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemBanana;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemDivineCatalyst;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemFirePotion;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemLeather;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemLightArmor;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemOrbContainer;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemPebble;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemPurityPotion;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemRegenPotion;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemScrollOfDestruction;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemScrollOfDoppelganger;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemScrollOfTeleportation;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemShuriken;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemSmallHealthPotion;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemSmokebomb;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemTutorialPage;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemUniversalCure;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemVenomGland;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemWebSack;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemWingPotion;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemWormholeShard;
import com.dokkaebistudio.tacticaljourney.items.orbs.ItemOrbDeath;
import com.dokkaebistudio.tacticaljourney.items.orbs.ItemOrbEnergy;
import com.dokkaebistudio.tacticaljourney.items.orbs.ItemOrbFire;
import com.dokkaebistudio.tacticaljourney.items.orbs.ItemOrbPoison;
import com.dokkaebistudio.tacticaljourney.items.orbs.ItemOrbVegetal;
import com.dokkaebistudio.tacticaljourney.items.orbs.ItemOrbVoid;
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
	
	
	public Entity createItemBase(Room room, Vector2 tilePos, AbstractItem itemType, EntityFlagEnum flag) {
		Entity item = createItemBase(room, tilePos, itemType.getTexture(), 
				itemType, itemType.getLabel(), itemType.getDescription());
		item.flags = flag.getFlag();
		return item;
	}
	
	public Entity createItemBase(Room room, Vector2 tilePos, RegionDescriptor texture, AbstractItem itemType, String title, String desc) {
		PublicEntity item = (PublicEntity) engine.createEntity();
		
		InspectableComponent inspect = engine.createComponent(InspectableComponent.class);
		inspect.setTitle(title);
		inspect.setDescription(desc);
		item.add(inspect);


		SpriteComponent spriteCompo = engine.createComponent(SpriteComponent.class);
		spriteCompo.setSprite( texture);
		item.add(spriteCompo);

		GridPositionComponent gridPosition = engine.createComponent(GridPositionComponent.class);
		if (tilePos != null) {
			gridPosition.coord(item, tilePos, room);
		}
		gridPosition.zIndex = ZIndexConstants.ITEM;
		item.add(gridPosition);
		
		ItemComponent itemCompo = engine.createComponent(ItemComponent.class);
		itemCompo.setItemType(itemType, item);
		item.add(itemCompo);
		
    	DestructibleComponent destructibleCompo = engine.createComponent(DestructibleComponent.class);
    	item.add(destructibleCompo);
    	
		GravityComponent gravityCompo = engine.createComponent(GravityComponent.class);
		item.add(gravityCompo);
		
    	if (room != null) {
    		room.addEntity(item);
    	} else {
    		engine.addEntity(item);
    	}
    	
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
	public Entity createItem(ItemEnum type, RandomXS128 randomToUse) {
		return createItem(type, null, null, randomToUse);
	}
	
	
	/**
	 * Create an item of the given type at the given position in the given room.
	 * @param type the type of item
	 * @param room the room
	 * @param tilePos the position
	 * @return the item created
	 */
	public Entity createItem(ItemEnum type, Room room, Vector2 tilePos) {
		return createItem(type, room, tilePos, null);
	}
	
	/**
	 * Create an item of the given type at the given position in the given room.
	 * @param type the type of item
	 * @param room the room
	 * @param tilePos the position
	 * @return the item created
	 */
	public Entity createItem(ItemEnum type, Room room, Vector2 tilePos, RandomXS128 randomToUse) {
		Entity item = null;
		
		switch (type) {
		
		
		//*****************
		// Insta use items
		
		case MONEY:
			item = createItemMoney(room, tilePos, MoneyAmountEnum.SMALL, randomToUse);
			break;
		case MONEY_MEDIUM:
			item = createItemMoney(room, tilePos, MoneyAmountEnum.MEDIUM, randomToUse);
			break;
		case MONEY_BIG:
			item = createItemMoney(room, tilePos, MoneyAmountEnum.LARGE, randomToUse);
			break;
		case AMMO_ARROW:
			item = createItemArrows(room, tilePos, randomToUse);
			break;
		case AMMO_BOMB:
			item = createItemBombs(room, tilePos, randomToUse);
			break;
		case CLOVER:
			item = createItemBase(room, tilePos,  new ItemFourLeafClover(), EntityFlagEnum.ITEM_CLOVER);
			break;
			
		//***********************
		// Inventory items
			
		case ARMOR_LIGHT:
			item = createItemBase(room, tilePos,  new ItemLightArmor(), EntityFlagEnum.ITEM_ARMOR_UP);
			break;
		case ARMOR_PIECE:
			item = createItemBase(room, tilePos,  new ItemArmorPiece(), EntityFlagEnum.ITEM_ARMOR_PIECE);
			break;
		case POTION_FIRE:
			item = createItemBase(room, tilePos,  new ItemFirePotion(), EntityFlagEnum.ITEM_FIRE_POTION);
			break;
		case POTION_SMALL_HEALTH:
			item = createItemBase(room, tilePos,  new ItemSmallHealthPotion(), EntityFlagEnum.ITEM_HEALTH_UP);
			break;
		case POTION_REGEN:
			item = createItemBase(room, tilePos,  new ItemRegenPotion(), EntityFlagEnum.ITEM_REGEN_POTION);
			break;
		case POTION_WING:
			item = createItemBase(room, tilePos,  new ItemWingPotion(), EntityFlagEnum.ITEM_WING_POTION);
			break;
		case POTION_PURITY:
			item = createItemBase(room, tilePos,  new ItemPurityPotion(), EntityFlagEnum.ITEM_PURITY_POTION);
			break;
		case ORB_CONTAINER:
			item = createItemBase(room, tilePos,  new ItemOrbContainer(), EntityFlagEnum.ITEM_ORB_CONTAINER);
			break;
		case DIVINE_CATALYST:
			item = createItemBase(room, tilePos,  new ItemDivineCatalyst(), EntityFlagEnum.ITEM_DIVINE_CATALYST);
			break;
		case LEATHER:
			item = createItemBase(room, tilePos,  new ItemLeather(), EntityFlagEnum.ITEM_LEATHER);
			break;
		case UNIVERSAL_CURE:
			item = createItemBase(room, tilePos, new ItemUniversalCure(), EntityFlagEnum.ITEM_UNIVERSAL_CURE);
			break;
			
			
		//*************
		// Throwing items
			
		case WEB_SACK:
			item = createItemBase(room, tilePos,  new ItemWebSack(), EntityFlagEnum.ITEM_WEB_SACK);
			break;
		case VENOM_GLAND:
			item = createItemBase(room, tilePos,  new ItemVenomGland(), EntityFlagEnum.ITEM_VENOM_GLAND);
			break;
		case PEBBLE:
			item = createItemBase(room, tilePos,  new ItemPebble(), EntityFlagEnum.ITEM_PEBBLE);
			break;
		case WORMHOLE_SHARD:
			item = createItemBase(room, tilePos,  new ItemWormholeShard(), EntityFlagEnum.ITEM_WORMHOLE_SHARD);
			break;
		case SHURIKEN:
			item = createItemBase(room, tilePos,  new ItemShuriken(), EntityFlagEnum.ITEM_SHURIKEN);
			break;
		case SMOKE_BOMB:
			item = createItemBase(room, tilePos,  new ItemSmokebomb(), EntityFlagEnum.ITEM_SHURIKEN);
			break;
		case BANANA:
			item = createItemBase(room, tilePos,  new ItemBanana(), EntityFlagEnum.ITEM_BANANA);
			break;
			
		//***************
		// Scrolls
			
		case SCROLL_DOPPELGANGER:
			item = createItemBase(room, tilePos,  new ItemScrollOfDoppelganger(), EntityFlagEnum.ITEM_SCROLL);
			FlammableComponent flammable = engine.createComponent(FlammableComponent.class);
			flammable.setPropagate(true);
			flammable.setDestroy(true);
			item.add(flammable);
			break;
		case SCROLL_TELEPORTATION:
			item = createItemBase(room, tilePos,  new ItemScrollOfTeleportation(), EntityFlagEnum.ITEM_SCROLL);
			flammable = engine.createComponent(FlammableComponent.class);
			flammable.setPropagate(true);
			flammable.setDestroy(true);
			item.add(flammable);
			break;
		case SCROLL_DESTRUCTION:
			item = createItemBase(room, tilePos,  new ItemScrollOfDestruction(), EntityFlagEnum.ITEM_SCROLL);
			flammable = engine.createComponent(FlammableComponent.class);
			flammable.setPropagate(true);
			flammable.setDestroy(true);
			item.add(flammable);
			break;
			
		//******************
		// Infusables
			
		case TOTEM_OF_KALAMAZOO:
			item = createItemBase(room, tilePos,  new ItemTotemOfKalamazoo(), EntityFlagEnum.ITEM_INFUSABLE);
			
			flammable = engine.createComponent(FlammableComponent.class);
			flammable.setPropagate(true);
			flammable.setDestroy(false);
			item.add(flammable);
			
			break;
			
		case FATA_MORGANA:
			item = createItemBase(room, tilePos,  new ItemFataMorgana(), EntityFlagEnum.ITEM_INFUSABLE);
			break;
			
		case MITHRIDATIUM:
			item = createItemBase(room, tilePos,  new ItemMithridatium(), EntityFlagEnum.ITEM_INFUSABLE);
			break;

		case NURSE_EYE_PATCH:
			item = createItemBase(room, tilePos,  new ItemNurseEyePatch(), EntityFlagEnum.ITEM_INFUSABLE);
			break;

		case VEGETAL_GARMENT:
			item = createItemBase(room, tilePos,  new ItemVegetalGarment(), EntityFlagEnum.ITEM_INFUSABLE);
			
			flammable = engine.createComponent(FlammableComponent.class);
			flammable.setPropagate(true);
			flammable.setDestroy(true);
			item.add(flammable);
			
			break;
			
		case RAM_SKULL:
			item = createItemBase(room, tilePos,  new ItemRamSkull(), EntityFlagEnum.ITEM_INFUSABLE);
			break;
			
		case COLORFUL_TIE:
			item = createItemBase(room, tilePos,  new ItemColorfulTie(), EntityFlagEnum.ITEM_INFUSABLE);
			break;
			
		case OLD_CROWN:
			item = createItemBase(room, tilePos,  new ItemOldCrown(), EntityFlagEnum.ITEM_INFUSABLE);
			break;
			
		case MEMENTO_MORI:
			item = createItemBase(room, tilePos,  new ItemMementoMori(), EntityFlagEnum.ITEM_INFUSABLE);
			break;
			
		case HEADBAND:
			item = createItemBase(room, tilePos,  new ItemHeadband(), EntityFlagEnum.ITEM_INFUSABLE);
			break;
			
		case VILLANELLE:
			item = createItemBase(room, tilePos,  new ItemVillanelle(), EntityFlagEnum.ITEM_INFUSABLE);
			break;
			
		case POWDER_FLASK:
			item = createItemBase(room, tilePos,  new ItemPowderFlask(), EntityFlagEnum.ITEM_INFUSABLE);
			break;
			
		case CAMO_BACKPACK:
			item = createItemBase(room, tilePos,  new ItemCamoBackpack(), EntityFlagEnum.ITEM_INFUSABLE);
			break;
			
		case MERCHANT_MASK:
			item = createItemBase(room, tilePos,  new ItemMerchantMask(), EntityFlagEnum.ITEM_INFUSABLE);
			break;
			
		case HAND_PROSTHESIS:
			item = createItemBase(room, tilePos,  new ItemHandProsthesis(), EntityFlagEnum.ITEM_INFUSABLE);
			break;
			
		case LEFT_JIKATABI:
			item = createItemBase(room, tilePos,  new ItemLeftJikatabi(), EntityFlagEnum.ITEM_INFUSABLE);
			break;
			
		case RIGHT_JIKATABI:
			item = createItemBase(room, tilePos,  new ItemRightJikatabi(), EntityFlagEnum.ITEM_INFUSABLE);
			break;
			
		case SHINOBI_HEADBAND:
			item = createItemBase(room, tilePos,  new ItemShinobiHeadband(), EntityFlagEnum.ITEM_INFUSABLE);
			break;
			
		case SILKY_BEARD:
			item = createItemBase(room, tilePos,  new ItemSilkyBeard(), EntityFlagEnum.ITEM_INFUSABLE);
			break;
			
		case PANGOLIN_SCALE:
			item = createItemBase(room, tilePos,  new ItemPangolinScale(), EntityFlagEnum.ITEM_INFUSABLE);
			break;
			
		case SCISSORHAND:
			item = createItemBase(room, tilePos,  new ItemScissorhand(), EntityFlagEnum.ITEM_INFUSABLE);
			break;
			
		case DURIAN:
			item = createItemBase(room, tilePos,  new ItemDurian(), EntityFlagEnum.ITEM_INFUSABLE);
			break;
			
			
			
			
		//*****************
		// Orbs
			
		case ENERGY_ORB:
			item = createItemBase(room, tilePos,  new ItemOrbEnergy(), EntityFlagEnum.ITEM_ORB);
			break;
		case VEGETAL_ORB:
			item = createItemBase(room, tilePos,  new ItemOrbVegetal(), EntityFlagEnum.ITEM_ORB);
			break;
		case POISON_ORB:
			item = createItemBase(room, tilePos,  new ItemOrbPoison(), EntityFlagEnum.ITEM_ORB);
			break;
		case FIRE_ORB:
			item = createItemBase(room, tilePos,  new ItemOrbFire(), EntityFlagEnum.ITEM_ORB);
			break;
		case DEATH_ORB:
			item = createItemBase(room, tilePos,  new ItemOrbDeath(), EntityFlagEnum.ITEM_ORB);
			break;
		case VOID_ORB:
			item = createItemBase(room, tilePos,  new ItemOrbVoid(), EntityFlagEnum.ITEM_ORB);
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
		Entity item = createItemBase(room, tilePos, Assets.money_item, new ItemMoney(),
				Descriptions.ITEM_MONEY_TITLE, Descriptions.ITEM_MONEY_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_MONEY.getFlag();
		return item;
	}
	public Entity createItemMoney(Room room, Vector2 tilePos, RandomXS128 randomToUse) {
		Entity item = createItemBase(room, tilePos, Assets.money_item, new ItemMoney(randomToUse),
				Descriptions.ITEM_MONEY_TITLE, Descriptions.ITEM_MONEY_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_MONEY.getFlag();
		return item;
	}
	public Entity createItemMoney(Room room, Vector2 tilePos, MoneyAmountEnum amount, RandomXS128 randomToUse) {
		Entity item = createItemBase(room, tilePos, Assets.money_item, new ItemMoney(amount,randomToUse),
				Descriptions.ITEM_MONEY_TITLE, Descriptions.ITEM_MONEY_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_MONEY.getFlag();
		return item;
	}
	
	/**
	 * Create a arrow item that is consumed when picked up.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemArrows(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.arrow_item, new ItemArrow(),
				Descriptions.ITEM_ARROWS_TITLE, Descriptions.ITEM_ARROWS_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_ARROWS.getFlag();
		return item;
	}
	public Entity createItemArrows(Room room, Vector2 tilePos, RandomXS128 randomToUse) {
		Entity item = createItemBase(room, tilePos, Assets.arrow_item, new ItemArrow(randomToUse),
				Descriptions.ITEM_ARROWS_TITLE, Descriptions.ITEM_ARROWS_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_ARROWS.getFlag();
		return item;
	}
	
	/**
	 * Create a bomb item that is consumed when picked up.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemBombs(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.bomb_item, new ItemBomb(),
				Descriptions.ITEM_BOMBS_TITLE, Descriptions.ITEM_BOMBS_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_BOMBS.getFlag();
		return item;
	}
	public Entity createItemBombs(Room room, Vector2 tilePos, RandomXS128 randomToUse) {
		Entity item = createItemBase(room, tilePos, Assets.bomb_item, new ItemBomb(randomToUse),
				Descriptions.ITEM_BOMBS_TITLE, Descriptions.ITEM_BOMBS_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_BOMBS.getFlag();
		return item;
	}

	
	/**
	 * Create a tutorial page.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemTutorialPage(int pageNumber, Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.tutorial_page_item, new ItemTutorialPage(pageNumber),
				Descriptions.ITEM_TUTORIAL_PAGE_TITLE, Descriptions.ITEM_TUTORIAL_PAGE_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_TUTORIAL_PAGE.getFlag();
		
		FlammableComponent flammable = engine.createComponent(FlammableComponent.class);
		flammable.setPropagate(true);
		flammable.setDestroy(true);
		flammable.setDestroyedTexture(Assets.tutorial_page_item);
		item.add(flammable);

		return item;
	}
	
	/**
	 * Create the key to next floor.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemKey(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, new ItemKey(), EntityFlagEnum.ITEM_MONEY);		
		item.remove(DestructibleComponent.class);
		
		return item;
	}
	
}
