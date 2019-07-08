package com.dokkaebistudio.tacticaljourney.dialog.pnjs.shopkeeper;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent.PlayerActionEnum;
import com.dokkaebistudio.tacticaljourney.dialog.DialogEffect;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class ShopkeeperRestockDialogEffect implements DialogEffect {

	@Override
	public void play(Entity speaker, Room room) {
		PlayerComponent playerComponent = Mappers.playerComponent.get(GameScreen.player);
		playerComponent.requestAction(PlayerActionEnum.RESTOCK_SHOP, speaker);
	}

}
