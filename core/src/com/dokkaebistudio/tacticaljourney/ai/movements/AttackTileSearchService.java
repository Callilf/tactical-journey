package com.dokkaebistudio.tacticaljourney.ai.movements;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.enums.DirectionEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class AttackTileSearchService extends TileSearchService {
	

	public AttackTileSearchService() {}
    

	/**
	 * Compute the tiles where attack is possible.
	 * @param attackerEntity the attacker
	 * @param room the current room
	 * @param onlyAttackableEntities whether we should check for each tile that there is something to attack or not
	 * This boolean is used to know whether we are computing attackable tiles for display to the player or during the enemy turn.
	 */
	public void buildAttackTilesSet(Entity attackerEntity, Room room, boolean onlyAttackableEntities, boolean ignoreObstacles) {
		visitedTilesWithRemainingMove.clear();
		attackableTilesPerDistance.clear();
		obstacles.clear();
		
		currentEntity = attackerEntity;
		
		 MoveComponent moveCompo = Mappers.moveComponent.get(attackerEntity);
		 AttackComponent attackCompo = Mappers.attackComponent.get(attackerEntity);
		 GridPositionComponent attackerPosCompo = Mappers.gridPositionComponent.get(attackerEntity);
		 
		 if (!attackCompo.isActive()) return;

		//Search all attackable tiles for each movable tile
		Set<Tile> attackableTiles = new HashSet<>();
		
		if (attackCompo.getAttackType() == AttackTypeEnum.THROW) {
			Tile tileAtGridPos = TileUtil.getTileAtGridPos(attackerPosCompo.coord(), room);
			attackableTiles.add(tileAtGridPos);
		}
		
		Set<Tile> moveTiles = moveCompo.allWalkableTiles;
		if (moveTiles.isEmpty()) {
			moveTiles.add(TileUtil.getTileAtGridPos(attackerPosCompo.coord(), room));
		}
		for (Tile t : moveTiles) {			
			CheckTypeEnum checkType = onlyAttackableEntities ? CheckTypeEnum.ATTACK : CheckTypeEnum.ATTACK_FOR_DISPLAY;
			
			visitedTilesWithRemainingMove.put(t, 0);
			Set<Tile> foundAttTiles = check4ContiguousTiles(attackCompo.getAttackType(), checkType, (int)t.getGridPos().x, (int)t.getGridPos().y, moveCompo.allWalkableTiles, room, attackCompo.getRangeMax(), 1);
			attackableTiles.addAll(foundAttTiles);
		}

		if (!ignoreObstacles) {
			//Obstacles post process
			obstaclesPostProcess(attackerPosCompo, attackableTiles);
		}
		
		
		//Range Postprocess : remove tiles that cannot be attacked
		if (attackCompo.getRangeMin() > 1) {
			Iterator<Tile> it = attackableTiles.iterator();
			while (it.hasNext()) {
				Tile currentAttackableTile = it.next();
				//Remove tiles that are too close
				if (TileUtil.getDistanceBetweenTiles(attackerPosCompo.coord(), currentAttackableTile.getGridPos()) < attackCompo.getRangeMin()) {
					it.remove();
				}
			}
		}

		attackCompo.allAttackableTiles = attackableTiles;

		
		//Create entities for each attackable tiles to display them
		for (Tile tileCoord : attackCompo.allAttackableTiles) {
			Entity attackableTileEntity = room.entityFactory.createAttackableTile(tileCoord.getGridPos(), room);
			attackCompo.attackableTiles.add(attackableTileEntity);
		}
	}

	
	
	
	//*************************************
	// Obstacles management

	private void obstaclesPostProcess(GridPositionComponent attackerPosCompo, Set<Tile> attackableTiles) {
		Iterator<Vector2> obstaclesIt = obstacles.iterator();
		while (obstaclesIt.hasNext()) {
			Vector2 obstacle = obstaclesIt.next();
			
			float xDist = obstacle.x - attackerPosCompo.coord().x;
			float yDist = obstacle.y - attackerPosCompo.coord().y;
			
			if (xDist > 0 && yDist == 0) {
				//obstacle on the right (y==0)
				
				filterHiddenTiles(DirectionEnum.RIGHT, xDist, yDist,attackerPosCompo, attackableTiles, obstacle);

			} else if (xDist == 0 && yDist > 0) {
				//obstacle up (x==0)
				
				filterHiddenTiles(DirectionEnum.UP, xDist, yDist,attackerPosCompo, attackableTiles, obstacle);


			} else if (xDist < 0 && yDist == 0) {
				//obstacle on the left (y==0)
				
				filterHiddenTiles(DirectionEnum.LEFT, xDist, yDist,attackerPosCompo, attackableTiles, obstacle);


			} else if (xDist == 0 && yDist < 0) {
				//obstacle down (x==0)
				
				filterHiddenTiles(DirectionEnum.DOWN, xDist, yDist,attackerPosCompo, attackableTiles, obstacle);


			} else if (xDist > 0 && yDist > 0) {
				//obstacle on the upper right
				
				filterHiddenTiles(DirectionEnum.UP_RIGHT, xDist, yDist,attackerPosCompo, attackableTiles, obstacle);

			} else if (xDist < 0 && yDist > 0) {
				//obstacle on the upper right
				
				filterHiddenTiles(DirectionEnum.UP_LEFT, xDist, yDist,attackerPosCompo, attackableTiles, obstacle);

			} else if (xDist > 0 && yDist < 0) {
				//obstacle on the upper right
				
				filterHiddenTiles(DirectionEnum.DOWN_RIGHT, xDist, yDist,attackerPosCompo, attackableTiles, obstacle);

			} else if (xDist < 0 && yDist < 0) {
				//obstacle on the upper right
				
				filterHiddenTiles(DirectionEnum.DOWN_LEFT, xDist, yDist,attackerPosCompo, attackableTiles, obstacle);

			}
			
		}
	}


	private void filterHiddenTiles(DirectionEnum direction, float xDist, float yDist, GridPositionComponent attackerPosCompo, Set<Tile> attackableTiles, Vector2 obstacle) {
		Iterator<Tile> it = attackableTiles.iterator();
		while (it.hasNext()) {
			Tile next = it.next();
			Vector2 currentTilePos = next.getGridPos();
			float currxDist = direction == DirectionEnum.UP || direction == DirectionEnum.DOWN ? Math.abs(currentTilePos.x - attackerPosCompo.coord().x) : currentTilePos.x - attackerPosCompo.coord().x;
			float curryDist = direction == DirectionEnum.RIGHT || direction == DirectionEnum.LEFT ? Math.abs(currentTilePos.y - attackerPosCompo.coord().y) : currentTilePos.y - attackerPosCompo.coord().y;
			float obstxDist = Math.abs(currentTilePos.x - obstacle.x); 
			float obstyDist = Math.abs(currentTilePos.y - obstacle.y); 

			switch(direction) {
			case RIGHT:
				if (currxDist > xDist && (curryDist == 0 || curryDist <= obstxDist - ((xDist)) || (xDist == 1 && obstxDist == 1 && obstyDist ==1)) ) {
					it.remove();
				}
				break;
				
			case UP:
				if (curryDist > yDist && (currxDist == 0 || currxDist <= obstyDist - ((yDist)) || (yDist == 1 && obstyDist == 1 && obstxDist == 1)) ) {
					it.remove();
				}
				break;
				
			case LEFT:
				if (currxDist < xDist && (curryDist == 0 || curryDist <= obstxDist - ((Math.abs(xDist))) || (xDist == -1 && obstxDist == 1 && obstyDist == 1)) ) {
					it.remove();
				}
				break;
				
			case DOWN:
				if (curryDist < yDist && (currxDist == 0 || currxDist <= obstyDist - ((Math.abs(yDist))) || (yDist == -1 && obstyDist == 1 && obstxDist == 1)) ) {
					it.remove();
				}
				break;
				
			case UP_RIGHT:
				if (xDist > yDist) {
					if (currxDist > xDist && curryDist >= yDist && obstyDist <= obstxDist && (obstyDist >= (obstxDist/xDist) - 1)) {
						it.remove();
					}
					
				} else if (yDist > xDist) {
					if (currxDist >= xDist && curryDist > yDist && obstxDist <= obstyDist && (obstxDist >= (obstyDist/yDist) - 1)) {
						it.remove();
					}
					
				} else {
					if (currxDist > xDist && curryDist > yDist && (obstxDist >= (obstyDist/yDist) - 1) && (obstyDist >= (obstxDist/xDist) - 1)) {
						it.remove();
					}
				}
				break;
				
			case UP_LEFT:
				if (Math.abs(xDist) > yDist) {
					if (currxDist < xDist && curryDist >= yDist && obstyDist <= obstxDist && (obstyDist >= (obstxDist/Math.abs(xDist)) - 1)) {
						it.remove();
					}
					
				} else if (yDist > Math.abs(xDist)) {
					if (currxDist <= xDist && curryDist > yDist && obstxDist <= obstyDist && (obstxDist >= (obstyDist/yDist) - 1)) {
						it.remove();
					}
					
				} else {
					if (currxDist < xDist && curryDist > yDist && (obstxDist >= (obstyDist/yDist) - 1) && (obstyDist >= (obstxDist/Math.abs(xDist)) - 1)) {
						it.remove();
					}
				}
				break;
				
			case DOWN_RIGHT:
				if (xDist > Math.abs(yDist)) {
					if (currxDist > xDist && curryDist <= yDist && obstyDist <= obstxDist && (obstyDist >= (obstxDist/xDist) - 1)) {
						it.remove();
					}
					
				} else if (Math.abs(yDist) > xDist) {
					if (currxDist >= xDist && curryDist < yDist && obstxDist <= obstyDist && (obstxDist >= (obstyDist/Math.abs(yDist)) - 1)) {
						it.remove();
					}
					
				} else {
					if (currxDist > xDist && curryDist < yDist && (obstxDist >= (obstyDist/Math.abs(yDist)) - 1) && (obstyDist >= (obstxDist/xDist) - 1)) {
						it.remove();
					}
				}
				break;
				
			case DOWN_LEFT:
				if (Math.abs(xDist) > Math.abs(yDist)) {
					if (currxDist < xDist && curryDist <= yDist && obstyDist <= obstxDist && (obstyDist >= (obstxDist/Math.abs(xDist)) - 1)) {
						it.remove();
					}
					
				} else if (Math.abs(yDist) > Math.abs(xDist)) {
					if (currxDist <= xDist && curryDist < yDist && obstxDist <= obstyDist && (obstxDist >= (obstyDist/Math.abs(yDist)) - 1)) {
						it.remove();
					}
					
				} else {
					if (currxDist < xDist && curryDist < yDist && (obstxDist >= (obstyDist/Math.abs(yDist)) - 1) && (obstyDist >= (obstxDist/Math.abs(xDist)) - 1)) {
						it.remove();
					}
				}
				break;
				
				default:
			}
			
		}
	}
	
	
}
