/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.orbs;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemVegetalGarment;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffEntangled;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;

/**
 * @author Callil
 *
 */
public class OrbVegetal extends Orb {

	public OrbVegetal() {
		super(Descriptions.ORB_VEGETAL_TITLE);
	}

	@Override
	public boolean effectOnContact(Entity user, Entity orb, Entity target, Room room) {

		// Entangle
		StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(target);
		if (statusReceiverComponent != null) {
			Journal.addEntry("[FOREST]Vegetal orb has been activated");
			statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffEntangled(5));
		}
		
		boolean immune = false;
		InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(target);
		immune = inventoryComponent != null && inventoryComponent.contains(ItemVegetalGarment.class);
		
		if (!immune) {
			// Stop movement
			MoveComponent moveComponent = Mappers.moveComponent.get(target);
			moveComponent.setMoveRemaining(0);
			if (moveComponent.moving) {
				moveComponent.setSelectedTile(orb);
				MovementHandler.finishRealMovement(target, room);
			}
		}
		
		return true;
	}
	
	public int getHeuristic(Entity mover) {
		return 1;
	}
}
