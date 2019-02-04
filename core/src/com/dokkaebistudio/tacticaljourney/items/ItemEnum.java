package com.dokkaebistudio.tacticaljourney.items;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;


/**
 * The enum of all the items.
 * Each item must override the methods pickUp and drop.
 * @author Callil
 *
 */
public enum ItemEnum {
		
	/** A consumable item that heals 25 HP. */
	CONSUMABLE_HEALTH_UP("Small health potion", Assets.health_up_item, false) {
		
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
			
			//Display a DamageDisplayer
			GridPositionComponent gridPosCompo = Mappers.gridPositionComponent.get(user);
			room.entityFactory.createDamageDisplayer("25", gridPosCompo.coord(), true, room);
			return true;
		}
	},
	
	
	/** A tutorial page. */
	TUTORIAL_PAGE_1("Tutorial page 1", Assets.tutorial_page_item, false) {
		
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
	TUTORIAL_PAGE_2("Tutorial page 2", Assets.tutorial_page_item, false) {
		
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
	TUTORIAL_PAGE_3("Tutorial page 3", Assets.tutorial_page_item, false) {
		
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
	TUTORIAL_PAGE_4("Tutorial page 4", Assets.tutorial_page_item, false) {
		
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
	
	
	ItemEnum(String label, String imageName, boolean instaPickUp) {
		this.setLabel(label);
		this.setImageName(imageName);
		this.setInstantPickUp(instaPickUp);
	}
	
	
	// Abstract methods
	
	/** Called when the item is picked up. */
	public boolean pickUp(Entity picker, Entity item, Room room) {
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(picker);
		if (inventoryComponent != null) {
			if (!inventoryComponent.canStore()) return false;
			
			inventoryComponent.store(item, room);
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
	

}
