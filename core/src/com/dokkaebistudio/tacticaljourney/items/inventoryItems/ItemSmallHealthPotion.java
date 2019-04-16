/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.items.AbstractItem;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffPoison;
import com.dokkaebistudio.tacticaljourney.util.AnimatedImage;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * A consumable item that heals 25 HP.
 * @author Callil
 *
 */
public class ItemSmallHealthPotion extends AbstractItem {

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
		
		// Display anim
		final AnimatedImage animation = new AnimatedImage(AnimationSingleton.getInstance().healing, false);
		Action finishAction = new Action() {
			@Override
			public boolean act(float delta) {
				animation.remove();
				return true;
			}
		};
		animation.setFinishAction(finishAction);
		PoolableVector2 animPos = TileUtil.convertGridPosIntoPixelPos(Mappers.gridPositionComponent.get(user).coord());
		animation.setPosition(animPos.x, animPos.y);
		animPos.free();
		GameScreen.fxStage.addActor(animation);
		
		return true;
	}
	
	@Override
	public void onThrow(Vector2 thrownPosition, Entity thrower, Entity item, Room room) {
		super.onThrow(thrownPosition, thrower, item, room);
		Journal.addEntry("The small health potion broke and the potion is wasted");

		room.removeEntity(item);
	}
}
