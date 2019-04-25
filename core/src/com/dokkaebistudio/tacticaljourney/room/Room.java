/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.dokkaebistudio.tacticaljourney.room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.dialog.Dialog;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.rendering.MapRenderer;
import com.dokkaebistudio.tacticaljourney.rendering.RoomRenderer;
import com.dokkaebistudio.tacticaljourney.room.generation.GeneratedRoom;
import com.dokkaebistudio.tacticaljourney.room.generation.RoomGenerator;
import com.dokkaebistudio.tacticaljourney.room.managers.AttackManager;
import com.dokkaebistudio.tacticaljourney.room.managers.TurnManager;
import com.dokkaebistudio.tacticaljourney.room.rewards.AbstractRoomReward;
import com.dokkaebistudio.tacticaljourney.room.rewards.RoomRewardMoney;
import com.dokkaebistudio.tacticaljourney.singletons.GameTimeSingleton;
import com.dokkaebistudio.tacticaljourney.systems.EnemySystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class Room extends EntitySystem {
	public Floor floor;
	private int index;
	
	public String roomPattern;
	public RoomType type;
	
	private RoomState lastInGameState;
	private RoomState state;
	private RoomState nextState;
	
	public Tile[][] grid;
	
	public PooledEngine engine;
	public EntityFactory entityFactory;
	
	public TurnManager turnManager;

	public AttackManager attackManager;
		
	/** Whether the player has already entered this room or not. */
	private RoomVisitedState visited;
	private boolean justEntered;
	
	/** Whether this room has been cleared. */
	private RoomClearedState cleared;
	

	/** All the entities of this room. */
	private Array<Entity> allEntities;
	
	/** For each tile, gives the list of entities. */
	private Map<Vector2,Set<Entity>> entitiesAtPositions;
	
	/** The enemy entities of this room. */
	private List<Entity> enemies;

	/** The neutral entities of this room. */
	private List<Entity> neutrals;
	
	/** The current dialog displayed. */
	private Entity dialog;
	private Dialog requestedDialog;
	
	
	
	/** The entities to remove during this frame. */
	private Set<Entity> entitiesToRemove;

	
	
	/** The items added to this room at the current frame. */
	private List<Entity> addedItems;
	/** The items removed from the room at the current frame. */
	private List<Entity> removedItems;
	
	private List<AbstractRoomReward> rewards;

	
	/** The doors entities of this room. */
	private List<Entity> doors;
	
	private Room northNeighbor;
	private Room southNeighbor;
	private Room westNeighbor;
	private Room eastNeighbor;

	public Room (Floor f, int index, PooledEngine engine, EntityFactory ef, RoomType type) {
		this.priority = 1;
		
		this.index = index;
		
		this.floor = f;
		this.engine = engine;
		this.entityFactory = ef;
		this.turnManager = new TurnManager(this);
		this.attackManager = new AttackManager(this);
		this.type = type;
		this.visited = RoomVisitedState.NEVER_VISITED;
		
		this.allEntities = new Array<>();
		this.entitiesAtPositions = new HashMap<>();
		this.entitiesToRemove = new HashSet<>();
		this.addedItems = new ArrayList<>();
		this.removedItems = new ArrayList<>();
		
		this.enemies = new ArrayList<>();
		this.neutrals = new ArrayList<>();
		this.doors = new ArrayList<>();

		
		this.rewards = new ArrayList<>();
	}
	
	public Array<Entity> getAllEntities() {
		return allEntities;
	}
	
	public void addToAllEntities(Entity e) {
		if (!this.allEntities.contains(e, true)) {
			this.allEntities.add(e);
		}
	}

	public Map<Vector2, Set<Entity>> getEntitiesAtPosition() {
		return entitiesAtPositions;
	}
	
	/**
	 * Add an entity at the given position.
	 * @param e the entity
	 * @param pos the position
	 */
	public void addEntityAtPosition(Entity e, Vector2 pos) {
		Set<Entity> set = entitiesAtPositions.get(pos);
		
		if (set == null) {
			set = new HashSet<>();
			entitiesAtPositions.put(new Vector2(pos), set);
		}
		set.add(e);
		
		this.addToAllEntities(e);
	}
	
	/**
	 * Remove an entity at the given position.
	 * @param e the entity
	 * @param pos the position
	 */
	public void removeEntityAtPosition(Entity e, Vector2 pos) {	
		if (e == null) return;
		
		Set<Entity> set = entitiesAtPositions.get(pos);
		
		if (set != null) {
			set.remove(e);
			this.allEntities.removeValue(e, true);
		}
	}
	
	/**
	 * Get the map that gives the entities at each position of the grid.
	 */
	public Set<Entity> getEntitiesAtPosition(Vector2 pos) {
		return entitiesAtPositions.get(pos);
	}
	
	/**
	 * Get the set of entities with the given component at the given position.
	 */
	public Set<Entity> getEntitiesAtPositionWithComponent(Vector2 pos, Class componentClass) {
		Set<Entity> result = null;
		Set<Entity> set = entitiesAtPositions.get(pos);
		if (set != null) {
			for (Entity e : set) {
				Component component = ComponentMapper.getFor(componentClass).get(e);
				if (component != null) {
					if (result == null) result = new HashSet<>();
					result.add(e);
				}
			}
		}
		
		if (result == null) {
			return Collections.emptySet();
		} else {
			return result;
		}
	}
	
	
	/**
	 * Add an entity to the game.
	 * @param e the entity to add
	 */
	public void addEntity(Entity e) {
		this.addToAllEntities(e);
		engine.addEntity(e);
	}
	
	/**
	 * Remove an entity from the game.
	 * @param e the entity
	 */
	public void removeEntity(Entity e) {
		this.allEntities.removeValue(e, true);
		this.entitiesToRemove.add(e);
	}
	
	
	/**
	 * Remove an entity from the game.
	 * @param e the entity
	 */
	private void removeEntityFromEngine(Entity e) {
		GridPositionComponent posCompo = Mappers.gridPositionComponent.get(e);
		if (posCompo != null) {
			this.removeEntityAtPosition(e, posCompo.coord());
		}
		if (this.getEnemies().contains(e)) {
			this.removeEnemy(e);
		}
		if (this.getNeutrals().contains(e)) {
			this.removeNeutral(e);
		}
		
		engine.removeEntity(e);
	}

	
	public void leaveRoom(Room nextRoom) {
		this.state = RoomState.PLAYER_TURN_INIT;
		this.floor.enterRoom(nextRoom);
	}
	
	
	@Override
	public void update(float deltaTime) {
		// Update the elapsed time
		if (!state.isPaused()) {
			GameTimeSingleton gtSingleton = GameTimeSingleton.getInstance();
			gtSingleton.updateElapsedTime(deltaTime);
		}
		
		
		// Remove entities that are no longer in the game
		for (Entity e : this.entitiesToRemove) {
			removeEntityFromEngine(e);
		}
		this.entitiesToRemove.clear();
		

		// Update the visited status
		if (this.visited == RoomVisitedState.FIRST_ENTRANCE || this.visited == RoomVisitedState.ENTRANCE) {
			this.visited = RoomVisitedState.VISITED;
		} else if (this.justEntered) {
			if (!this.visited.isVisited()) {
				this.visited = RoomVisitedState.FIRST_ENTRANCE;
				
				AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(GameScreen.player);
				alterationReceiverComponent.onRoomVisited(GameScreen.player, this);
				
				for (Entity enemy : this.getEnemies()) {
					Mappers.enemyComponent.get(enemy).onRoomVisited(enemy, this);
				}
			} else {
				this.visited = RoomVisitedState.ENTRANCE;
			}
			this.justEntered = false;
		}

		
		
		// Update cleared status
		if (this.getCleared() == RoomClearedState.JUST_CLEARED) {
			this.cleared = RoomClearedState.CLEARED;
		}	
		// Check if room cleared
		if (!this.isCleared() && this.enemies.isEmpty()) {
			this.setCleared(RoomClearedState.JUST_CLEARED);
			
			Journal.addEntry("The " + this.type.title() + " has been cleared");
			MapRenderer.requireRefresh();

			Entity player = GameScreen.player;
			AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(player);
			if (alterationReceiverComponent != null) {
				alterationReceiverComponent.onRoomCleared(player, this);
			}

			for (Entity door : doors) {
				Mappers.doorComponent.get(door).open(door);
			}
			
			// Receive rewards
			for (AbstractRoomReward reward : this.rewards) {
				reward.receive(player, this);
			}
		}

		
		// Update the room state
		updateState();
		
		
		Mappers.playerComponent.get(GameScreen.player).setActionDoneAtThisFrame(false);
	}
	
	
	/**
	 * Create the room. Generate it's layout and content.
	 */
	public void create() {
		this.state = RoomState.PLAYER_TURN_INIT;

		this.rewards.add(new RoomRewardMoney(1 + RandomSingleton.getInstance().nextSeededInt(5)));
	 
		RoomGenerator generator = this.floor.getFloorGenerator().getRoomGenerator();
		
		// Layout
		GeneratedRoom generatedRoom = generator.generateRoomLayout(this, this.northNeighbor, this.eastNeighbor, this.southNeighbor, this.westNeighbor);
		grid = generatedRoom.getTiles();

		// Content
		generator.generateRoomContent(this, generatedRoom);
		

		this.cleared = enemies.isEmpty() ? RoomClearedState.CLEARED : RoomClearedState.UNCLEARED;
	}


	public void setNextState(RoomState nextState) {
		if (this.nextState != RoomState.LEVEL_UP_POPIN) {
			this.nextState = nextState;
			if (this.nextState.isInGameState()) {
				this.lastInGameState = this.nextState;
			}
		}
	}
	public RoomState getNextState() {
		return this.nextState;
	}
	public RoomState getState() {
		return this.state;
	}
	
	public void forceState(RoomState forcedState) {
		this.state = forcedState;
	}
	
	public RoomState getLastInGameState() {
		return this.lastInGameState;
	}
	
	private void updateState() {
		if (this.nextState != null) {
			this.state = this.nextState;
			this.nextState = null;
			
			if (this.state.isPopinDisplayed()) {
				RoomRenderer.showBlackFilter();
			} else {
				RoomRenderer.hideBlackFilter();
			}
		}
	}


	
	/**
	 * Return the tile at the given position.
	 * @param x the abscissa
	 * @param y the ordinate
	 * @return the tile at the given position
	 */
	public Tile getTileAtGridPosition(int x, int y) {
		return grid[x][y];
	}
	
	/**
	 * Return the tile at the given position.
	 * @param pos the position
	 * @return the tile at the given position
	 */
	public Tile getTileAtGridPosition(Vector2 pos) {
		return grid[(int) pos.x][(int) pos.y];
	}

	
	
	
	// Getters and Setters
	
	/**
	 * Add an enemy in the room.
	 * @param enemy the enemy to add
	 */
	public void addEnemy(Entity enemy) {
		this.addEntity(enemy);
		this.enemies.add(enemy);
	}
	
	/**
	 * Remove an enemy from the room.
	 * @param enemy the enemy to remove
	 */
	public void removeEnemy(Entity enemy) {
		this.enemies.remove(enemy);
		this.removeEntity(enemy);
		
		if (state.isEnemyTurn() && EnemySystem.enemyCurrentyPlaying == enemy) {
			// Finish this enemy turn since it's dead
			this.setNextState(RoomState.ENEMY_TURN_INIT);
		}
	}
	
	/**
	 * @return true if there are remaining enemies in this room.
	 */
	public boolean hasEnemies() {
		return this.enemies.size() > 0;
	}
	
	public List<Entity> getEnemies() {
		return this.enemies;
	}
	
	
	public void addDoor(Entity d) {
		this.doors.add(d);
	}
	
	public List<Entity> getDoors() {
		return this.doors;
	}
	
	
	// Neighbors
	
	/** Set the neighbors.
	 * 
	 * @param nn
	 * @param sn
	 * @param wn
	 * @param en
	 */
	public void setNeighbors(Room nn, Room sn, Room wn, Room en) {
		this.northNeighbor = nn;
		this.southNeighbor = sn;
		this.westNeighbor = wn;
		this.eastNeighbor = en;
	}
	
	/**
	 * @return the number of neighbors for the current room.
	 */
	public int getNumberOfNeighbors() {
		int nb = 0;
		if (northNeighbor != null) nb ++;
		if (southNeighbor != null) nb ++;
		if (westNeighbor != null) nb ++;
		if (eastNeighbor != null) nb ++;
		return nb;
	}


	public Room getNorthNeighbor() {
		return northNeighbor;
	}


	public void setNorthNeighbor(Room northNeighbor) {
		this.northNeighbor = northNeighbor;
	}


	public Room getSouthNeighbor() {
		return southNeighbor;
	}


	public void setSouthNeighbor(Room southNeighbor) {
		this.southNeighbor = southNeighbor;
	}


	public Room getWestNeighbor() {
		return westNeighbor;
	}


	public void setWestNeighbor(Room westNeighbor) {
		this.westNeighbor = westNeighbor;
	}


	public Room getEastNeighbor() {
		return eastNeighbor;
	}


	public void setEastNeighbor(Room easthNeighbor) {
		this.eastNeighbor = easthNeighbor;
	}


	public boolean isVisited() {
		return justEntered || visited.isVisited();
	}

	public RoomVisitedState getVisited() {
		return visited;
	}

	public void setVisited(RoomVisitedState visitedState) {
		this.visited = visitedState;
	}

	public List<Entity> getAddedItems() {
		return addedItems;
	}

	public void setAddedItems(List<Entity> addedItems) {
		this.addedItems = addedItems;
	}

	public List<Entity> getRemovedItems() {
		return removedItems;
	}

	public void setRemovedItems(List<Entity> removedItems) {
		this.removedItems = removedItems;
	}
	
	
	/**
	 * Add a neutral in the room.
	 * @param neutral the neutral to add
	 */
	public void addNeutral(Entity neutral) {
		this.addEntity(neutral);
		this.neutrals.add(neutral);
	}
	
	/**
	 * Remove a neutral from the room.
	 * @param neutral the neutral to remove
	 */
	public void removeNeutral(Entity neutral) {
		this.neutrals.remove(neutral);
		this.removeEntity(neutral);
	}

	public List<Entity> getNeutrals() {
		return neutrals;
	}

	public void setNeutrals(List<Entity> neutrals) {
		this.neutrals = neutrals;
	}

	public Entity getDialog() {
		return dialog;
	}

	public void setDialog(Entity dialog) {
		this.addEntity(dialog);
		this.dialog = dialog;
	}
	
	public void removeDialog() {
		this.removeEntity(this.dialog);
		this.dialog = null;
	}

	public Dialog getRequestedDialog() {
		return requestedDialog;
	}

	public void setRequestedDialog(String speaker, String text) {
		Dialog d = new Dialog(speaker, text, false);
		this.requestedDialog = d;
	}
	public void setRequestedDialog(String speaker, String text, boolean force) {
		Dialog d = new Dialog(speaker, text, force);
		this.requestedDialog = d;
	}

	public void clearRequestedDialog() {
		this.requestedDialog = null;
	}

	public boolean isCleared() {
		return cleared == RoomClearedState.CLEARED || cleared == RoomClearedState.JUST_CLEARED;
	}
	
	public RoomClearedState getCleared() {
		return cleared;
	}

	public void setCleared(RoomClearedState cleared) {
		this.cleared = cleared;
	}

	public List<AbstractRoomReward> getRewards() {
		return rewards;
	}

	public void addRewards(AbstractRoomReward reward) {
		this.rewards.add(reward);
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isJustEntered() {
		return justEntered;
	}

	public void setJustEntered(boolean justEntered) {
		this.justEntered = justEntered;
	}

	
	
}
