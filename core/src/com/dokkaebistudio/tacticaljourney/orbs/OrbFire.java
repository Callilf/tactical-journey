/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.orbs;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Descriptions;
import com.dokkaebistudio.tacticaljourney.ces.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.orbs.OrbCarrierComponent;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffBurning;
import com.dokkaebistudio.tacticaljourney.util.CreepUtil;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;

/**
 * @author Callil
 *
 */
public class OrbFire extends Orb {

	public OrbFire() {
		super(Descriptions.ORB_FIRE_TITLE);
	}

	@Override
	public boolean effectOnContact(Entity user, Entity orb, Entity target, Room room) {
		OrbCarrierComponent orbCarrierComponent = Mappers.orbCarrierComponent.get(user);
		GridPositionComponent targetPos = Mappers.gridPositionComponent.get(target);
		
		Journal.addEntry("[ORANGE]Fire orb has been activated");
		
		// Burning status
		StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(target);
		if (statusReceiverComponent != null) {
			statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffBurning(user));
		}

		// Fire creep
		room.entityFactory.creepFactory.createFire(room, targetPos.coord(), user);
		if (orbCarrierComponent.getNorthOrb() == orb) {
			boolean added = addOneFire(PoolableVector2.create(targetPos.coord().x - 1, targetPos.coord().y + 1), room, user);
			if (added) {
				addOneFire(PoolableVector2.create(targetPos.coord().x - 2, targetPos.coord().y + 2), room, user);
			}
			
			added = addOneFire(PoolableVector2.create(targetPos.coord().x, targetPos.coord().y + 1), room, user);
			if (added) {
				addOneFire(PoolableVector2.create(targetPos.coord().x - 1, targetPos.coord().y + 2), room, user);
				addOneFire(PoolableVector2.create(targetPos.coord().x, targetPos.coord().y + 2), room, user);
				addOneFire(PoolableVector2.create(targetPos.coord().x + 1, targetPos.coord().y + 2), room, user);
			}
			
			added = addOneFire(PoolableVector2.create(targetPos.coord().x + 1, targetPos.coord().y + 1), room, user);
			if (added) {
				addOneFire(PoolableVector2.create(targetPos.coord().x + 2, targetPos.coord().y + 2), room, user);
			}
		}
		
		
		if (orbCarrierComponent.getSouthOrb() == orb) {
			boolean added = addOneFire(PoolableVector2.create(targetPos.coord().x - 1, targetPos.coord().y - 1), room, user);
			if (added) {
				addOneFire(PoolableVector2.create(targetPos.coord().x - 2, targetPos.coord().y - 2), room, user);
			}
			
			added = addOneFire(PoolableVector2.create(targetPos.coord().x, targetPos.coord().y - 1), room, user);
			if (added) {
				addOneFire(PoolableVector2.create(targetPos.coord().x - 1, targetPos.coord().y - 2), room, user);
				addOneFire(PoolableVector2.create(targetPos.coord().x, targetPos.coord().y - 2), room, user);
				addOneFire(PoolableVector2.create(targetPos.coord().x + 1, targetPos.coord().y - 2), room, user);
			}
			
			added = addOneFire(PoolableVector2.create(targetPos.coord().x + 1, targetPos.coord().y - 1), room, user);
			if (added) {
				addOneFire(PoolableVector2.create(targetPos.coord().x + 2, targetPos.coord().y - 2), room, user);
			}
		}
		
		
		if (orbCarrierComponent.getWestOrb() == orb) {
			boolean added = addOneFire(PoolableVector2.create(targetPos.coord().x - 1, targetPos.coord().y - 1), room, user);
			if (added) {
				addOneFire(PoolableVector2.create(targetPos.coord().x - 2, targetPos.coord().y - 2), room, user);
			}
			
			added = addOneFire(PoolableVector2.create(targetPos.coord().x - 1, targetPos.coord().y), room, user);
			if (added) {
				addOneFire(PoolableVector2.create(targetPos.coord().x - 2, targetPos.coord().y - 1), room, user);
				addOneFire(PoolableVector2.create(targetPos.coord().x - 2 , targetPos.coord().y), room, user);
				addOneFire(PoolableVector2.create(targetPos.coord().x - 2, targetPos.coord().y + 1), room, user);
			}
			
			added = addOneFire(PoolableVector2.create(targetPos.coord().x - 1, targetPos.coord().y + 1), room, user);
			if (added) {
				addOneFire(PoolableVector2.create(targetPos.coord().x - 2, targetPos.coord().y + 2), room, user);
			}
		}

		
		if (orbCarrierComponent.getEasthOrb() == orb) {
			boolean added = addOneFire(PoolableVector2.create(targetPos.coord().x + 1, targetPos.coord().y - 1), room, user);
			if (added) {
				addOneFire(PoolableVector2.create(targetPos.coord().x + 2, targetPos.coord().y - 2), room, user);
			}
			
			added = addOneFire(PoolableVector2.create(targetPos.coord().x + 1, targetPos.coord().y), room, user);
			if (added) {
				addOneFire(PoolableVector2.create(targetPos.coord().x + 2, targetPos.coord().y - 1), room, user);
				addOneFire(PoolableVector2.create(targetPos.coord().x + 2 , targetPos.coord().y), room, user);
				addOneFire(PoolableVector2.create(targetPos.coord().x + 2, targetPos.coord().y + 1), room, user);
			}
			
			added = addOneFire(PoolableVector2.create(targetPos.coord().x + 1, targetPos.coord().y + 1), room, user);
			if (added) {
				addOneFire(PoolableVector2.create(targetPos.coord().x + 2, targetPos.coord().y + 2), room, user);
			}
		}
		
		return true;
	}

	private boolean addOneFire(PoolableVector2 pos, Room room, Entity user) {
		if (CreepUtil.canCatchFire(pos, room)) {
			room.entityFactory.creepFactory.createFire(room, pos, user);
			pos.free();
			return true;
		}
		pos.free();
		return false;
	}
	
	
	public int getHeuristic(Entity mover) {
		return 1;
	}
	
}
