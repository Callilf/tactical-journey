package com.dokkaebistudio.tacticaljourney.systems.enemies.tribesmen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.orbs.OrbCarrierComponent;
import com.dokkaebistudio.tacticaljourney.enemies.tribesmen.EnemyTribesmanShaman;
import com.dokkaebistudio.tacticaljourney.enemies.tribesmen.EnemyTribesmanTotem;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.systems.EnemySystem;
import com.dokkaebistudio.tacticaljourney.systems.enemies.EnemySubSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;

public class TribesmanShamanSubSystem extends EnemySubSystem {
	
	private Entity totem;
	private boolean recovering;
	private boolean summoningTotem;
	private boolean summoningOrb;
	
	private int numberOfObsSummoned;
	
	@Override
	public boolean update(final EnemySystem enemySystem, final Entity enemy, final Room room) {		
		
		EnemyComponent enemyComponent = Mappers.enemyComponent.get(enemy);
		EnemyTribesmanShaman shamanType = (EnemyTribesmanShaman) enemyComponent.getType();

		
		switch(room.getState()) {
		
		case ENEMY_TURN_INIT:
			
			// Check if there is a totem in the room
			boolean totemFound = false;
			for (Entity e : room.getEnemies()) {
				if (Mappers.enemyComponent.get(e).getType() instanceof EnemyTribesmanTotem) {
					totem = e;
					totemFound = true;
					break;
				}
			}
			if (!totemFound) totem = null;
			
			break;

		case ENEMY_ATTACK:
			
			if (recovering) {
				recovering = false;
	    		room.setNextState(RoomState.ENEMY_ATTACK_FINISH);
				return true;
			}
			
			if (summoningTotem) {
				PoolableVector2 temp = PoolableVector2.create(11,  6);
				totem = room.entityFactory.enemyFactory.createTribesmenTotem(room, temp);
				Mappers.enemyComponent.get(totem).setTurnOver(true);
				temp.free();
				
				summoningTotem = false;
				recovering = true;
				Mappers.stateComponent.get(enemy).set(StatesEnum.TRIBESMEN_SHAMAN_STAND.getState());
	    		room.setNextState(RoomState.ENEMY_ATTACK_FINISH);
				return true;
				
			} else if (summoningOrb) {
				if (totem != null) {
					OrbCarrierComponent orbCarrierComponent = Mappers.orbCarrierComponent.get(totem);
					orbCarrierComponent.acquire(totem, generateOrb(room));
				}
				
				summoningOrb = false;
				recovering = true;
				Mappers.stateComponent.get(enemy).set(StatesEnum.TRIBESMEN_SHAMAN_STAND.getState());
	    		room.setNextState(RoomState.ENEMY_ATTACK_FINISH);
				return true;
			} else {
				
				// NOT SUMMONING
			
				if (totem == null) {
					// No totem, summon one
					summoningTotem = true;
					Mappers.stateComponent.get(enemy).set(StatesEnum.TRIBESMEN_SHAMAN_SUMMONING.getState());
					
		    		room.setNextState(RoomState.ENEMY_ATTACK_FINISH);
					return true;
				} else {
					
					OrbCarrierComponent orbCarrierComponent = Mappers.orbCarrierComponent.get(totem);
					if (orbCarrierComponent.getOrbs().size() < 4) {
						// Orb slot available
						summoningOrb = true;
						Mappers.stateComponent.get(enemy).set(StatesEnum.TRIBESMEN_SHAMAN_SUMMONING.getState());
						
			    		room.setNextState(RoomState.ENEMY_ATTACK_FINISH);
						return true;
					}
					
				}
				
			}
			
			break;
			
			default:
		
		}
		
		return false;
	}

	
	private Entity generateOrb(Room room) {
		numberOfObsSummoned ++;
		
		RandomXS128 unseededRandom = RandomSingleton.getInstance().getUnseededRandom();
		int randInt = unseededRandom.nextInt(101);
		if (randInt >= 0 && randInt < 25) {
			return room.entityFactory.orbFactory.createEnergyOrb(null, room);
		} else if (randInt >= 25 && randInt < 50) {
			return room.entityFactory.orbFactory.createVegetalOrb(null, room);
		} else if (randInt >= 50 && randInt < 75) {
			return room.entityFactory.orbFactory.createPoisonOrb(null, room);
		} else if ( randInt >= 75 && randInt < 100) {
			return room.entityFactory.orbFactory.createFireOrb(null, room);
		} else {
			return room.entityFactory.orbFactory.createDeathOrb(null, room);
		}
	}
	
}
