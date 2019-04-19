package com.dokkaebistudio.tacticaljourney.systems.enemies;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.BlockVisibilityComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.systems.EnemySystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class ShinobiSubSystem extends EnemySubSystem {
	
	private boolean isSleeping = true;
	
	@Override
	public boolean update(final EnemySystem enemySystem, final Entity enemy, final Room room) {
		
		switch(room.getState()) {
		
		case ENEMY_TURN_INIT:
			if (isSleeping) {
				Mappers.stateComponent.get(enemy).set(StatesEnum.SHINOBI_SLEEPING);
			}
			break;

    	case ENEMY_MOVE_TILES_DISPLAYED:
    		if (isSleeping) {
        		Vector2 playerPos = Mappers.gridPositionComponent.get(GameScreen.player).coord();
        		for(Tile t : Mappers.attackComponent.get(enemy).allAttackableTiles) {
        			if (t.getGridPos().equals(playerPos)) {
        				// Sees the player
        				isSleeping = false;
        				break;
        			}
        		}
    		}
    			
    		if (isSleeping) {
    			room.setNextState(RoomState.ENEMY_END_MOVEMENT);
    			return true;
    		}
    		break;
    		
    	case ENEMY_ATTACK:
    			break;
    		
    	default:
    	}
		
		return false;
	}	
	
	
	
	
	@Override
	public boolean computeMovableTilesToDisplayToPlayer(EnemySystem system, Entity enemyEntity, Room room) {
		MoveComponent moveCompo = Mappers.moveComponent.get(enemyEntity);
    	AttackComponent attackCompo = Mappers.attackComponent.get(enemyEntity);
    	
		//clear the movable tile
		moveCompo.clearMovableTiles();
		if (attackCompo != null) attackCompo.clearAttackableTiles();
		
		moveCompo.setMoveRemaining(moveCompo.getMoveSpeed());
    		
    	//Build the movable tiles list
		system.getTileSearchService().buildMoveTilesSet(enemyEntity, room);
		if (attackCompo != null) system.getAttackTileSearchService().buildAttackTilesSet(enemyEntity, room, false, true);
		
		if (!moveCompo.isFrozen()) {
			// Add the horizontal and vertical lines
			Set<Entity> additionnalAttackableTiles = new HashSet<>();
			PoolableVector2 temp = PoolableVector2.create(0, 0);
			GridPositionComponent enemyPos = Mappers.gridPositionComponent.get(enemyEntity);
			int i = (int) enemyPos.coord().x - 1;
			while (i >= 0) {
				// left
				temp.set(i, enemyPos.coord().y);
				if (!checkTile(temp, additionnalAttackableTiles, moveCompo, attackCompo, room)) {
					break;
				}
				i--;
			}
			
			i = (int) enemyPos.coord().x + 1;
			while (i < GameScreen.GRID_W) {
				// right
				temp.set(i, enemyPos.coord().y);
				if (!checkTile(temp, additionnalAttackableTiles, moveCompo, attackCompo, room)) {
					break;
				}
				i++;
			}
			
			i = (int) enemyPos.coord().y - 1;
			while (i >= 0) {
				// down
				temp.set(enemyPos.coord().x, i);
				if (!checkTile(temp, additionnalAttackableTiles, moveCompo, attackCompo, room)) {
					break;
				}
				i--;
			}
			
			i = (int) enemyPos.coord().y + 1;
			while (i < GameScreen.GRID_H) {
				// up
				temp.set(enemyPos.coord().x, i);
				if (!checkTile(temp, additionnalAttackableTiles, moveCompo, attackCompo, room)) {
					break;
				}
				i++;
			}
			temp.free();	
		
			attackCompo.attackableTiles.addAll(additionnalAttackableTiles);
		}

		moveCompo.hideMovableTiles();
		attackCompo.hideAttackableTiles();		
		return true;
	}




	private boolean checkTile(PoolableVector2 position, Set<Entity> additionnalAttackableTiles, MoveComponent moveCompo,
			AttackComponent attackCompo, Room room) {
		Tile tileAtGridPos = TileUtil.getTileAtGridPos(position, room);
		Entity solid = TileUtil.getEntityWithComponentOnTile(position, SolidComponent.class, room);
		Entity blockVision = TileUtil.getEntityWithComponentOnTile(position, BlockVisibilityComponent.class, room);
		if (!attackCompo.allAttackableTiles.contains(tileAtGridPos) && !moveCompo.allWalkableTiles.contains(tileAtGridPos)) {
			additionnalAttackableTiles.add(room.entityFactory.createAttackableTile(position, room, false));
		}
		return solid == null && blockVision == null;
	}
	
	
	private boolean canChargePlayer(Entity enemyEntity, MoveComponent moveCompo, AttackComponent attackCompo, Room room) {
		if (moveCompo.getMoveSpeed() == 0) return false;
		
		// Add the horizontal and vertical lines
		PoolableVector2 temp = PoolableVector2.create(0, 0);
		GridPositionComponent enemyPos = Mappers.gridPositionComponent.get(enemyEntity);
		int i = (int) enemyPos.coord().x - 1;
		while (i >= 0) {
			// left
			temp.set(i, enemyPos.coord().y);
			if (checkTileForPlayer(temp, moveCompo, attackCompo, room)) {
				return true;
			}
			if (checkTileForSolid(temp, moveCompo, attackCompo, room)) {
				break;
			}
			i--;
		}
		
		i = (int) enemyPos.coord().x + 1;
		while (i < GameScreen.GRID_W) {
			// right
			temp.set(i, enemyPos.coord().y);
			if (checkTileForPlayer(temp, moveCompo, attackCompo, room)) {
				return true;
			}
			if (checkTileForSolid(temp, moveCompo, attackCompo, room)) {
				break;
			}
			i++;
		}
		
		i = (int) enemyPos.coord().y - 1;
		while (i >= 0) {
			// down
			temp.set(enemyPos.coord().x, i);
			if (checkTileForPlayer(temp, moveCompo, attackCompo, room)) {
				return true;
			}
			if (checkTileForSolid(temp, moveCompo, attackCompo, room)) {
				break;
			}
			i--;
		}
		
		i = (int) enemyPos.coord().y + 1;
		while (i < GameScreen.GRID_H) {
			// up
			temp.set(enemyPos.coord().x, i);
			if (checkTileForPlayer(temp, moveCompo, attackCompo, room)) {
				return true;
			}
			if (checkTileForSolid(temp, moveCompo, attackCompo, room)) {
				break;
			}
			i++;
		}
		temp.free();
		
		return false;
	}
	
	private boolean checkTileForSolid(PoolableVector2 position, MoveComponent moveCompo, AttackComponent attackCompo, Room room) {
		Entity solid = TileUtil.getEntityWithComponentOnTile(position, SolidComponent.class, room);
		if (solid != null) return true;
		Entity blockVision = TileUtil.getEntityWithComponentOnTile(position, BlockVisibilityComponent.class, room);
		return blockVision != null;
	}
	private boolean checkTileForPlayer(PoolableVector2 position, MoveComponent moveCompo, AttackComponent attackCompo, Room room) {
		Entity player = TileUtil.getEntityWithComponentOnTile(position, PlayerComponent.class, room);
		return player != null;
	}


	
}
