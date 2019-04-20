package com.dokkaebistudio.tacticaljourney.systems.enemies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.pathfinding.RoomGraph;
import com.dokkaebistudio.tacticaljourney.ai.pathfinding.RoomHeuristic;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.MoveComponent;
import com.dokkaebistudio.tacticaljourney.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.enemies.EnemyShinobi;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.items.enums.ItemEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.systems.EnemySystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.dokkaebistudio.tacticaljourney.vfx.AttackAnimation;

public class ShinobiSubSystem extends EnemySubSystem {
	
	private boolean isSleeping = true;
	private EnemyShinobi enemyShinobi;
	private boolean firstTimeAtLessThan10HP;
	
	private AttackComponent throwAttackCompo;
	private boolean throwSmokeBomb;
	private Vector2 fleeTile = null;
	private Tile fleeNextTile;
	private boolean fleeTileReached = false;
	
	@Override
	public boolean update(final EnemySystem enemySystem, final Entity enemy, final Room room) {
		if (enemyShinobi == null) {
			enemyShinobi = (EnemyShinobi) Mappers.enemyComponent.get(enemy).getType();
		}
		final Vector2 playerPos = Mappers.gridPositionComponent.get(GameScreen.player).coord();
		
		switch(room.getState()) {
		
		case ENEMY_TURN_INIT:
			if (isSleeping) {
				Mappers.stateComponent.get(enemy).set(StatesEnum.SHINOBI_SLEEPING);
			}
			
			if (!firstTimeAtLessThan10HP && Mappers.healthComponent.get(enemy).getHp() <= 10) {
				firstTimeAtLessThan10HP = true;
				throwSmokeBomb = true;
			}
			
			if (fleeTile != null) {
				
				Tile startTile = room.getTileAtGridPosition(Mappers.gridPositionComponent.get(enemy).coord());
				RoomGraph roomGraph = new RoomGraph(enemy, TileUtil.getAllTiles(room));
				IndexedAStarPathFinder<Tile> indexedAStarPathFinder = new IndexedAStarPathFinder<Tile>(roomGraph);
				GraphPath<Tile> path = new DefaultGraphPath<Tile>();
				indexedAStarPathFinder.searchNodePath(startTile, TileUtil.getTileAtGridPos(fleeTile, room), new RoomHeuristic(), path);

				if (path.getCount() == 0) {
					fleeTile = null;
				} else {
					MoveComponent moveComponent = Mappers.moveComponent.get(enemy);
					int totalCost = 0;
					Iterator<Tile> iterator = path.iterator();
					while(iterator.hasNext()) {
						Tile next = iterator.next();
						totalCost += TileUtil.getCostOfMovementForTilePos(next.getGridPos(), enemy, room);
						
						if (totalCost <= moveComponent.getMoveSpeed()) {
							fleeNextTile = next;
						}
						if (totalCost > moveComponent.getMoveSpeed()) break;
					}
				}
				
				
			}
			
			break;

    	case ENEMY_MOVE_TILES_DISPLAYED:

    		if (isSleeping) {
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
    		
    		if (!enemyShinobi.isSmokeBombUsed() &&
    				TileUtil.getDistanceBetweenTiles(playerPos, Mappers.gridPositionComponent.get(enemy).coord()) == 1) {
    			// Close range and still has smoke bomb
    			useSmokeBomb(enemy, enemySystem, room);
    		}
    		
    		if (fleeNextTile != null) {
    			MoveComponent moveComponent = Mappers.moveComponent.get(enemy);
    			for (Entity tile : moveComponent.movableTiles) {
    				if (Mappers.gridPositionComponent.get(tile).coord().equals(fleeNextTile.getGridPos())) {
	            		GridPositionComponent destinationPos = Mappers.gridPositionComponent.get(tile);
	    		    	//Clicked on this tile !!
	    				//Create an entity to show that this tile is selected as the destination
	    				Entity destinationTileEntity = room.entityFactory.createDestinationTile(destinationPos.coord(), room);
	    				moveComponent.setSelectedTile(destinationTileEntity);
	    					
	    				//Display the way to go to this point
	    				List<Entity> waypoints = enemySystem.getTileSearchService().buildWaypointList(enemy, moveComponent, Mappers.gridPositionComponent.get(enemy), 
	    						destinationPos, room);
	    				moveComponent.setWayPoints(waypoints);
	    				moveComponent.hideMovementEntities();
	            		room.setNextState(RoomState.ENEMY_MOVE_DESTINATION_SELECTED);
	            		
	            		if (fleeNextTile.getGridPos().equals(fleeTile)) {
	            			fleeNextTile = null;
	            			fleeTile = null;
	            			fleeTileReached = true;
	            			
	            			Mappers.stateComponent.get(enemy).set(StatesEnum.SHINOBI_CLONING);
	            			enemySystem.finishOneEnemyTurn(enemy, Mappers.attackComponent.get(enemy), Mappers.enemyComponent.get(enemy));

	            			break;
	            		}
    				}
    			}
    			return true;
    		}
    		
    		if (fleeTileReached) {
    			Mappers.stateComponent.get(enemy).set(StatesEnum.SHINOBI_CLONING);
    			enemySystem.finishOneEnemyTurn(enemy, Mappers.attackComponent.get(enemy), Mappers.enemyComponent.get(enemy));
    		}
    		
    		break;
    		
    	case ENEMY_ATTACK:
    		if (!isSleeping) {
    			Mappers.stateComponent.get(enemy).set(StatesEnum.SHINOBI_THROWING);
    		}
    		
    		if (throwSmokeBomb) {
    			throwSmokeBomb(playerPos, enemy, room);
    			return true;
    		}
    		
    		
    		break;
    		
    	case ENEMY_ATTACK_FINISH:
    		if (!isSleeping) {
    			Mappers.stateComponent.get(enemy).set(StatesEnum.STANDING);
    		}
			break;
			
    	default:
    	}
		
		return false;
	}


	
	// Utils


	private void useSmokeBomb(final Entity enemy, final EnemySystem enemySystem, final Room room) {
		enemyShinobi.setSmokeBombUsed(true);
		
		Entity smokeBomb = room.entityFactory.itemFactory.createItem(ItemEnum.SMOKE_BOMB);
		ItemComponent itemComponent = Mappers.itemComponent.get(smokeBomb);
		itemComponent.use(enemy, smokeBomb, room);
		room.removeEntity(smokeBomb);
		Journal.addEntry(Mappers.inspectableComponentMapper.get(enemy).getTitle() + " used a smoke bomb.");
		
		enemySystem.finishOneEnemyTurn(enemy, Mappers.attackComponent.get(enemy), Mappers.enemyComponent.get(enemy));
	}




	private void throwSmokeBomb(final Vector2 thrownPosition, final Entity thrower, final Room room) {
		if (throwAttackCompo == null) {
			throwAttackCompo = room.engine.createComponent(AttackComponent.class);
			throwAttackCompo.setAttackAnimation(new AttackAnimation(null, null, false));
			
			GridPositionComponent enemyPos = Mappers.gridPositionComponent.get(thrower);
			Tile playerTile = TileUtil.getTileAtGridPos(thrownPosition, room);
			
			final Entity smokeBomb = room.entityFactory.itemFactory.createItem(ItemEnum.SMOKE_BOMB);
			throwAttackCompo.getAttackAnimation().setAttackAnim(Assets.smoke_bomb_item.getRegion());
			
			Action finishThrowAction = new Action(){
			  @Override
			  public boolean act(float delta){
				throwSmokeBomb = false;
				
				throwAttackCompo.clearAttackImage();
				fleeTile = new Vector2(3,6);
				
				ItemComponent itemComponent = Mappers.itemComponent.get(smokeBomb);
				itemComponent.onThrow(thrownPosition, thrower, smokeBomb, room);
				room.removeEntity(smokeBomb);
				Journal.addEntry(Mappers.inspectableComponentMapper.get(thrower).getTitle() + " threw a smoke bomb.");
				
				room.setNextState(RoomState.ENEMY_ATTACK_FINISH);
				return true;
			  }
			};
			
			throwAttackCompo.setAttackImage(enemyPos.coord(), 
					playerTile, 
					null,
					GameScreen.fxStage,
					finishThrowAction);
			
		}
	}	

}
