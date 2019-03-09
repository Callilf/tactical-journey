package com.dokkaebistudio.tacticaljourney.systems.enemies;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.systems.EnemySystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class StingerSubSystem extends EnemySubSystem {
	
	
	private enum StingerAttackStates {
		NONE,
		INIT_CHARGE,
		CHARGE,
		END_CHARGE;
	}
	
	private boolean canCharge = false;
	private int chargeDistance = 0;
	private StingerAttackStates attackState = StingerAttackStates.NONE;
	
	@Override
	public boolean update(final EnemySystem enemySystem, final Entity enemy, final Room room) {
    	MoveComponent moveCompo = Mappers.moveComponent.get(enemy);
    	final AttackComponent attackCompo = Mappers.attackComponent.get(enemy);
    	GridPositionComponent enemyCurrentPos = Mappers.gridPositionComponent.get(enemy);

		Entity playerEntity = room.floor.getGameScreen().player;
		GridPositionComponent playerPosition = Mappers.gridPositionComponent.get(playerEntity);

		
		switch(room.getState()) {
		
		case ENEMY_TURN_INIT:
			attackCompo.setAdditionnalStrength(0);
			break;

    	case ENEMY_MOVE_TILES_DISPLAYED :
    		
    		// First check whether the stinger in aligned with the player horizontally or vertically    		
    		if (canSeePlayer(enemy, moveCompo, attackCompo, room)) {
    			// Aligned
    			canCharge = true;
    			chargeDistance = TileUtil.getDistanceBetweenTiles(playerPosition.coord(), enemyCurrentPos.coord());
        		room.setNextState(RoomState.ENEMY_ATTACK);
        		return true;
    		} else {
	    		return false;
    		}
    		
    	case ENEMY_ATTACK:
    		
    		if (canCharge) {
    			attackState = StingerAttackStates.INIT_CHARGE;
    			canCharge = false;
    		}
    		
    		if (attackState == StingerAttackStates.NONE) {
    			return false;
    		}
    		
    		
    		switch(attackState) {
    		case INIT_CHARGE:
    			PoolableVector2 chargeLocation = PoolableVector2.create(0, 0);
    			if (enemyCurrentPos.coord().x == playerPosition.coord().x) {
    				// Vertical charge
    				if (enemyCurrentPos.coord().y < playerPosition.coord().y) {
    					chargeLocation.x = playerPosition.coord().x;
    					chargeLocation.y = playerPosition.coord().y - 1;
    					
    					int i = (int) enemyCurrentPos.coord().y;
    					while (i < playerPosition.coord().y - 1) {
    						Entity wp = room.entityFactory.createWaypoint(new Vector2(chargeLocation.x, i), room);
    						moveCompo.getWayPoints().add(wp);
    						i++;
    					}
    				} else {
    					chargeLocation.x = playerPosition.coord().x;
    					chargeLocation.y = playerPosition.coord().y + 1;
    					
    					int i = (int) enemyCurrentPos.coord().y;
    					while (i > playerPosition.coord().y + 1) {
    						Entity wp = room.entityFactory.createWaypoint(new Vector2(chargeLocation.x, i), room);
    						moveCompo.getWayPoints().add(wp);
    						i--;
    					}
    				}
    			} else {
    				// Horizontal charge
    				if (enemyCurrentPos.coord().x < playerPosition.coord().x) {
    					// To the right
    					SpriteComponent spriteComponent = Mappers.spriteComponent.get(enemy);
    					spriteComponent.flipX = false;

    					chargeLocation.x = playerPosition.coord().x - 1;
    					chargeLocation.y = playerPosition.coord().y;
    					
    					int i = (int) enemyCurrentPos.coord().x;
    					while (i < playerPosition.coord().x - 1) {
    						Entity wp = room.entityFactory.createWaypoint(new Vector2(i, chargeLocation.y), room);
    						moveCompo.getWayPoints().add(wp);
    						i++;
    					}
    				} else {
    					// To the left
    					SpriteComponent spriteComponent = Mappers.spriteComponent.get(enemy);
    					spriteComponent.flipX = true;

    					chargeLocation.x = playerPosition.coord().x + 1;
    					chargeLocation.y = playerPosition.coord().y;
    					
    					int i = (int) enemyCurrentPos.coord().x;
    					while (i > playerPosition.coord().x + 1) {
    						Entity wp = room.entityFactory.createWaypoint(new Vector2(i, chargeLocation.y), room);
    						moveCompo.getWayPoints().add(wp);
    						i--;
    					}
    				}
    			}
    			
    			Entity destinationTileEntity = room.entityFactory.createDestinationTile(chargeLocation, room);
    			moveCompo.setSelectedTile(destinationTileEntity);
    			moveCompo.hideMovementEntities();
    			
    			// Stinger animation
    			StateComponent stateComponent = Mappers.stateComponent.get(enemy);
    			stateComponent.set(StatesEnum.STINGER_ATTACK.getState());
    			
    			enemySystem.getMovementHandler().initiateMovement(enemy);
    			attackState = StingerAttackStates.CHARGE;
    			
    			break;
    		case CHARGE:
    	    	moveCompo.selectCurrentMoveDestinationTile(enemy);
	    		
    	    	//Do the movement on screen
    	    	boolean movementFinished = enemySystem.getMovementHandler().performRealMovement(enemy, room, 15);
        		if (movementFinished) attackState = StingerAttackStates.END_CHARGE;

    			break;
    		case END_CHARGE:
    			enemySystem.getMovementHandler().finishRealMovement(enemy, room);
    			attackState = StingerAttackStates.NONE;
    			
    			// Stinger animation
    			stateComponent = Mappers.stateComponent.get(enemy);
    			stateComponent.set(StatesEnum.STINGER_FLY.getState());
    			
				attackCompo.setTarget(playerEntity);
				attackCompo.setAdditionnalStrength(chargeDistance);
				room.setNextState(RoomState.ENEMY_ATTACK_ANIMATION);
    			
    			default:
    		}
    		return true;
    		
    		
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
		
		moveCompo.hideMovableTiles();
		attackCompo.hideAttackableTiles();		
		return true;
	}




	private boolean checkTile(PoolableVector2 position, Set<Entity> additionnalAttackableTiles, MoveComponent moveCompo,
			AttackComponent attackCompo, Room room) {
		Tile tileAtGridPos = TileUtil.getTileAtGridPos(position, room);
		Entity solid = TileUtil.getEntityWithComponentOnTile(position, SolidComponent.class, room);
		if (!attackCompo.allAttackableTiles.contains(tileAtGridPos) && !moveCompo.allWalkableTiles.contains(tileAtGridPos)) {
			additionnalAttackableTiles.add(room.entityFactory.createAttackableTile(position, room, false));
		}
		return solid == null;
	}
	
	
	private boolean canSeePlayer(Entity enemyEntity, MoveComponent moveCompo, AttackComponent attackCompo, Room room) {
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
		return solid != null;
	}
	private boolean checkTileForPlayer(PoolableVector2 position, MoveComponent moveCompo, AttackComponent attackCompo, Room room) {
		Entity player = TileUtil.getEntityWithComponentOnTile(position, PlayerComponent.class, room);
		return player != null;
	}


	
}
