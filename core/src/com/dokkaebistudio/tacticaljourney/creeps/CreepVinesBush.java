/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.creeps;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.ces.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.StatusReceiverComponent.StatusActionEnum;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.InventoryComponent;
import com.dokkaebistudio.tacticaljourney.creature.enemies.enums.EnemyFactionEnum;
import com.dokkaebistudio.tacticaljourney.factory.EntityFlagEnum;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.ItemVegetalGarment;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffEntangled;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;

/**
 * Bush that slows movements and hide visibility + chance to entangle anything that walks through it.
 * @author Callil
 *
 */
public class CreepVinesBush extends Creep {
	
	private int chanceToProc = 100;
	
	public CreepVinesBush() {
		super("bush", Assets.tallGrass);
		type = CreepType.VINES_BUSH;
	}
	

	@Override
	public void onWalk(Entity walker, Entity creep, Room room) {
		if (Mappers.enemyComponent.has(walker)) {
			if (Mappers.enemyComponent.get(walker).getFaction() == EnemyFactionEnum.ORANGUTANS) return;
		}
		
		int chance = RandomSingleton.getInstance().nextUnseededInt(100);
		if (chance < chanceToProc) {
			GridPositionComponent creepPos = Mappers.gridPositionComponent.get(creep);

			// Entangle
			StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(walker);
			if (statusReceiverComponent != null) {
				Journal.addEntry("[FOREST]Vines surged from the bush and entangled " + Mappers.inspectableComponent.get(walker).getTitle());
				statusReceiverComponent.requestAction(StatusActionEnum.RECEIVE_STATUS, new StatusDebuffEntangled(3));
			}
			
			boolean immune = false;
			InventoryComponent inventoryComponent = Mappers.inventoryComponent.get(walker);
			immune = inventoryComponent != null && inventoryComponent.contains(ItemVegetalGarment.class);
			
			if (!immune) {
				// Stop movement
				MoveComponent moveComponent = Mappers.moveComponent.get(walker);
				moveComponent.setMoveRemaining(0);
				if (moveComponent.moving) {
					moveComponent.setSelectedTile(creepPos.coord(), room);
					MovementHandler.finishRealMovement(walker, room);
				}
			}
			
			room.entityFactory.createSpriteOnTile(creepPos.coord(), 2, Assets.tallGrass_destroyed, EntityFlagEnum.DESTROYED_SPRITE, room);

			room.removeEntity(creep);

		}
	}
	
	@Override
	public int getMovementConsumed(Entity mover) {
		if (Mappers.enemyComponent.has(mover)) {
			if (Mappers.enemyComponent.get(mover).getFaction() == EnemyFactionEnum.ORANGUTANS) return -1;
		}
		
		if (isImmune(mover)) {
			return 0;
		} else {
			return 1;
		}
	}

}
