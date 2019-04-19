package com.dokkaebistudio.tacticaljourney.enemies;

import java.util.Collections;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.shinobi.BlessingKawarimi;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffEntangled;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffStunned;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;

public class EnemyShinobi extends Enemy {
	
	private boolean kawarimiActivated = false;
	private boolean smokeBombUsed = false;

	@Override
	public String title() {
		return "Fallen shinobi";
	}
	
	
	
	@Override
	public boolean onReceiveAttack(Entity enemy, Entity attacker, Room room) {
		if (kawarimiActivated) return true;
		
		kawarimiActivated = true;
		
		StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(enemy);
		if (statusReceiverComponent.hasAtLeastOneStatus(StatusDebuffEntangled.class, StatusDebuffStunned.class)) {
			// If immobilized, do not evade
			return true;
		}
		
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(enemy);
		List<Vector2> possibleMoves = BlessingKawarimi.getPossibleMoves(enemy, gridPositionComponent, room);
		if (possibleMoves.isEmpty()) {
			// No tile available to move
			Journal.addEntry("Kawarimi activated but could not avoid the attack since there are no adjacent tile to move to.");
			return true;
		}
		
		Journal.addEntry("Kawarimi activated and evaded the attack.");
						
		// Effect on the previous tile : smoke + log
		BlessingKawarimi.createLogEffect(gridPositionComponent.coord());
		BlessingKawarimi.createSmokeEffect(gridPositionComponent.coord());
		
		// Select the new tile + add smoke
		Collections.shuffle(possibleMoves,  RandomSingleton.getInstance().getUnseededRandom());
		Vector2 newPlace = possibleMoves.get(0);
		MovementHandler.placeEntity(enemy, newPlace, room);
		BlessingKawarimi.createSmokeEffect(newPlace);
		

		// Cancel the enemy attack
		return false;
	}

	public boolean isKawarimiActivated() {
		return kawarimiActivated;
	}

	public boolean isSmokeBombUsed() {
		return smokeBombUsed;
	}

	public void setSmokeBombUsed(boolean smokeBombUsed) {
		this.smokeBombUsed = smokeBombUsed;
	}
}
