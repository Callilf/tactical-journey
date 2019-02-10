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
	MONEY(" # gold coin[s]", Assets.money_item, true, false, 1, 5) {

		@Override
		public String getDescription() {return null;}
		
		@Override
		public String getActionLabel() {return null;}
		
		@Override
		public boolean use(Entity user, Entity item, Room room) {
			ItemComponent itemComponent = Mappers.itemComponent.get(item);
			itemComponent.setQuantityPickedUp(itemComponent.getQuantity());
			WalletComponent walletComponent = Mappers.walletComponent.get(user);
			walletComponent.receive(itemComponent.getQuantity());
			return true;
		}
	},
	
	/** Add arrows to the player's quiver. */
	ARROW(" # arrow[s]", Assets.arrow_item, false, false, 1, 5) {

		@Override
		public String getDescription() {
			return "Arrows can be shot from a distance using your bow skill (down left of the screen). If there are too much arrows for your quiver, the remaining "
				+ "arrows will stay on the ground.";
		}
		
		@Override
		public String getActionLabel() {return null;}
		
		@Override
		public boolean use(Entity user, Entity item, Room room) {
			ItemComponent itemComponent = Mappers.itemComponent.get(item);
			AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(user);
			int remainingArrows = ammoCarrierComponent.pickUpAmmo(AmmoTypeEnum.ARROWS, itemComponent.getQuantity());
			
			itemComponent.setQuantityPickedUp(itemComponent.getQuantity() - remainingArrows);
			itemComponent.setQuantity(remainingArrows);

			return remainingArrows == 0;
		}
	},
	
	/** Add bombs to the player's bag. */
	BOMB(" # bomb[s]", Assets.bomb_item, false, false, 1, 2) {

		@Override
		public String getDescription() {
			return "Bombs can be thrown on the ground by using your bomb skill and explode after some turns. Be sure to stay away from the blast. "
					+ "If there are too much bombs for your bag, the remaining bombs will stay on the ground.";
		}
		
		@Override
		public String getActionLabel() {return null;}
		
		@Override
		public boolean use(Entity user, Entity item, Room room) {
			ItemComponent itemComponent = Mappers.itemComponent.get(item);
			AmmoCarrierComponent ammoCarrierComponent = Mappers.ammoCarrierComponent.get(user);
			int remainingBombs = ammoCarrierComponent.pickUpAmmo(AmmoTypeEnum.BOMBS, itemComponent.getQuantity());
			
			itemComponent.setQuantityPickedUp(itemComponent.getQuantity() - remainingBombs);
			itemComponent.setQuantity(remainingBombs);

			return remainingBombs == 0;
		}
	},
	
	/** A consumable item that heals 25 HP. */
	CONSUMABLE_HEALTH_UP("Small health potion", Assets.health_up_item, false, true) {
		
		@Override
		public String getDescription() {
			return "Heal 25 HP upon use.\n"
					+ "Remember that drinking this potion will take a turn, so don't stay too close from the enemy while doing it.";		
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
					+ "Your goal is to reach the end of the last floor. As of now, there is only one floor, so reaching the end of this floor will be enough.";		
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
			return "Skills: \n"
					+ "On the bottom right of the screen you have 3 skills:\n"
					+ " - The melee skill allowing you to attack anything close\n"
					+ " - The range skill, allowing you to use your bow given you have arrows\n"
					+ " - The bomb skill that allows you throwing bombs that explode after 2 turns.";		
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
			return "Turns:\n"
					+ "Remember that mostly everything in this game except movement takes a turn. Using, droping or picking up an item will"
					+ " end your turn, so stay away from enemies when managing your inventory. Note that picking up money does not end your turn.";		
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
			return "The wheel:\n"
					+ "When attacking an enemy, the attack wheel pops up. The damage you deal depends on the color you hit.\n"
					+ " - [GREEN]Green[WHITE]: normal hit, the amount you deal is equal to your strength\n"
					+ " - [GRAY]Gray[WHITE]: graze, the amount you deal is a bit lower than your strength\n"
					+ " - [BLACK]Black[WHITE]: miss, you don't deal any damage\n"
					+ " - [RED]Red[WHITE]: critical, the amount you deal is 2 times your strength.";		
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
			return stored;
		}

		return false;
	}
	
	/** Called when the item is used. */
	public abstract boolean use(Entity user, Entity item, Room room);
	
	/** Called when the item is removed from an entity. */
	public boolean drop(Entity dropper, Entity item, Room room) {
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(dropper);
		if (inventoryComponent != null) {
			inventoryComponent.remove(item);
		}
		
		GridPositionComponent playerPosCompo = Mappers.gridPositionComponent.get(dropper);
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
