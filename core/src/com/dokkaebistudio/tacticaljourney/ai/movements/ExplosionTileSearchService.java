package com.dokkaebistudio.tacticaljourney.ai.movements;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.ces.components.ExplosiveComponent;
import com.dokkaebistudio.tacticaljourney.enums.DirectionEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class ExplosionTileSearchService extends TileSearchService {
	

	public ExplosionTileSearchService() {}
    

	/**
	 * Compute the tiles affected by the explosion.
	 * @param explosive the explosive entity
	 * @param room the current room
	 */
	public void buildExplosionTilesSet(Entity explosive, Room room) {
		visitedTilesWithRemainingMove.clear();
		attackableTilesPerDistance.clear();
		obstacles.clear();
		currentEntity = explosive;
		
		ExplosiveComponent explosiveComponent = Mappers.explosiveComponent.get(explosive);
		
		//Search all attackable tiles for each movable tile
		Set<Tile> attackableTiles = new HashSet<>();

		Tile tile = TileUtil.getTileFromEntity(explosive, room);
		attackableTiles.add(tile);
			
		CheckTypeEnum checkType = CheckTypeEnum.ATTACK_FOR_DISPLAY;
		visitedTilesWithRemainingMove.put(tile, 0);
		Set<Tile> foundAttTiles = check4ContiguousTiles(explosive, AttackTypeEnum.EXPLOSION, checkType, (int)tile.getGridPos().x, (int)tile.getGridPos().y, null, room, explosiveComponent.getRadius(), 1);
		attackableTiles.addAll(foundAttTiles);

		//Obstacles post process
		obstaclesPostProcess(tile.getGridPos(), attackableTiles);

		explosiveComponent.allAttackableTiles = attackableTiles;

		//Create entities for each attackable tiles to display them
		for (Tile tileCoord : explosiveComponent.allAttackableTiles) {
			Entity attackableTileEntity = room.entityFactory.createAttackableTile(tileCoord.getGridPos(), room, true);
			explosiveComponent.attackableTiles.add(attackableTileEntity);
		}
	}

	
	
	
	//*************************************
	// Obstacles management

	private void obstaclesPostProcess(Vector2 attackerPos, Set<Tile> attackableTiles) {
		Iterator<Vector2> obstaclesIt = obstacles.iterator();
		while (obstaclesIt.hasNext()) {
			Vector2 obstacle = obstaclesIt.next();
			
			float xDist = obstacle.x - attackerPos.x;
			float yDist = obstacle.y - attackerPos.y;
			
			if (xDist > 0 && yDist == 0) {
				//obstacle on the right (y==0)
				
				filterHiddenTiles(DirectionEnum.RIGHT, xDist, yDist, attackerPos, attackableTiles, obstacle);

			} else if (xDist == 0 && yDist > 0) {
				//obstacle up (x==0)
				
				filterHiddenTiles(DirectionEnum.UP, xDist, yDist, attackerPos, attackableTiles, obstacle);


			} else if (xDist < 0 && yDist == 0) {
				//obstacle on the left (y==0)
				
				filterHiddenTiles(DirectionEnum.LEFT, xDist, yDist, attackerPos, attackableTiles, obstacle);


			} else if (xDist == 0 && yDist < 0) {
				//obstacle down (x==0)
				
				filterHiddenTiles(DirectionEnum.DOWN, xDist, yDist, attackerPos, attackableTiles, obstacle);


			} else if (xDist > 0 && yDist > 0) {
				//obstacle on the upper right
				
				filterHiddenTiles(DirectionEnum.UP_RIGHT, xDist, yDist, attackerPos, attackableTiles, obstacle);

			} else if (xDist < 0 && yDist > 0) {
				//obstacle on the upper right
				
				filterHiddenTiles(DirectionEnum.UP_LEFT, xDist, yDist, attackerPos, attackableTiles, obstacle);

			} else if (xDist > 0 && yDist < 0) {
				//obstacle on the upper right
				
				filterHiddenTiles(DirectionEnum.DOWN_RIGHT, xDist, yDist, attackerPos, attackableTiles, obstacle);

			} else if (xDist < 0 && yDist < 0) {
				//obstacle on the upper right
				
				filterHiddenTiles(DirectionEnum.DOWN_LEFT, xDist, yDist, attackerPos, attackableTiles, obstacle);

			}
			
		}
	}


	private void filterHiddenTiles(DirectionEnum direction, float xDist, float yDist, Vector2 attackerPos, Set<Tile> attackableTiles, Vector2 obstacle) {
		Iterator<Tile> it = attackableTiles.iterator();
		while (it.hasNext()) {
			Tile next = it.next();
			Vector2 currentTilePos = next.getGridPos();
			float currxDist = direction == DirectionEnum.UP || direction == DirectionEnum.DOWN ? Math.abs(currentTilePos.x - attackerPos.x) : currentTilePos.x - attackerPos.x;
			float curryDist = direction == DirectionEnum.RIGHT || direction == DirectionEnum.LEFT ? Math.abs(currentTilePos.y - attackerPos.y) : currentTilePos.y - attackerPos.y;
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
