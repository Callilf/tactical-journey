/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.items;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.rendering.HUDRenderer;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.vfx.VFXUtil;

/**
 * Karma +1
 * @author Callil
 *
 */
public class ItemFourLeafClover extends AbstractItem {

	public ItemFourLeafClover() {
		super(ItemEnum.CLOVER, Assets.clover_item, false, false);
	}

	@Override
	public String getDescription() {
		return Descriptions.ITEM_CLOVER_DESCRIPTION;
	}
	
	@Override
	public String getActionLabel() {return null;}
	
	@Override
	public boolean use(Entity user, Entity item, Room room) {
		PlayerComponent playerComponent = Mappers.playerComponent.get(user);
		if (playerComponent != null) {
			playerComponent.increaseKarma(1);
			VFXUtil.createStatsUpNotif("Karma +1", Mappers.gridPositionComponent.get(GameScreen.player).coord());
			Journal.addEntry(Journal.getLabel(user) + " picked up a Four-leaf clover and gain +1 karma");
		}
		return true;
	}
	
	@Override
	public Vector2 getPickupImageMoveDestination() {
		return HUDRenderer.POS_PROFILE;
	}

}
