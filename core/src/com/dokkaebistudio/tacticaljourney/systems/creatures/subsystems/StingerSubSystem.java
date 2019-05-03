package com.dokkaebistudio.tacticaljourney.systems.creatures.subsystems;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.BlockVisibilityComponent;
import com.dokkaebistudio.tacticaljourney.components.SolidComponent;
import com.dokkaebistudio.tacticaljourney.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AllyComponent;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomCreatureState;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.systems.creatures.CreatureSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler;
import com.dokkaebistudio.tacticaljourney.util.MovementHandler.MovementProgressEnum;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class StingerSubSystem extends CreatureSubSystem {
	
	
	private enum StingerAttackStates {
		NONE,
		INIT_CHARGE,
		CHARGE,
		END_CHARGE;
	}
	
	private boolean canCharge = false;
	private int chargeDistance = 0;
	private Entity chargeTarget;
	private StingerAttackStates attackState = StingerAttackStates.NONE;
	
	@Override
	public boolean update(final CreatureSystem creatureSystem, final Entity enemy, final Room room) {
    	MoveComponent moveCompo = Mappers.moveComponent.get(enemy);
    	final AttackComponent attackCompo = Mappers.attackComponent.get(enemy);
    	GridPositionComponent enemyCurrentPos = Mappers.gridPositionComponent.get(enemy);

		Entity playerEntity = GameScreen.player;
		GridPositionComponent playerPosition = Mappers.gridPositionComponent.get(playerEntity);

		
		switch(room.getCreatureState()) {
		
		case TURN_INIT:
			attackCompo.setAdditionnalStrength(0);
			break;

    	case MOVE_TILES_DISPLAYED :
    		
    		// First check whether the stinger in aligned with the player horizontally or vertically    		
    		chargeTarget = canChargePlayer(enemy, moveCompo, attackCompo, room);
    		if (chargeTarget != null) {
    			// Aligned
    			canCharge = true;
    			GridPositionComponent targetPos = Mappers.gridPositionComponent.get(chargeTarget);
    			chargeDistance = TileUtil.getDistanceBetweenTiles(targetPos.coord(), enemyCurrentPos.coord());
        		room.setCreatureState(RoomCreatureState.ATTACK);
        		return true;
    		} else {
	    		return false;
    		}
    		
    	case ATTACK:
    		
    		if (canCharge) {
    			attackState = StingerAttackStates.INIT_CHARGE;
    			canCharge = false;
    		}
    		
    		if (attackState == StingerAttackStates.NONE) {
    			return false;
    		}
    		
			GridPositionComponent targetPos = Mappers.gridPositionComponent.get(chargeTarget);

    		switch(attackState) {
    		case INIT_CHARGE:
    			PoolableVector2 chargeLocation = PoolableVector2.create(0, 0);
    			if (enemyCurrentPos.coord().x == targetPos.coord().x) {
    				// Vertical charge
    				if (enemyCurrentPos.coord().y < targetPos.coord().y) {
    					chargeLocation.x = targetPos.coord().x;
    					chargeLocation.y = targetPos.coord().y - 1;
    					
    					int i = (int) enemyCurrentPos.coord().y;
    					while (i < targetPos.coord().y - 1) {
    						Entity wp = room.entityFactory.createWaypoint(new Vector2(chargeLocation.x, i), room);
    						moveCompo.getWayPoints().add(wp);
    						i++;
    					}
    				} else {
    					chargeLocation.x = targetPos.coord().x;
    					chargeLocation.y = targetPos.coord().y + 1;
    					
    					int i = (int) enemyCurrentPos.coord().y;
    					while (i > targetPos.coord().y + 1) {
    						Entity wp = room.entityFactory.createWaypoint(new Vector2(chargeLocation.x, i), room);
    						moveCompo.getWayPoints().add(wp);
    						i--;
    					}
    				}
    			} else {
    				// Horizontal charge
    				if (enemyCurrentPos.coord().x < targetPos.coord().x) {
    					// To the right
    					SpriteComponent spriteComponent = Mappers.spriteComponent.get(enemy);
    					spriteComponent.flipX = false;

    					chargeLocation.x = targetPos.coord().x - 1;
    					chargeLocation.y = targetPos.coord().y;
    					
    					int i = (int) enemyCurrentPos.coord().x;
    					while (i < targetPos.coord().x - 1) {
    						Entity wp = room.entityFactory.createWaypoint(new Vector2(i, chargeLocation.y), room);
    						moveCompo.getWayPoints().add(wp);
    						i++;
    					}
    				} else {
    					// To the left
    					SpriteComponent spriteComponent = Mappers.spriteComponent.get(enemy);
    					spriteComponent.flipX = true;

    					chargeLocation.x = targetPos.coord().x + 1;
    					chargeLocation.y = targetPos.coord().y;
    					
    					int i = (int) enemyCurrentPos.coord().x;
    					while (i > targetPos.coord().x + 1) {
    						Entity wp = room.entityFactory.createWaypoint(new Vector2(i, chargeLocation.y), room);
    						moveCompo.getWayPoints().add(wp);
    						i--;
    					}
    				}
    			}
    			
    			moveCompo.setSelectedTile(chargeLocation, room);
    			moveCompo.hideMovementEntities();
    			
    			// Stinger animation
    			StateComponent stateComponent = Mappers.stateComponent.get(enemy);
    			stateComponent.set(StatesEnum.STINGER_ATTACK, true);
    			
    			creatureSystem.getMovementHandler().initiateMovement(enemy);
    			attackState = StingerAttackStates.CHARGE;
    			
    			break;
    		case CHARGE:
    	    	moveCompo.selectCurrentMoveDestinationTile(enemy);
	    		
    	    	//Do the movement on screen
    	    	MovementProgressEnum movementFinished = creatureSystem.getMovementHandler().performRealMovement(enemy, room, 15);
        		if (movementFinished == MovementProgressEnum.MOVEMENT_OVER) attackState = StingerAttackStates.END_CHARGE;

    			break;
    		case END_CHARGE:
    			MovementHandler.finishRealMovement(enemy, room);
    			attackState = StingerAttackStates.NONE;
    			
    			// Stinger animation
    			stateComponent = Mappers.stateComponent.get(enemy);
    			stateComponent.set(StatesEnum.FLY_STANDING);
    			
				attackCompo.setTarget(chargeTarget);
				attackCompo.setTargetedTile(TileUtil.getTileAtGridPos(targetPos.coord(), room));
				
				attackCompo.setAdditionnalStrength(chargeDistance);
				room.setCreatureState(RoomCreatureState.ATTACK_ANIMATION);
    			
    			default:
    		}
    		return true;
    		
    		
    	default:
    	}
		
		return false;
	}	
	
	
	
	
	@Override
	public boolean computeMovableTilesToDisplayToPlayer(CreatureSystem system, Entity enemyEntity, Room room) {
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
	
	
	private Entity canChargePlayer(Entity enemyEntity, MoveComponent moveCompo, AttackComponent attackCompo, Room room) {
		if (moveCompo.getMoveSpeed() == 0) return null;
		
		// Add the horizontal and vertical lines
		PoolableVector2 temp = PoolableVector2.create(0, 0);
		GridPositionComponent enemyPos = Mappers.gridPositionComponent.get(enemyEntity);
		int i = (int) enemyPos.coord().x - 1;
		while (i >= 0) {
			// left
			temp.set(i, enemyPos.coord().y);
			Entity target = checkTileForAlly(temp, moveCompo, attackCompo, room);
			if (target != null) {
				return target;
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
			Entity target = checkTileForAlly(temp, moveCompo, attackCompo, room);
			if (target != null) {
				return target;
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
			Entity target = checkTileForAlly(temp, moveCompo, attackCompo, room);
			if (target != null) {
				return target;
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
			Entity target = checkTileForAlly(temp, moveCompo, attackCompo, room);
			if (target != null) {
				return target;
			}
			if (checkTileForSolid(temp, moveCompo, attackCompo, room)) {
				break;
			}
			i++;
		}
		temp.free();
		
		return null;
	}
	
	private boolean checkTileForSolid(PoolableVector2 position, MoveComponent moveCompo, AttackComponent attackCompo, Room room) {
		Entity solid = TileUtil.getEntityWithComponentOnTile(position, SolidComponent.class, room);
		if (solid != null) return true;
		Entity blockVision = TileUtil.getEntityWithComponentOnTile(position, BlockVisibilityComponent.class, room);
		return blockVision != null;
	}
	private Entity checkTileForAlly(PoolableVector2 position, MoveComponent moveCompo, AttackComponent attackCompo, Room room) {
		Entity ally = TileUtil.getEntityWithComponentOnTile(position, AllyComponent.class, room);
		return ally;
	}


	
}
