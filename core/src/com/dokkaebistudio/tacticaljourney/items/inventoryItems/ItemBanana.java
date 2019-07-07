/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items.inventoryItems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.BlessingOfTheOrangutan;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
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
public class ItemBanana extends AbstractItem {

	public ItemBanana() {
		super(ItemEnum.BANANA, Assets.banana_item, false, true);
		setRecyclePrice(3);
	}
	
	@Override
	public String getDescription() {
		return Descriptions.ITEM_BANANA_DESCRIPTION;		
	}
	
	@Override
	public String getActionLabel() {
		return "[GREEN]Eat";
	}
	
	@Override
	public boolean isStackable() {
		return true;
	}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {
		Journal.addEntry("You ate the banana");

		//Heal for 5 hp!
		int healAmount = 5;
		AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(user);
		if (alterationReceiverComponent != null && alterationReceiverComponent.hasBlessing(BlessingOfTheOrangutan.class)) {
			healAmount = 10;
		}
		
		HealthComponent healthComponent = Mappers.healthComponent.get(user);
		healthComponent.restoreHealth(healAmount);
		
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

}
