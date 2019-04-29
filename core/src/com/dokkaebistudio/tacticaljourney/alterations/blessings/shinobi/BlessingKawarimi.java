/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings.shinobi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffEntangled;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffStunned;
import com.dokkaebistudio.tacticaljourney.systems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.dokkaebistudio.tacticaljourney.vfx.VFXUtil;

/**
 * Kawarimi : Body replacement technique.
 * @author Callil
 *
 */
public class BlessingKawarimi extends Blessing {
	
	private int chanceToProc = 33;
	private Map<Integer, Boolean> activationForRoom = new HashMap<>();

	@Override
	public String title() {
		return "Kawarimi no jutsu";
	}
	
	@Override
	public String description() {
		return "Also known as Body replacement technique. Chance to negate the first attack you receive in a room.";
	}
	
	@Override
	public RegionDescriptor texture() {
		return Assets.blessing_kawarimi;
	}
	
	@Override
	public Integer getCurrentProcChance(Entity user) {
		return chanceToProc;
	}
	
	@Override
	public void onReceive(Entity entity) {
		Room room = Mappers.gridPositionComponent.get(entity).room;
		if (!activationForRoom.containsKey(room.getIndex())) {
			activationForRoom.put(room.getIndex(), false);
		}
	}
	

	@Override
	public void onRoomVisited(Entity entity, Room room) {
		if (!activationForRoom.containsKey(room.getIndex())) {
			activationForRoom.put(room.getIndex(), false);
		}
	}
	
	@Override
	public boolean onReceiveAttack(Entity user, Entity attacker, Room room) {
		if (!activationForRoom.containsKey(room.getIndex())) {
			activationForRoom.put(room.getIndex(), true);
		}
		
		if (!activationForRoom.get(room.getIndex())) {
			// Switch to activated even if it doesn't proc, so that it won't proc on nother attack in the same room.
			activationForRoom.put(room.getIndex(), true);
			
			StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(user);
			if (statusReceiverComponent.hasAtLeastOneStatus(StatusDebuffEntangled.class, StatusDebuffStunned.class)) {
				// If immobilized, do not evade
				Journal.addEntry("Kawarimi activated but could not avoid the attack since " 
						+ Mappers.inspectableComponent.get(user).getTitle() 
						+ " is immobilized by a status effect.");
				return true;
			}
			
			float randomValue = RandomSingleton.getNextChanceWithKarma();
			if (randomValue < getCurrentProcChance(user)) {

				GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(user);
				List<Vector2> possibleMoves = getPossibleMoves(user, gridPositionComponent, room);
				if (possibleMoves.isEmpty()) {
					// No tile available to move
					Journal.addEntry("Kawarimi activated but could not avoid the attack since there are no adjacent tile to move to.");
					AlterationSystem.addAlterationProc(this);
					return true;
				}
				
				
				Journal.addEntry("Kawarimi activated and evaded the attack.");
				AlterationSystem.addAlterationProc(this);
								
				// Effect on the previous tile : smoke + log
				VFXUtil.createLogEffect(gridPositionComponent.coord());
				VFXUtil.createSmokeEffect(gridPositionComponent.coord());
				
				// Select the new tile + add smoke
				Collections.shuffle(possibleMoves,  RandomSingleton.getInstance().getUnseededRandom());
				Vector2 newPlace = possibleMoves.get(0);
				MovementHandler.placeEntity(user, newPlace, room);
				VFXUtil.createSmokeEffect(newPlace);

				// Cancel the enemy attack
				return false;
			}
		}
		
		// Did nothing, proceed with the attack
		return true;
	}

	
	@Override
	public void onFloorVisited(Entity entity, Floor floor, Room room) {
		// Changed floor, clear the activation map to save memory
		this.activationForRoom.clear();
	}
	
	//*********
	// Utils
	

	/**
	 * Get the list of positions where the player can teleport to.
	 * @param mover the mover
	 * @param gridPositionComponent the current player pos
	 * @param room the room
	 * @return a list of positions
	 */
	public static List<Vector2> getPossibleMoves(Entity mover, GridPositionComponent gridPositionComponent, Room room) {
		List<Vector2> possibleMoves = new ArrayList<>();
		PoolableVector2 temp = PoolableVector2.create(gridPositionComponent.coord());
		temp.x += 1;
		if (TileUtil.isTileWalkableForEntity(mover, temp, room)) {
			possibleMoves.add(PoolableVector2.create(temp));
		}
		temp.x -= 1;
		
		temp.x -= 1;
		if (TileUtil.isTileWalkableForEntity(mover, temp, room)) {
			possibleMoves.add(PoolableVector2.create(temp));
		}
		temp.x += 1;

		temp.y += 1;
		if (TileUtil.isTileWalkableForEntity(mover, temp, room)) {
			possibleMoves.add(PoolableVector2.create(temp));
		}
		temp.y -= 1;

		temp.y -= 1;
		if (TileUtil.isTileWalkableForEntity(mover, temp, room)) {
			possibleMoves.add(PoolableVector2.create(temp));
		}
		temp.y += 1;
		temp.free();
		
		return possibleMoves;
	}

}
