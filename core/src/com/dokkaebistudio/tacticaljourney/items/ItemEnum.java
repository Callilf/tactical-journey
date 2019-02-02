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
					+ "No one knows how it's made and why is the colour so close to the blood colour, but rumor has it that is tastes like cinnamon latte.";		
		}
		
		@Override
		public boolean pickUp(Entity picker, Entity item, Room room) {
			InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(picker);
			if (inventoryComponent != null) {
				if (!inventoryComponent.canStore()) return false;
				
				inventoryComponent.store(item, room);
			}
			return true;
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

		@Override
		public boolean drop(Entity user, Entity item, Room room) {
			InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(user);
			inventoryComponent.remove(item);
			
			GridPositionComponent playerPosCompo = Mappers.gridPositionComponent.get(user);
			GridPositionComponent itemPosCompo = Mappers.gridPositionComponent.get(item);
			itemPosCompo.coord().set(playerPosCompo.coord());
			itemPosCompo.setActive(item, room);
			return true;
		}
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
	public abstract boolean pickUp(Entity picker, Entity item, Room room);
	
	/** Called when the item is used. */
	public abstract boolean use(Entity user, Entity item, Room room);
	
	/** Called when the item is removed from an entity. */
	public abstract boolean drop(Entity picker, Entity item, Room room);

	/** Return the description of the item. */
	public abstract String getDescription();
	
	
	
	
	
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
