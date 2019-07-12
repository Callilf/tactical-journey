/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.curses;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;
import com.dokkaebistudio.tacticaljourney.ces.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffExhausted;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class CurseMarvel extends Curse {

	@Override
	public String title() {
		return "Curse of marvel";
	}
	
	@Override
	public String description() {
		return "On [YELLOW]binary[] status effect removal, receive the exhausted status effect.";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.curse_marvel;
	}

	@Override
	public void onRemoveStatusEffect(Entity entity, Status status, Room room) {
		Mappers.statusReceiverComponent.get(entity).addStatus(entity, new StatusDebuffExhausted(5), room, GameScreen.fxStage);
		
		Journal.addEntry("[PURPLE]Curse of marvel inflicted the exhausted status effect to " + Journal.getLabel(entity));
		AlterationSystem.addAlterationProc(this);
	}

}
