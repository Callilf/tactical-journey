package com.dokkaebistudio.tacticaljourney.systems.creatures.subsystems.tribesmen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.RandomXS128;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.components.ExpRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.loot.LootRewardComponent;
import com.dokkaebistudio.tacticaljourney.components.orbs.OrbCarrierComponent;
import com.dokkaebistudio.tacticaljourney.creature.enemies.tribesmen.EnemyTribesmanScout;
import com.dokkaebistudio.tacticaljourney.creature.enemies.tribesmen.EnemyTribesmanShaman;
import com.dokkaebistudio.tacticaljourney.creature.enemies.tribesmen.EnemyTribesmanShield;
import com.dokkaebistudio.tacticaljourney.creature.enemies.tribesmen.EnemyTribesmanSpear;
import com.dokkaebistudio.tacticaljourney.creature.enemies.tribesmen.EnemyTribesmanTotem;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomCreatureState;
import com.dokkaebistudio.tacticaljourney.systems.creatures.CreatureSystem;
import com.dokkaebistudio.tacticaljourney.systems.creatures.subsystems.CreatureSubSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;

public class TribesmanShamanSubSystem extends CreatureSubSystem {
	
	private Entity totem;
	private int numberOfEnemies;
	private boolean recovering;
	private boolean summoningTotem;
	private boolean summoningOrb;
	private boolean summoningEnemy;
	
	private int numberOfObsSummoned;
	
	@Override
	public boolean update(final CreatureSystem enemySystem, final Entity enemy, final Room room) {		
		
		AIComponent aiComponent = Mappers.aiComponent.get(enemy);
		EnemyTribesmanShaman shamanType = (EnemyTribesmanShaman) aiComponent.getType();

		
		switch(room.getCreatureState()) {
		
		case TURN_INIT:
			
			// Check if there is a totem in the room
			boolean totemFound = false;
			for (Entity e : room.getEnemies()) {
				if (Mappers.aiComponent.get(e).getType() instanceof EnemyTribesmanTotem) {
					totem = e;
					totemFound = true;
					break;
				}
			}
			if (!totemFound) totem = null;
			
			numberOfEnemies = 0;
			for (Entity e : room.getEnemies()) {
				if (Mappers.aiComponent.get(e).getType() instanceof EnemyTribesmanShield
						|| Mappers.aiComponent.get(e).getType() instanceof EnemyTribesmanSpear
						|| Mappers.aiComponent.get(e).getType() instanceof EnemyTribesmanScout) {
					numberOfEnemies ++;
				}
			}
			
			break;

		case ATTACK:
			
			if (recovering) {
				recovering = false;
	    		room.setCreatureState(RoomCreatureState.ATTACK_FINISH);
				return true;
			}
			
			if (summoningTotem) {
				PoolableVector2 temp = PoolableVector2.create(11,  6);
				totem = room.entityFactory.enemyFactory.createTribesmenTotem(room, temp);
				Mappers.aiComponent.get(totem).setTurnOver(true);
				temp.free();
				
				summoningTotem = false;
				recovering = true;
				Mappers.stateComponent.get(enemy).set(StatesEnum.STANDING);
	    		room.setCreatureState(RoomCreatureState.ATTACK_FINISH);
				return true;
				
			} else if (summoningOrb) {
				if (totem != null) {
					OrbCarrierComponent orbCarrierComponent = Mappers.orbCarrierComponent.get(totem);
					orbCarrierComponent.acquire(totem, generateOrb(room));
				}
				
				summoningOrb = false;
				recovering = true;
				Mappers.stateComponent.get(enemy).set(StatesEnum.STANDING);
	    		room.setCreatureState(RoomCreatureState.ATTACK_FINISH);
				return true;
			} else if (summoningEnemy) {
				Entity generateTribesman = generateTribesman(room);
				Mappers.aiComponent.get(generateTribesman).setTurnOver(true);
				
				summoningEnemy = false;
				recovering = true;
				Mappers.stateComponent.get(enemy).set(StatesEnum.STANDING);
	    		room.setCreatureState(RoomCreatureState.ATTACK_FINISH);
				return true;

			} else {
				
				// NOT SUMMONING
			
				if (totem == null) {
					// No totem, summon one
					summoningTotem = true;
					Mappers.stateComponent.get(enemy).set(StatesEnum.TRIBESMEN_SHAMAN_SUMMONING);
					
		    		room.setCreatureState(RoomCreatureState.ATTACK_FINISH);
					return true;
				} else {
					
					int choice = RandomSingleton.getInstance().getUnseededRandom().nextInt(2);
					if (Mappers.orbCarrierComponent.get(totem).getOrbs().size() == 4) {
						choice = 0;
					}
					if (numberOfEnemies == 2) {
						choice = 1;
					}

					
					if (choice == 0) {
						summoningEnemy = true;
						Mappers.stateComponent.get(enemy).set(StatesEnum.TRIBESMEN_SHAMAN_SUMMONING);
						
			    		room.setCreatureState(RoomCreatureState.ATTACK_FINISH);
						return true;

					} else if (choice == 1) {
						
						OrbCarrierComponent orbCarrierComponent = Mappers.orbCarrierComponent.get(totem);
						if (orbCarrierComponent.getOrbs().size() < 4) {
							// Orb slot available
							summoningOrb = true;
							Mappers.stateComponent.get(enemy).set(StatesEnum.TRIBESMEN_SHAMAN_SUMMONING);
							
				    		room.setCreatureState(RoomCreatureState.ATTACK_FINISH);
							return true;
						}
						
					}
				}
				
			}
			
			break;
			
			default:
		
		}
		
		return false;
	}

	
	private Entity generateOrb(Room room) {
		RandomXS128 unseededRandom = RandomSingleton.getInstance().getUnseededRandom();

		if (numberOfObsSummoned >= 3) {
			// After 4 orbs have been summoned, 1 chance out of 3 to summon a void
			// Also 100% chance to summon voids each multiples of 8
			if (numberOfObsSummoned % 8 == 0 || unseededRandom.nextInt(3) == 0) {
				numberOfObsSummoned ++;
				return room.entityFactory.orbFactory.createVoid(null, room);
			}
		}
		
		numberOfObsSummoned ++;
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
	
	
	private Entity generateTribesman(Room room) {
		RandomXS128 unseededRandom = RandomSingleton.getInstance().getUnseededRandom();
		
		PoolableVector2 temp = PoolableVector2.create(11,  6);
		Entity tribesman = null;
		int randInt = unseededRandom.nextInt(75);
		if (randInt >= 0 && randInt < 25) {
			tribesman = room.entityFactory.enemyFactory.createTribesmenSpear( room, temp);
		} else if (randInt >= 25 && randInt < 50) {
			tribesman = room.entityFactory.enemyFactory.createTribesmenShield( room, temp);
		} else {
			tribesman = room.entityFactory.enemyFactory.createTribesmenScout( room, temp);
		}
		
		temp.free();
		
		// Summoned tribesmen do not give XP or loot
		tribesman.remove(LootRewardComponent.class);
		tribesman.remove(ExpRewardComponent.class);
		
		Mappers.aiComponent.get(tribesman).setAlerted(true, tribesman, GameScreen.player);
		return tribesman;
	}
}
