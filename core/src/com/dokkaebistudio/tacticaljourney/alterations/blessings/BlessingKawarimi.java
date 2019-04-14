/**
 * 
 */
package com.dokkaebistudio.tacticaljourney.alterations.blessings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.descriptors.RegionDescriptor;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * Kawarimi : Body replacement technique.
 * @author Callil
 *
 */
public class BlessingKawarimi extends Blessing {
	
	private boolean activated;
	private int chanceToProc = 100;

	@Override
	public String title() {
		return "Kawarimi";
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
	public void onRoomVisited(Entity entity, Room room) {
		this.activated = false;
	}
	
	@Override
	public boolean onReceiveAttack(Entity user, Entity attacker, Room room) {
		if (!activated) {
			Journal.addEntry("Kawarimi activated and evaded the attack.");
			AlterationSystem.addAlterationProc(this);
			
			
			// Move the player
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(user);
			
			room.entityFactory.effectFactory.createExplosionEffect(	room, gridPositionComponent.coord());

			
			List<Vector2> possibleMoves = new ArrayList<>();
			
			PoolableVector2 temp = PoolableVector2.create(gridPositionComponent.coord());
			temp.x += 1;
			if (TileUtil.getSolidEntityOnTile(temp, room) == null) {
				possibleMoves.add(PoolableVector2.create(temp));
				temp.x -= 1;
			}
			
			temp.x -= 1;
			if (TileUtil.getSolidEntityOnTile(temp, room) == null) {
				possibleMoves.add(PoolableVector2.create(temp));
				temp.x += 1;
			}
			
			temp.y += 1;
			if (TileUtil.getSolidEntityOnTile(temp, room) == null) {
				possibleMoves.add(PoolableVector2.create(temp));
				temp.y -= 1;
			}
			
			temp.y -= 1;
			if (TileUtil.getSolidEntityOnTile(temp, room) == null) {
				possibleMoves.add(PoolableVector2.create(temp));
				temp.y += 1;
			}
			temp.free();
			
			if (!possibleMoves.isEmpty()) {
				Collections.shuffle(possibleMoves,  RandomSingleton.getInstance().getUnseededRandom());
				Vector2 newPlace = possibleMoves.get(0);
				MovementHandler.placeEntity(user, newPlace, room);
				room.entityFactory.effectFactory.createExplosionEffect(	room, newPlace);			
			} else {
				// No place to move, stay on the same tile
				room.entityFactory.effectFactory.createExplosionEffect(	room, gridPositionComponent.coord());
			}
			
			activated = true;
			// Cancel the enemy attack
			return false;
		}
		
		// Did nothing, proceed with the attack
		return true;
	}

}
