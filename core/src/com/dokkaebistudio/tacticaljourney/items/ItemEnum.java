package com.dokkaebistudio.tacticaljourney.items;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AmmoCarrierComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.components.player.WalletComponent;
import com.dokkaebistudio.tacticaljourney.enums.AmmoTypeEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;


/**
 * The enum of all the items.
 * Each item must override the methods pickUp and drop.
 * @author Callil
 *
 */
public enum ItemEnum {
		
	/** Add money to the player's wallet. */
	MONEY(" # gold coin[s]", Assets.money_item, true, false, 1, 10) {

		@Override
		public String getDescription() {return null;}
		
		@Override
		public String getActionLabel() {return null;}
		
		@Override
		public boolean use(Entity user, Entity item, Room room) {
			ItemComponent itemComponent = Mappers.itemComponent.get(item);
			WalletComponent walletComponent = Mappers.walletComponent.get(user);
			walletComponent.receive(itemComponent.getRandomValue());
			return true;
		}
	},
	
	/** Add arrows to the player's quiver. */
	ARROW(" # arrow[s]", Assets.arrow_item, false, false, 1, 8) {

		@Override
		public String getDescription() {
			return "Arrows can be shot from a distance. If there are too much arrows for your quiver, the remaining "
				+ "arrows will stay on the ground.";
		}
		
		@Override
		public String getActionLabel() {return null;}
		
		@Override
		public boolean use(Entity user, Entity item, Room room) {
			ItemComponent itemComponent = Mappers.itemComponent.get(item);
			AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(user);
			int remainingArrows = ammoCarrierComponent.pickUpAmmo(AmmoTypeEnum.ARROWS, itemComponent.getRandomValue());
			
			if (remainingArrows > 0) {
				itemComponent.setRandomValue(remainingArrows);
				return false;
			}
			return true;
		}
	},
	
	/** Add bombs to the player's bag. */
	BOMB(" # bomb[s]", Assets.bomb_item, false, false, 1, 5) {

		@Override
		public String getDescription() {
			return "Bombs can be thrown on the ground and explode after some turns. Be sure to stay away from the blast. "
					+ "If there are too much bombs for your bag, the remaining bombs will stay on the ground.";
		}
		
		@Override
		public String getActionLabel() {return null;}
		
		@Override
		public boolean use(Entity user, Entity item, Room room) {
			ItemComponent itemComponent = Mappers.itemComponent.get(item);
			AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(user);
			int remainingBombs = ammoCarrierComponent.pickUpAmmo(AmmoTypeEnum.BOMBS, itemComponent.getRandomValue());
			
			if (remainingBombs > 0) {
				itemComponent.setRandomValue(remainingBombs);
				return false;
			}
			return true;		}
	},
	
	/** A consumable item that heals 25 HP. */
	CONSUMABLE_HEALTH_UP("Small health potion", Assets.health_up_item, false, true) {
		
		@Override
		public String getDescription() {
			return "Heal 25 HP upon use.\n"
					+ "No one knows how it's made and why is it red, but rumor has it that it tastes like cinnamon latte.";		
		}
		
		@Override
		public String getActionLabel() {
			return "Drink";
		}
		
		@Override
		public boolean use(Entity user, Entity item, Room room) {
			//Heal the picker for 25 HP !
			HealthComponent healthComponent = Mappers.healthComponent.get(user);
			healthComponent.restoreHealth(25);
			return true;
		}
	},
	
	
	/** A tutorial page. */
	TUTORIAL_PAGE_1("Tutorial page 1", Assets.tutorial_page_item, false, true) {
		
		@Override
		public String getDescription() {
			return "Welcome to Tactical Journey.\n"
					+ "You won't be assisted much in this game so go explore and try to understand it by yourself.";		
		}
		
		@Override
		public String getActionLabel() {
			return "Tear";
		}
		
		@Override
		public boolean use(Entity user, Entity item, Room room) {return true;}

	},
	
	/** A tutorial page. */
	TUTORIAL_PAGE_2("Tutorial page 2", Assets.tutorial_page_item, false, true) {
		
		@Override
		public String getDescription() {
			return "Page 2: Le verbe Ouamoulure (4eme groupe).\n"
					+ "Je ouamoului, tu ouamouluis, il ouamoului, nous ouamouluissons, vous ouamouluissez, ils ouamouluissent.";		
		}
		
		@Override
		public String getActionLabel() {
			return "Tear";
		}
		
		@Override
		public boolean use(Entity user, Entity item, Room room) {return true;}

	},
	
	/** A tutorial page. */
	TUTORIAL_PAGE_3("Tutorial page 3", Assets.tutorial_page_item, false, true) {
		
		@Override
		public String getDescription() {
			return "Page 3: Le chant des herons.\n"
					+ "Heron Heron Heron Heron Heron Heron Heron Heron Heron Heron Heron Heron Heron Heron.";		
		}
		
		@Override
		public String getActionLabel() {
			return "Tear";
		}
		
		@Override
		public boolean use(Entity user, Entity item, Room room) {return true;}

	},
	
	/** A tutorial page. */
	TUTORIAL_PAGE_4("Tutorial page 4", Assets.tutorial_page_item, false, true) {
		
		@Override
		public String getDescription() {
			return "Page 4: The best game.\n"
					+ "Marmotte de terre, Windows tournevista, Tortulipe, Salamandragore.";		
		}
		
		@Override
		public String getActionLabel() {
			return "Tear";
		}
		
		@Override
		public boolean use(Entity user, Entity item, Room room) {return true;}

	};
	
	
	
	
	
	/** The name displayed. */
	private String label;
	/** The name of the image in the assets. */
	private String imageName;
	/** Whether this item is picked up automatically while walking on its tile. */
	private boolean instantPickUp;
	/** Whether this item can go into the inventory. */
	private boolean goIntoInventory;
	
	private Integer randomValueMin;
	private Integer randomValueMax;	
	
	/**
	 * Constructor for basic items without random values
	 * @param label
	 * @param imageName
	 * @param instaPickUp
	 */
	ItemEnum(String label, String imageName, boolean instaPickUp, boolean goIntoInventory) {
		this.setLabel(label);
		this.setImageName(imageName);
		this.setInstantPickUp(instaPickUp);
		this.setGoIntoInventory(goIntoInventory);
	}
	
	/**
	 * Constructor for items with random values.
	 * @param label
	 * @param imageName
	 * @param instaPickUp
	 * @param valMin
	 * @param valMax
	 */
	ItemEnum(String label, String imageName, boolean instaPickUp, boolean goIntoInventory, Integer valMin, Integer valMax) {
		this(label, imageName, instaPickUp, goIntoInventory);
		this.setRandomValueMin(valMin);
		this.setRandomValueMax(valMax);
	}
	
	
	// Abstract methods
	
	/** Called when the item is picked up. */
	public boolean pickUp(Entity picker, Entity item, Room room) {
		ItemComponent itemComponent = Mappers.itemComponent.get(item);
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(picker);
		if (inventoryComponent != null) {
			if (!inventoryComponent.canStore(itemComponent)) return false;
			
			boolean stored = inventoryComponent.store(item, itemComponent, room);
			if (stored && !this.goIntoInventory) {
				room.removeEntity(item);
			}
		}

		return true;
	}
	
	/** Called when the item is used. */
	public abstract boolean use(Entity user, Entity item, Room room);
	
	/** Called when the item is removed from an entity. */
	public boolean drop(Entity picker, Entity item, Room room) {
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(picker);
		inventoryComponent.remove(item);
		
		GridPositionComponent playerPosCompo = Mappers.gridPositionComponent.get(picker);
		GridPositionComponent itemPosCompo = Mappers.gridPositionComponent.get(item);
		itemPosCompo.coord().set(playerPosCompo.coord());
		itemPosCompo.setActive(item, room);
		return true;
	}

	/** Return the description of the item. */
	public abstract String getDescription();
	
	/** Return the label on the action button for the item. */
	public abstract String getActionLabel();
	
	
	
	// Getters and Setters

	public boolean isInstantPickUp() {
		return instantPickUp;
	}


	public void setInstantPickUp(boolean instantPickUp) {
		this.instantPickUp = instantPickUp;
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public String getImageName() {
		return imageName;
	}


	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	
	public boolean isGoIntoInventory() {
		return goIntoInventory;
	}

	public void setGoIntoInventory(boolean goIntoInventory) {
		this.goIntoInventory = goIntoInventory;
	}

	public Integer getRandomValueMin() {
		return randomValueMin;
	}

	public void setRandomValueMin(Integer randomValueMin) {
		this.randomValueMin = randomValueMin;
	}

	public Integer getRandomValueMax() {
		return randomValueMax;
	}

	public void setRandomValueMax(Integer randomValueMax) {
		this.randomValueMax = randomValueMax;
	}
	

}
