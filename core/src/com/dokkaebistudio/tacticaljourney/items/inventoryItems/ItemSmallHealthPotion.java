/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.items.Item;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffPoison;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * A consumable item that heals 25 HP.
 * @author Callil
 *
 */
public class ItemSmallHealthPotion extends Item {

	public ItemSmallHealthPotion() {
		super(ItemEnum.POTION_SMALL_HEALTH, Assets.health_up_item, false, true);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_SMALL_HEALTH_POTION_DESCRIPTION;		
	}
	
	@Override
	public String getActionLabel() {
		return "[GREEN]Drink";
	}
	
	@Override
	public boolean isStackable() {
		return true;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {
		Journal.addEntry("You drank the small health potion");

		//Heal the picker for 25 HP !
		HealthComponent healthComponent = Mappers.healthComponent.get(user);
		healthComponent.restoreHealth(25);
		
		StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(user);
		statusReceiverComponent.removeStatus(user, StatusDebuffPoison.class, room);
		return true;
	}
	
	@Override
	public void onThrow(Vector2 thrownPosition, Entity thrower, Entity item, Room room) {
		super.onThrow(thrownPosition, thrower, item, room);
		Journal.addEntry("The small health potion broke and the potion is wasted");

		room.removeEntity(item);
	}
}
