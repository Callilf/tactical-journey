package com.dokkaebistudio.tacticaljourney.systems.creatures.subsystems;

import java.util.Iterator;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.movements.TileSearchService;
import com.dokkaebistudio.tacticaljourney.ai.pathfinding.RoomGraph;
import com.dokkaebistudio.tacticaljourney.ai.pathfinding.RoomHeuristic;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.components.attack.AttackSkill;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.creature.enemies.EnemyOrangutan;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomCreatureState;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.systems.creatures.CreatureSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class OrangutanSubSystem extends CreatureSubSystem {
	
	public static final Vector2 LEFT_CLONE_TILE = new Vector2(3, 6);
	public static final Vector2 RIGHT_CLONE_TILE = new Vector2(19, 6);
	public static final Vector2 UP_CLONE_TILE = new Vector2(11, 10);
	public static final Vector2 DOWN_CLONE_TILE = new Vector2(11, 2);
	
	private EnemyOrangutan enemyOrangutan;
	
	private Entity banana;
	private Vector2 bananaTile = null;
	private Tile bananaNextTile = null;
	private boolean bananaTileReached = false;


	
	private AttackSkill meleeSkill;
	private AttackSkill rangeSkill;
	private AttackSkill throwSkill;
	
	@Override
	public boolean update(final CreatureSystem creatureSystem, final Entity enemy, final Room room) {
		if (enemyOrangutan == null) {
			enemyOrangutan = (EnemyOrangutan) Mappers.aiComponent.get(enemy).getType();
			for (AttackSkill as : Mappers.attackComponent.get(enemy).getSkills()) {
				switch(as.getAttackType()) {
				case MELEE:
					meleeSkill = as;
					break;
				case RANGE:
					rangeSkill = as;
					break;
				case THROW:
					throwSkill = as;
					break;
					default:
				}
			}
		}
		final Vector2 playerPos = Mappers.gridPositionComponent.get(GameScreen.player).coord();
		final Vector2 orangutanPos = Mappers.gridPositionComponent.get(enemy).coord();
		
		switch(room.getCreatureState()) {
		
		case TURN_INIT:
			
			if (enemyOrangutan.isGoingForBanana()) {
				int shortestDistance = 1000;
				for (Entity b : enemyOrangutan.getBananas()) {
					GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(b);
					int dist = TileUtil.getDistanceBetweenTiles(orangutanPos, gridPositionComponent.coord());
					if (shortestDistance > dist) {
						
						// Compute a path to the banana
						Tile startTile = room.getTileAtGridPosition(orangutanPos);
						RoomGraph roomGraph = new RoomGraph(enemy, TileUtil.getAllTiles(room));
						IndexedAStarPathFinder<Tile> indexedAStarPathFinder = new IndexedAStarPathFinder<Tile>(roomGraph);
						GraphPath<Tile> path = new DefaultGraphPath<Tile>();
						indexedAStarPathFinder.searchNodePath(startTile, TileUtil.getTileAtGridPos(gridPositionComponent.coord(), room), new RoomHeuristic(), path);
						if (path.getCount() == 0) continue;
						
						shortestDistance = dist;
						banana = b;
						bananaTile = gridPositionComponent.coord();
					}
				}
				
				enemyOrangutan.setGoingForBanana(false);
			}
			
			if (bananaTile != null) {
				
				Tile startTile = room.getTileAtGridPosition(orangutanPos);
				RoomGraph roomGraph = new RoomGraph(enemy, TileUtil.getAllTiles(room));
				IndexedAStarPathFinder<Tile> indexedAStarPathFinder = new IndexedAStarPathFinder<Tile>(roomGraph);
				GraphPath<Tile> path = new DefaultGraphPath<Tile>();
				indexedAStarPathFinder.searchNodePath(startTile, TileUtil.getTileAtGridPos(bananaTile, room), new RoomHeuristic(), path);

				if (path.getCount() == 0) {
					bananaTile = null;
				} else {
					MoveComponent moveComponent = Mappers.moveComponent.get(enemy);
					int totalCost = 0;
					Iterator<Tile> iterator = path.iterator();
					while(iterator.hasNext()) {
						Tile next = iterator.next();
						if (next.getGridPos() == orangutanPos) continue;
						
						totalCost += TileUtil.getCostOfMovementForTilePos(next.getGridPos(), enemy, room);
						
						if (totalCost <= moveComponent.getMoveSpeed()) {
							bananaNextTile = next;
						}
						if (totalCost > moveComponent.getMoveSpeed()) break;
					}
				}
				
			}
			
			
			break;

    	case MOVE_TILES_DISPLAYED:

    		if (enemyOrangutan.isSleeping()) {
        		for(Tile t : Mappers.attackComponent.get(enemy).allAttackableTiles) {
        			if (t.getGridPos().equals(playerPos)) {
        				// Sees the player
        				enemyOrangutan.setSleeping(false);
        				break;
        			}
        		}
    		}
    			
    		if (enemyOrangutan.isSleeping()) {
    			room.setCreatureState(RoomCreatureState.END_MOVEMENT);
    			return true;
    		}
    		
    		if (bananaNextTile != null) {
    			MoveComponent moveComponent = Mappers.moveComponent.get(enemy);
    			for (Entity tile : moveComponent.movableTiles) {
    				if (Mappers.gridPositionComponent.get(tile).coord().equals(bananaNextTile.getGridPos())) {
	            		GridPositionComponent destinationPos = Mappers.gridPositionComponent.get(tile);
	    		    	//Clicked on this tile !!
	    				//Create an entity to show that this tile is selected as the destination
	    				moveComponent.setSelectedTile(destinationPos.coord(), room);
	    					
	    				//Display the way to go to this point
	    				List<Entity> waypoints = TileSearchService.buildWaypointList(enemy, moveComponent, 
	    						Mappers.gridPositionComponent.get(enemy).coord(), 
	    						destinationPos.coord(), room, true);
	    				moveComponent.setWayPoints(waypoints);
	    				moveComponent.hideMovementEntities();
	            		room.setCreatureState(RoomCreatureState.MOVE_DESTINATION_SELECTED);

    				}
    			}
    			return true;
    		}
    		
    		
    		break;
    		
    	case END_MOVEMENT:
    		if (bananaNextTile != null && bananaNextTile.getGridPos().equals(bananaTile)) {
    			bananaNextTile = null;
    			bananaTile = null;
    			bananaTileReached = true;
    			
    			// eat banana
    			HealthComponent healthComponent = Mappers.healthComponent.get(enemy);
    			healthComponent.restoreHealth(10);
    			
    			enemyOrangutan.getBananas().remove(banana);
    			room.removeEntity(banana);
    			creatureSystem.finishOneCreatureTurn(enemy, Mappers.attackComponent.get(enemy), Mappers.aiComponent.get(enemy));
    			return true;
    		}
    		break;

    	case ATTACK:
    		
    		break;
    		
    	case ATTACK_FINISH:
    		if (!enemyOrangutan.isSleeping()) {
    			Mappers.stateComponent.get(enemy).set(StatesEnum.STANDING);
    		}
			break;
			
    	default:
    	}
		
		return false;
	}

	

}
