package com.dokkaebistudio.tacticaljourney.items;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
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
	CONSUMABLE_HEALTH_UP(true) {
		@Override
		public void pickUp(Entity picker, Entity item, Room room) {
			//Heal the picker for 25 HP !
			HealthComponent healthComponent = Mappers.healthComponent.get(picker);
			healthComponent.restoreHealth(25);
			
			//Display a DamageDisplayer
			GridPositionComponent gridPosCompo = Mappers.gridPositionComponent.get(item);
			room.entityFactory.createDamageDisplayer("25", gridPosCompo.coord, true);
		}

		@Override
		public void drop(Entity picker, Entity item, Room room) {}
	};
	
	/** Whether this item is picked up automatically while walking on its tile. */
	private boolean instantPickUp;
	
	
	ItemEnum(boolean instaPickUp) {
		this.setInstantPickUp(instaPickUp);
	}
	
	
	/** Called when the item is picked up. */
	public abstract void pickUp(Entity picker, Entity item, Room room);
	
	/** Called when the item is removed from an entity. */
	public abstract void drop(Entity picker, Entity item, Room room);

	
	
	// Getters and Setters

	public boolean isInstantPickUp() {
		return instantPickUp;
	}


	public void setInstantPickUp(boolean instantPickUp) {
		this.instantPickUp = instantPickUp;
	}
	

}
