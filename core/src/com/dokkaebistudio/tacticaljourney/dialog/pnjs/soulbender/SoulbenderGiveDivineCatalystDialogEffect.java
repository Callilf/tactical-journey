package com.dokkaebistudio.tacticaljourney.dialog.pnjs.soulbender;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent.PlayerActionEnum;
import com.dokkaebistudio.tacticaljourney.dialog.DialogEffect;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class SoulbenderGiveDivineCatalystDialogEffect implements DialogEffect {

	@Override
	public void play(Entity speaker, Room room) {
		PlayerComponent playerComponent = Mappers.playerComponent.get(GameScreen.player);
		playerComponent.requestAction(PlayerActionEnum.GIVE_CATALYST_SOULBENDER, speaker);
	}

}
