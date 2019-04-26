package com.dokkaebistudio.tacticaljourney.systems.enemies.tribesmen;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.AIComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AllyComponent;
import com.dokkaebistudio.tacticaljourney.enemies.enums.EnemyFactionEnum;
import com.dokkaebistudio.tacticaljourney.enemies.tribesmen.EnemyTribesmanScout;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.systems.EnemySystem;
import com.dokkaebistudio.tacticaljourney.systems.enemies.EnemySubSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class TribesmanScoutSubSystem extends EnemySubSystem {
	

	
	@Override
	public boolean update(final EnemySystem enemySystem, final Entity enemy, final Room room) {		
		
		EnemyComponent enemyComponent = Mappers.enemyComponent.get(enemy);
		AIComponent aiComponent = Mappers.aiComponent.get(enemy);
		EnemyTribesmanScout scoutType = (EnemyTribesmanScout) enemyComponent.getType();

		
		switch(room.getState()) {
		
		case ENEMY_TURN_INIT:
			
			scoutType.setHasFriendsNotAlerted(false);
			for (Entity e : room.getEnemies()) {
				if (e == enemy) continue;
				
				EnemyComponent ec = Mappers.enemyComponent.get(e);
				AIComponent aic = Mappers.aiComponent.get(e);
				if (ec.getFaction() == EnemyFactionEnum.TRIBESMEN && !aic.isAlerted()) {
					// Found one friend that is not alerted yet
					scoutType.setHasFriendsNotAlerted(true);
					break;
				}
			}
			
			return false;

		case ENEMY_ATTACK:

			// Alert other tribesmen
			if (scoutType.hasFriendsNotAlerted()) {
				
        		boolean canAlert = false;

				if (aiComponent.isAlerted()) {
					
					// The scout is alerted, so he's been attacked. Check if he's close to a friend.
					GridPositionComponent enemyPos = Mappers.gridPositionComponent.get(enemy);

					Entity friendToAlert = null;
					for (Entity e : room.getEnemies()) {
						if (e == enemy) continue;
						EnemyComponent ec = Mappers.enemyComponent.get(e);
						if (ec != null && ec.getFaction() == enemyComponent.getFaction()) {
							// Found another member of the faction
							GridPositionComponent friendPos = Mappers.gridPositionComponent.get(e);
							int distanceBetweenTiles = TileUtil.getDistanceBetweenTiles(enemyPos.coord(), friendPos.coord());
							if (distanceBetweenTiles == 1) {
								// The scout is safe, he can alert
								friendToAlert = e;
								break;
							}
						}
					}
					
					if (friendToAlert != null) {
						// ALERT ALL TRIBESMEN OF THE ROOM
						
						EnemyComponent ec = Mappers.enemyComponent.get(friendToAlert);
						AIComponent aic = Mappers.aiComponent.get(friendToAlert);

						Journal.addEntry("[RED]" + scoutType.title() + " alerted " + ec.getType().title() + " of your presence.");
						
						if (ec.getFaction() == EnemyFactionEnum.TRIBESMEN) {
							aic.setAlerted(true, friendToAlert, aiComponent.getTarget());
						}
					}
					
					
				} else {
					// The scout is not alerted, so check if he sees the player
					
					AttackComponent attackCompo = Mappers.attackComponent.get(enemy);
					GridPositionComponent enemyCurrentPos = Mappers.gridPositionComponent.get(enemy);
					
					//Check if he sees the player
	    	    	if (attackCompo.isActive() && attackCompo.attackableTiles != null && !attackCompo.attackableTiles.isEmpty()) {
	    	    		for (Entity attTile : attackCompo.attackableTiles) {
	    	    			GridPositionComponent attTilePos = Mappers.gridPositionComponent.get(attTile);
	    	    			int range = TileUtil.getDistanceBetweenTiles(enemyCurrentPos.coord(), attTilePos.coord());
							if (range <= attackCompo.getRangeMax() && range >= attackCompo.getRangeMin()) {
	    	    				//Attack possible
								Entity ally = TileUtil.getEntityWithComponentOnTile(attTilePos.coord(), AllyComponent.class, room);
								if (ally != null) {
									// The scout sees the player, set it's alert status to TRUE
		            				aiComponent.setAlerted(true, enemy, ally);
		            				break;
								}
	    	    			}
	    	    		}
	    	    	}
	    	    	attackCompo.hideAttackableTiles();
				}
				
				
	    		room.setNextState(RoomState.ENEMY_ATTACK_FINISH);
				return true;

			} else {
				// All other tribesmen are alerted, he can attack que player
				return false;
			}
			
			default:
		
		}
		
		return false;
	}

	
}
