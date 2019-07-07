package com.dokkaebistudio.tacticaljourney.creature.enemies;

import java.util.Collections;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.blessings.shinobi.BlessingKawarimi;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.creature.Creature;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffEntangled;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffStunned;
import com.dokkaebistudio.tacticaljourney.systems.entitysystems.creatures.subsystems.ShinobiSubSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;
import com.dokkaebistudio.tacticaljourney.vfx.VFXUtil;

public class EnemyShinobi extends Creature {
	
	private boolean sleeping = true;
	private boolean kawarimiActivated = false;
	private boolean smokeBombUsed = false;
	private Boolean receivedFirstDamage;

	@Override
	public String title() {
		return "Fallen shinobi";
	}
	
	
	@Override
	public void onRoomVisited(Entity enemy, Room room) {
		// Place the shinobi so that it is far enough from the player
		GridPositionComponent playerPos = Mappers.gridPositionComponent.get(GameScreen.player);
		if (playerPos.coord().x < 11) {
			MovementHandler.placeEntity(enemy, ShinobiSubSystem.RIGHT_CLONE_TILE, room);
		} else if (playerPos.coord().x > 11) {
			MovementHandler.placeEntity(enemy, ShinobiSubSystem.LEFT_CLONE_TILE, room);
		} else if (playerPos.coord().y < 6) {
			MovementHandler.placeEntity(enemy, ShinobiSubSystem.UP_CLONE_TILE, room);
		} else if (playerPos.coord().y > 6) {
			MovementHandler.placeEntity(enemy, ShinobiSubSystem.DOWN_CLONE_TILE, room);
		}
		
		// Orient the shinobi towards the player
		Mappers.spriteComponent.get(enemy).orientSprite(enemy, playerPos.coord());
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
		VFXUtil.createLogEffect(gridPositionComponent.coord());
		VFXUtil.createSmokeEffect(gridPositionComponent.coord());
		
		// Select the new tile + add smoke
		Collections.shuffle(possibleMoves,  RandomSingleton.getInstance().getUnseededRandom());
		Vector2 newPlace = possibleMoves.get(0);
		MovementHandler.placeEntity(enemy, newPlace, room);
		VFXUtil.createSmokeEffect(newPlace);
		

		// Cancel the enemy attack
		return false;
	}
	
	@Override
	public void onReceiveDamage(int damage, Entity enemy, Entity attacker, Room room) {
		if (damage > 0 && receivedFirstDamage == null) {
			receivedFirstDamage = true;
		}
	}
	
	
	
	@Override
	public void onAlerted(Entity enemy, Entity target, Room room) {
		sleeping = false;
	}
	

	public boolean isKawarimiActivated() {
		return kawarimiActivated;
	}
	
	public void setKawarimiActivated(boolean kawarimiActivated) {
		this.kawarimiActivated = kawarimiActivated;
	}

	public boolean isSmokeBombUsed() {
		return smokeBombUsed;
	}

	public void setSmokeBombUsed(boolean smokeBombUsed) {
		this.smokeBombUsed = smokeBombUsed;
	}



	public Boolean getReceivedFirstDamage() {
		return receivedFirstDamage;
	}



	public void setReceivedFirstDamage(Boolean receivedFirstDamage) {
		this.receivedFirstDamage = receivedFirstDamage;
	}



	public boolean isSleeping() {
		return sleeping;
	}



	public void setSleeping(boolean sleeping) {
		this.sleeping = sleeping;
	}
}
