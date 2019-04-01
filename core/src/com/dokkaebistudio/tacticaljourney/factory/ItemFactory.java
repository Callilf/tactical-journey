/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.factory;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.FlammableComponent;
import com.dokkaebistudio.tacticaljourney.components.InspectableComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.constants.ZIndexConstants;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.items.ItemArrow;
import com.dokkaebistudio.tacticaljourney.items.ItemBomb;
import com.dokkaebistudio.tacticaljourney.items.ItemKey;
import com.dokkaebistudio.tacticaljourney.items.ItemMoney;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemColorfulTie;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemFataMorgana;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemHeadband;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemMementoMori;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemMithridatium;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemNurseEyePatch;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemOldCrown;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemRamSkull;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemTotemOfKalamazoo;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemVegetalGarment;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemVillanelle;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.boss.ItemPangolinScale;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemArmorPiece;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemFirePotion;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemLightArmor;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemOrbContainer;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemPebble;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemRegenPotion;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemSmallHealthPotion;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemTutorialPage;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemUniversalCure;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemVenomGland;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemWebSack;
import com.dokkaebistudio.tacticaljourney.items.inventoryItems.ItemWingPotion;
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
	
	public Entity createItemBase(Room room, Vector2 tilePos, RegionDescriptor texture, AbstractItem itemType, String title, String desc) {
		Entity item = engine.createEntity();
		
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
	public Entity createItem(ItemEnum type, boolean noRandom) {
		return createItem(type, null, null, noRandom);
	}
	
	
	/**
	 * Create an item of the given type at the given position in the given room.
	 * @param type the type of item
	 * @param room the room
	 * @param tilePos the position
	 * @return the item created
	 */
	public Entity createItem(ItemEnum type, Room room, Vector2 tilePos) {
		return createItem(type, room, tilePos, false);
	}
	
	/**
	 * Create an item of the given type at the given position in the given room.
	 * @param type the type of item
	 * @param room the room
	 * @param tilePos the position
	 * @return the item created
	 */
	public Entity createItem(ItemEnum type, Room room, Vector2 tilePos, boolean noRandom) {
		Entity item = null;
		
		switch (type) {
		case MONEY:
			item = createItemMoney(room, tilePos, noRandom);
			break;
			
		case AMMO_ARROW:
			item = createItemArrows(room, tilePos, noRandom);
			break;
		case AMMO_BOMB:
			item = createItemBombs(room, tilePos, noRandom);
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
			
		case COLORFUL_TIE:
			item = createItemColorfulTie(room, tilePos);
			break;
			
		case OLD_CROWN:
			item = createItemOldCrown(room, tilePos);
			break;
			
		case MEMENTO_MORI:
			item = createItemMementoMori(room, tilePos);
			break;
			
		case HEADBAND:
			item = createItemHeadband(room, tilePos);
			break;
			
		case VILLANELLE:
			item = createItemVillanelle(room, tilePos);
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
		case FIRE_ORB:
			item = createItemFireOrb(room, tilePos);
			break;
		case DEATH_ORB:
			item = createItemDeathOrb(room, tilePos);
			break;
		case VOID_ORB:
			item = createItemVoidOrb(room, tilePos);
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
	public Entity createItemMoney(Room room, Vector2 tilePos, boolean noRandom) {
		Entity item = createItemBase(room, tilePos, Assets.money_item, new ItemMoney(noRandom),
				Descriptions.ITEM_MONEY_TITLE, Descriptions.ITEM_MONEY_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_MONEY.getFlag();
		return item;
	}
	
	/**
	 * Create the key to next floor.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemKey(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.key, new ItemKey(),
				Descriptions.ITEM_KEY_TITLE, Descriptions.ITEM_KEY_DESCRIPTION);
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
		Entity item = createItemBase(room, tilePos, Assets.arrow_item, new ItemArrow(),
				Descriptions.ITEM_ARROWS_TITLE, Descriptions.ITEM_ARROWS_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_ARROWS.getFlag();
		return item;
	}
	public Entity createItemArrows(Room room, Vector2 tilePos, boolean noRandom) {
		Entity item = createItemBase(room, tilePos, Assets.arrow_item, new ItemArrow(noRandom),
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
	public Entity createItemBombs(Room room, Vector2 tilePos, boolean noRandom) {
		Entity item = createItemBase(room, tilePos, Assets.bomb_item, new ItemBomb(noRandom),
				Descriptions.ITEM_BOMBS_TITLE, Descriptions.ITEM_BOMBS_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_BOMBS.getFlag();
		return item;
	}
	
	
	public Entity createUniversalCure(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.universal_cure, new ItemUniversalCure(),
				Descriptions.ITEM_UNIVERSAL_CURE_TITLE, Descriptions.ITEM_UNIVERSAL_CURE_DESCRIPTION);
		item.flags = EntityFlagEnum.UNIVERSAL_CURE.getFlag();
		return item;
	}
	
	/**
	 * Create a health potion.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemHealthUp(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.health_up_item, new ItemSmallHealthPotion(),
				Descriptions.ITEM_SMALL_HEALTH_POTION_TITLE, Descriptions.ITEM_SMALL_HEALTH_POTION_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_HEALTH_UP.getFlag();
		return item;
	}
	
	/**
	 * Create a regen potion.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemRegenPotion(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.regen_potion_item, new ItemRegenPotion(),
				Descriptions.ITEM_REGEN_POTION_TITLE, Descriptions.ITEM_REGEN_POTION_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_REGEN_POTION.getFlag();
		return item;
	}
	
	/**
	 * Create a wing potion.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemWingPotion(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.wing_potion_item, new ItemWingPotion(),
				Descriptions.ITEM_WING_POTION_TITLE, Descriptions.ITEM_WING_POTION_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_WING_POTION.getFlag();
		return item;
	}
	
	public Entity createItemOrbContainer(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.orb_container_item, new ItemOrbContainer(),
				Descriptions.ITEM_ORB_CONTAINER_TITLE, Descriptions.ITEM_ORB_CONTAINER_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_ORB_CONTAINER.getFlag();
		return item;
	}
	
	/**
	 * Create a fire potion.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemFirePotion(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.fire_potion_item, new ItemFirePotion(),
				Descriptions.ITEM_FIRE_POTION_TITLE, Descriptions.ITEM_FIRE_POTION_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_FIRE_POTION.getFlag();
		return item;
	}
	
	/**
	 * Create a light armor.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemLightArmor(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.armor_up_item, new ItemLightArmor(),
				Descriptions.ITEM_MONEY_TITLE, Descriptions.ITEM_MONEY_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_ARMOR_UP.getFlag();
		return item;
	}
	
	/**
	 * Create a piece of armor.
	 * @param tilePos the position in tiles
	 * @return the entity created
	 */
	public Entity createItemArmorPiece(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.armor_piece_item, new ItemArmorPiece(),
				Descriptions.ITEM_MONEY_TITLE, Descriptions.ITEM_MONEY_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_ARMOR_PIECE.getFlag();
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
		Entity item = createItemBase(room, tilePos, Assets.web_sack_item, new ItemWebSack(),
				Descriptions.ITEM_WEB_SACK_TITLE, Descriptions.ITEM_WEB_SACK_DESCRIPTION);
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
		Entity item = createItemBase(room, tilePos, Assets.venom_gland_item, new ItemVenomGland(),
				Descriptions.ITEM_VENOM_GLAND_TITLE, Descriptions.ITEM_VENOM_GLAND_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_VENOM_GLAND.getFlag();
		return item;
	}
	
	public Entity createItemPebble(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.pebble_item, new ItemPebble(),
				Descriptions.ITEM_PEBBLE_TITLE, Descriptions.ITEM_PEBBLE_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_PEBBLE.getFlag();
		return item;
	}
	
	

	//*********************
	// Infusables 
	
	public Entity createItemTotemOfKalamazoo(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.totem_of_kalamazoo, new ItemTotemOfKalamazoo(),
				Descriptions.ITEM_TOTEM_OF_KALAMAZOO_TITLE, Descriptions.ITEM_TOTEM_OF_KALAMAZOO_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_TOTEM_OF_KALAMAZOO.getFlag();
		
		FlammableComponent flammable = engine.createComponent(FlammableComponent.class);
		flammable.setPropagate(true);
		flammable.setDestroyed(false);
		item.add(flammable);
		
		return item;
	}
	

	public Entity createItemFataMorgana(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.fata_morgana, new ItemFataMorgana(),
				Descriptions.ITEM_FATA_MORGANA_TITLE, Descriptions.ITEM_FATA_MORGANA_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_FATA_MORGANA.getFlag();
		return item;
	}
	

	public Entity createItemMithridatium(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.mithridatium, new ItemMithridatium(),
				Descriptions.ITEM_MITHRIDATIUM_TITLE, Descriptions.ITEM_MITHRIDATIUM_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_MITHRIDATIUM.getFlag();
		return item;
	}
	
	

	public Entity createItemNurseEyePatch(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.nurse_eye_patch, new ItemNurseEyePatch(),
				Descriptions.ITEM_NURSE_EYE_PATCH_TITLE, Descriptions.ITEM_NURSE_EYE_PATCH_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_NURSE_EYE_PATCH.getFlag();
		return item;
	}
	

	public Entity createItemVegetalGarment(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.vegetal_garment, new ItemVegetalGarment(),
				Descriptions.ITEM_VEGETAL_GARMENT_TITLE, Descriptions.ITEM_VEGETAL_GARMENT_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_VEGETAL_GARMENT.getFlag();
		return item;
	}
	
	public Entity createItemRamSkull(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.ram_skull, new ItemRamSkull(),
				Descriptions.ITEM_RAM_SKULL_TITLE, Descriptions.ITEM_RAM_SKULL_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_RAM_SKULL.getFlag();
		return item;
	}
	
	public Entity createItemColorfulTie(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.colorful_tie, new ItemColorfulTie(),
				Descriptions.ITEM_COLORFUL_TIE_TITLE, Descriptions.ITEM_COLORFUL_TIE_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_COLORFUL_TIE.getFlag();
		return item;
	}
	
	public Entity createItemOldCrown(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.old_crown, new ItemOldCrown(),
				Descriptions.ITEM_OLD_CROWN_TITLE, Descriptions.ITEM_OLD_CROWN_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_OLD_CROWN.getFlag();
		return item;
	}
	
	public Entity createItemMementoMori(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.memento_mori, new ItemMementoMori(),
				Descriptions.ITEM_MEMENTO_MORI_TITLE, Descriptions.ITEM_MEMENTO_MORI_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_OLD_CROWN.getFlag();
		return item;
	}
	
	public Entity createItemHeadband(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.headband, new ItemHeadband(),
				Descriptions.ITEM_HEADBAND_TITLE, Descriptions.ITEM_HEADBAND_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_HEADBAND.getFlag();
		return item;
	}
	
	public Entity createItemVillanelle(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.villanelle, new ItemVillanelle(),
				Descriptions.ITEM_VILLANELLE_TITLE, Descriptions.ITEM_VILLANELLE_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_HEADBAND.getFlag();
		return item;
	}
	
	public Entity createItemPangolinScale(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.pangolin_scale, new ItemPangolinScale(),
				Descriptions.ITEM_PANGOLIN_SCALE_TITLE, Descriptions.ITEM_PANGOLIN_SCALE_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_PANGOLIN_SCALE.getFlag();
		return item;
	}
	
	
	
	
	
	
	//************
	// Orbs
	
	public Entity createItemEnergyOrb(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.energy_orb_item, new ItemOrbEnergy(),
				Descriptions.ORB_ENERGY_TITLE, Descriptions.ORB_ENERGY_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_ORB.getFlag();
		return item;
	}
	
	public Entity createItemVegetalOrb(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.vegetal_orb_item, new ItemOrbVegetal(),
				Descriptions.ORB_VEGETAL_TITLE, Descriptions.ORB_VEGETAL_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_ORB.getFlag();
		return item;
	}
	
	public Entity createItemPoisonOrb(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.poison_orb_item, new ItemOrbPoison(),
				Descriptions.ORB_POISON_TITLE, Descriptions.ORB_POISON_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_ORB.getFlag();
		return item;
	}
	
	public Entity createItemFireOrb(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.fire_orb_item, new ItemOrbFire(),
				Descriptions.ORB_FIRE_TITLE, Descriptions.ORB_FIRE_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_ORB.getFlag();
		return item;
	}
	
	
	public Entity createItemDeathOrb(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.death_orb_item, new ItemOrbDeath(),
				Descriptions.ORB_DEATH_TITLE, Descriptions.ORB_DEATH_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_ORB.getFlag();
		return item;
	}
	
	public Entity createItemVoidOrb(Room room, Vector2 tilePos) {
		Entity item = createItemBase(room, tilePos, Assets.void_orb_item, new ItemOrbVoid(),
				Descriptions.ORB_VOID_TITLE, Descriptions.ORB_VOID_DESCRIPTION);
		item.flags = EntityFlagEnum.ITEM_ORB.getFlag();
		return item;
	}
}
