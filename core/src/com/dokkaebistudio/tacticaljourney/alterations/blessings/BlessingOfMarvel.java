/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.ces.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomType;
import com.dokkaebistudio.tacticaljourney.statuses.buffs.StatusBuffBinary;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class BlessingOfMarvel extends Blessing {

	@Override
	public String title() {
		return "Blessing of marvel";
	}
	
	@Override
	public String description() {
		return "On boss room and mini-boss room entrance, receive the [YELLOW]binary[] status effect for 10 turns.";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_marvel;
	}

	@Override
	public void onRoomVisited(Entity entity, Room room) {
		if (room.type == RoomType.BOSS_ROOM || room.type == RoomType.MINI_BOSS_ROOM) {
			StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(entity);
			statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusBuffBinary(10));
			
			Journal.addEntry("[YELLOW]Blessing of marvel makes " + Journal.getLabel(entity) + " go binary.");
			AlterationSystem.addAlterationProc(this);
			
		}
	}

}
