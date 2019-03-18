package com.dokkaebistudio.tacticaljourney.systems.enemies;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.EnemyComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.enemies.enums.EnemyFactionEnum;
import com.dokkaebistudio.tacticaljourney.enemies.tribesmen.EnemyTribesmanScout;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.systems.EnemySystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class TribesmanScoutSubSystem extends EnemySubSystem {
	

	
	@Override
	public boolean update(final EnemySystem enemySystem, final Entity enemy, final Room room) {		
		
		EnemyComponent enemyComponent = Mappers.enemyComponent.get(enemy);
		EnemyTribesmanScout scoutType = (EnemyTribesmanScout) enemyComponent.getType();
		StateComponent stateComponent = Mappers.stateComponent.get(enemy);

		
		switch(room.getState()) {

		case ENEMY_ATTACK:

			//TODO
			// Alert other tribesmen
			if (scoutType.hasAlertedOthers()) {
				
				return false;
				
			} else {
				
				AttackComponent attackCompo = Mappers.attackComponent.get(enemy);
				GridPositionComponent enemyCurrentPos = Mappers.gridPositionComponent.get(enemy);
				
				//Check if he sees the player
        		boolean sawPlayer = false;
    	    	if (attackCompo.isActive() && attackCompo.attackableTiles != null && !attackCompo.attackableTiles.isEmpty()) {
    	    		for (Entity attTile : attackCompo.attackableTiles) {
    	    			GridPositionComponent attTilePos = Mappers.gridPositionComponent.get(attTile);
    	    			int range = TileUtil.getDistanceBetweenTiles(enemyCurrentPos.coord(), attTilePos.coord());
						if (range <= attackCompo.getRangeMax() && range >= attackCompo.getRangeMin()) {
    	    				//Attack possible
							Entity player = TileUtil.getEntityWithComponentOnTile(attTilePos.coord(), PlayerComponent.class, room);
							if (player != null) {
	            				sawPlayer = true;
	            				break;
							}
    	    			}
    	    		}
    	    	}
    	    	attackCompo.hideAttackableTiles();
				

				if (sawPlayer) {
					scoutType.setHasAlertedOthers(true);
					Journal.addEntry("[RED]" + scoutType.title() + " alerted all other tribesmen of your presence.");
					
					for(Entity e : room.getEnemies()) {
						EnemyComponent ec = Mappers.enemyComponent.get(e);
						if (ec.getFaction() == EnemyFactionEnum.TRIBESMEN) {
							ec.setAlerted(true);
						}
					}
				}
				
	    		room.setNextState(RoomState.ENEMY_ATTACK_FINISH);
				return true;

			}
			
			default:
		
		}
		
		return false;
	}

	
}
