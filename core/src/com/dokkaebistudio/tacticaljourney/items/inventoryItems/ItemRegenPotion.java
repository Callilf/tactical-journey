/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.items.Item;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.buffs.StatusBuffRegen;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

/**
 * A consumable item that heals 25 HP.
 * @author Callil
 *
 */
public class ItemRegenPotion extends Item {

	public ItemRegenPotion() {
		super("Regeneration potion", Assets.regen_potion_item, false, true);
	}
	
	@Override
	public String getDescription() {
		return "Upon drink, grants a regeneration that lasts 20 to 40 turns.\n"
				+ "This weird concoction will slowly close your wounds but it won't be strong enough to cure any afflictions.";		
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
		Journal.addEntry("You drank the regeneration potion.");

		RandomXS128 unseededRandom = RandomSingleton.getInstance().getUnseededRandom();
		int duration = 20 + unseededRandom.nextInt(21);
		StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(user);
		statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusBuffRegen(duration));
		
		return true;
	}
	
	
	@Override
	public void onThrow(Vector2 thrownPosition, Entity thrower, Entity item, Room room) {
		super.onThrow(thrownPosition, thrower, item, room);
		Journal.addEntry("The regeneration potion broke and the potion is wasted");

		room.removeEntity(item);
	}
}
