/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.ces.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.PlayerComponent.ProfilePopinDisplayModeEnum;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * A consumable item that grants flight.
 * @author Callil
 *
 */
public class ItemPurityPotion extends AbstractItem {

	public ItemPurityPotion() {
		super(ItemEnum.POTION_PURITY, Assets.purity_potion_item, false, true);
		setRecyclePrice(40);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_PURITY_POTION_DESCRIPTION;		
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

		AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(user);
		if (alterationReceiverComponent == null || alterationReceiverComponent.getCurses().isEmpty()) {
			Journal.addEntry("You drank the purity potion, but since you had no curses, it did nothing else than making you feel good");
			return true;
		}
		
		PlayerComponent playerComponent = Mappers.playerComponent.get(user);
		if (playerComponent != null) {
			playerComponent.setProfilePopinDisplayed(ProfilePopinDisplayModeEnum.LIFT_CURSE);
			Journal.addEntry("You drank the purity potion.");
		}
		
		return true;
	}
	
	
	@Override
	public void onThrow(Vector2 thrownPosition, Entity thrower, Entity item, Room room) {
		super.onThrow(thrownPosition, thrower, item, room);
		Journal.addEntry("The purity potion broke and the potion is wasted");

		room.removeEntity(item);
	}
}
