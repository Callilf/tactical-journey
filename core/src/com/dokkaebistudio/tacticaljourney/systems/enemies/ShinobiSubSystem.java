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
import com.dokkaebistudio.tacticaljourney.alterations.blessings.shinobi.BlessingKawarimi;
import com.dokkaebistudio.tacticaljourney.components.AttackComponent;
import com.dokkaebistudio.tacticaljourney.components.HealthComponent;
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
	
	private static final Vector2 LEFT_CLONE_TILE = new Vector2(3, 6);
	private static final Vector2 RIGHT_CLONE_TILE = new Vector2(19, 6);
	private static final Vector2 UP_CLONE_TILE = new Vector2(11, 10);
	private static final Vector2 DOWN_CLONE_TILE = new Vector2(11, 2);
	
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
			if (enemyShinobi.isSleeping()) {
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

    		if (enemyShinobi.isSleeping()) {
        		for(Tile t : Mappers.attackComponent.get(enemy).allAttackableTiles) {
        			if (t.getGridPos().equals(playerPos)) {
        				// Sees the player
        				enemyShinobi.setSleeping(false);
        				break;
        			}
        		}
    		}
    			
    		if (enemyShinobi.isSleeping()) {
    			room.setNextState(RoomState.ENEMY_END_MOVEMENT);
    			return true;
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

    				}
    			}
    			return true;
    		}
    		
    		if (fleeTileReached) {
    			fleeTileReached = false;
    			
    			// Clone
    			List<Entity> clones = new ArrayList<>();
    			BlessingKawarimi.createSmokeEffect(UP_CLONE_TILE);
    			clones.add(room.entityFactory.enemyFactory.createShinobi(room, UP_CLONE_TILE, true));
    			BlessingKawarimi.createSmokeEffect(DOWN_CLONE_TILE);
    			clones.add(room.entityFactory.enemyFactory.createShinobi(room, DOWN_CLONE_TILE, true));
    			if (LEFT_CLONE_TILE.equals(Mappers.gridPositionComponent.get(enemy).coord())) {
        			BlessingKawarimi.createSmokeEffect(RIGHT_CLONE_TILE);
        			clones.add(room.entityFactory.enemyFactory.createShinobi(room, RIGHT_CLONE_TILE, true));
    			} else {
        			BlessingKawarimi.createSmokeEffect(LEFT_CLONE_TILE);
        			clones.add(room.entityFactory.enemyFactory.createShinobi(room, LEFT_CLONE_TILE, true));
    			}
    			
    			// Clones have HP depending on the remaining hp of the shinobi
    			HealthComponent healthComponent = Mappers.healthComponent.get(enemy);
    			for (Entity e : clones) {
    				HealthComponent cloneHealthCompo = Mappers.healthComponent.get(e);
    				cloneHealthCompo.setMaxHp(healthComponent.getHp());
    				cloneHealthCompo.setHp(healthComponent.getHp());
    			}
    			
    			Mappers.stateComponent.get(enemy).set(StatesEnum.STANDING);
    			enemySystem.finishOneEnemyTurn(enemy, Mappers.attackComponent.get(enemy), Mappers.enemyComponent.get(enemy));
    			return true;
    		}
    		
    		break;
    		
    	case ENEMY_END_MOVEMENT:
    		
    		if (fleeNextTile != null && fleeNextTile.getGridPos().equals(fleeTile)) {
    			fleeNextTile = null;
    			fleeTile = null;
    			fleeTileReached = true;
    			
    			Mappers.stateComponent.get(enemy).set(StatesEnum.SHINOBI_CLONING);
    			enemySystem.finishOneEnemyTurn(enemy, Mappers.attackComponent.get(enemy), Mappers.enemyComponent.get(enemy));
    			return true;
    		}
    		break;
    		
    	case ENEMY_ATTACK:
    		if (!enemyShinobi.isSleeping()) {
    			Mappers.stateComponent.get(enemy).set(StatesEnum.SHINOBI_THROWING);
    		}
    		
    		if (enemyShinobi.getReceivedFirstDamage() != null && enemyShinobi.getReceivedFirstDamage()) {
    			throwFirstSmokeBomb(playerPos, enemy, room);
    			return true;
    		}
    		
    		if (throwSmokeBomb) {
    			throwSmokeBombAndFlee(playerPos, enemy, room);
    			return true;
    		}
    		
    		
    		break;
    		
    	case ENEMY_ATTACK_FINISH:
    		if (!enemyShinobi.isSleeping()) {
    			Mappers.stateComponent.get(enemy).set(StatesEnum.STANDING);
    		}
			break;
			
    	default:
    	}
		
		return false;
	}


	
	// Utils

	
	private void throwFirstSmokeBomb(final Vector2 thrownPosition, final Entity thrower, final Room room) {
		if (throwAttackCompo == null) {
			final GridPositionComponent enemyPos = Mappers.gridPositionComponent.get(thrower);
			if (TileUtil.getDistanceBetweenTiles(enemyPos.coord(), thrownPosition) > 5) {
				// Too far, cannot throw smoke bomb
				enemyShinobi.setReceivedFirstDamage(false);
				return;
			}
			
			throwAttackCompo = room.engine.createComponent(AttackComponent.class);
			throwAttackCompo.setAttackAnimation(new AttackAnimation(null, null, false));
			
			Tile playerTile = TileUtil.getTileAtGridPos(thrownPosition, room);
			
			final Entity smokeBomb = room.entityFactory.itemFactory.createItem(ItemEnum.SMOKE_BOMB);
			throwAttackCompo.getAttackAnimation().setAttackAnim(Assets.smoke_bomb_item.getRegion());
			
			Action finishThrowAction = new Action(){
			  @Override
			  public boolean act(float delta){
					enemyShinobi.setReceivedFirstDamage(false);

					throwAttackCompo.clearAttackImage();
					throwAttackCompo = null;

					ItemComponent itemComponent = Mappers.itemComponent.get(smokeBomb);
					itemComponent.onThrow(thrownPosition, thrower, smokeBomb, room);
					room.removeEntity(smokeBomb);
					Journal.addEntry(
							Mappers.inspectableComponentMapper.get(thrower).getTitle() + " threw a smoke bomb.");

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


	private void throwSmokeBombAndFlee(final Vector2 thrownPosition, final Entity thrower, final Room room) {
		if (throwAttackCompo == null) {
			final GridPositionComponent enemyPos = Mappers.gridPositionComponent.get(thrower);
			if (TileUtil.getDistanceBetweenTiles(enemyPos.coord(), thrownPosition) > 5) {
				// Too far, cannot throw smoke bomb
				throwSmokeBomb = false;
				return;
			}
			
			throwAttackCompo = room.engine.createComponent(AttackComponent.class);
			throwAttackCompo.setAttackAnimation(new AttackAnimation(null, null, false));
			
			Tile playerTile = TileUtil.getTileAtGridPos(thrownPosition, room);
			
			final Entity smokeBomb = room.entityFactory.itemFactory.createItem(ItemEnum.SMOKE_BOMB);
			throwAttackCompo.getAttackAnimation().setAttackAnim(Assets.smoke_bomb_item.getRegion());
			
			Action finishThrowAction = new Action(){
			  @Override
			  public boolean act(float delta){
				throwSmokeBomb = false;
				
				throwAttackCompo.clearAttackImage();
				
				int leftDistance = TileUtil.getDistanceBetweenTiles(enemyPos.coord(), LEFT_CLONE_TILE);
				int rightDistance = TileUtil.getDistanceBetweenTiles(enemyPos.coord(), RIGHT_CLONE_TILE);
				if (leftDistance >= rightDistance) {
					fleeTile = RIGHT_CLONE_TILE;
				} else {
					fleeTile = LEFT_CLONE_TILE;
				}
				
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
