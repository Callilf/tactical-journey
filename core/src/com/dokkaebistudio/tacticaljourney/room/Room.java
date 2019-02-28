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
import com.dokkaebistudio.tacticaljourney.GameTimeSingleton;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.dialog.Dialog;
import com.dokkaebistudio.tacticaljourney.factory.EntityFactory;
import com.dokkaebistudio.tacticaljourney.room.generation.GeneratedRoom;
import com.dokkaebistudio.tacticaljourney.room.generation.RoomGenerator;
import com.dokkaebistudio.tacticaljourney.room.managers.AttackManager;
import com.dokkaebistudio.tacticaljourney.room.managers.TurnManager;
import com.dokkaebistudio.tacticaljourney.systems.EnemySystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class Room extends EntitySystem {
	public Floor floor;
	
	public String roomPattern;
	public RoomType type;
	
	private RoomState state;
	private RoomState nextState;
	
	public Tile[][] grid;
	
	public PooledEngine engine;
	public EntityFactory entityFactory;
	
	public TurnManager turnManager;

	public AttackManager attackManager;
		
	/** Whether the player has already entered this room or not. */
	private boolean visited;
	

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
	private List<Entity> entitiesToRemove;

	
	
	/** The items added to this room at the current frame. */
	private List<Entity> addedItems;
	/** The items removed from the room at the current frame. */
	private List<Entity> removedItems;

	
	
	private Room northNeighbor;
	private Room southNeighbor;
	private Room westNeighbor;
	private Room eastNeighbor;
	

	public Room (Floor f, PooledEngine engine, EntityFactory ef, RoomType type) {
		this.priority = 1;
		
		this.floor = f;
		this.engine = engine;
		this.entityFactory = ef;
		this.turnManager = new TurnManager(this);
		this.type = type;
		this.visited = false;
		
		this.allEntities = new Array<>();
		this.entitiesAtPositions = new HashMap<>();
		this.entitiesToRemove = new ArrayList<>();
		this.addedItems = new ArrayList<>();
		this.removedItems = new ArrayList<>();
	}
	
	public Array<Entity> getAllEntities() {
		return allEntities;
	}
	
	public void addToAllEntities(Entity e) {
		if (!this.allEntities.contains(e, true)) {
			this.allEntities.add(e);
		}
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
		GridPositionComponent posCompo = Mappers.gridPositionComponent.get(e);
		if (posCompo != null) {
			this.removeEntityAtPosition(e, posCompo.coord());
		}
		
		this.allEntities.removeValue(e, true);		
		this.entitiesToRemove.add(e);
	}
	
	

	
	public void leaveRoom(Room nextRoom) {
		this.state = RoomState.PLAYER_TURN_INIT;
		this.floor.enterRoom(nextRoom);
	}
	
	
	@Override
	public void update(float deltaTime) {
		if (!state.isPaused()) {
			GameTimeSingleton gtSingleton = GameTimeSingleton.getInstance();
			gtSingleton.updateElapsedTime(deltaTime);
		}
		
		for (Entity e : this.entitiesToRemove) {
			engine.removeEntity(e);
		}
		this.entitiesToRemove.clear();
		
		updateState();
	}
	
	
	/**
	 * Create the room. Generate it's layout and content.
	 */
	public void create() {
		this.state = RoomState.PLAYER_TURN_INIT;

		enemies = new ArrayList<>();
		neutrals = new ArrayList<>();
		attackManager = new AttackManager(this);
		
		// Layout
		RoomGenerator generator = new RoomGenerator(this.entityFactory);
		GeneratedRoom generatedRoom = generator.generateRoomLayout(this, this.northNeighbor, this.eastNeighbor, this.southNeighbor, this.westNeighbor);
		grid = generatedRoom.getTiles();

		// Content
		generator.generateRoomContent(this, generatedRoom);
	}


	public void setNextState(RoomState nextState) {
		if (this.nextState != RoomState.LEVEL_UP_POPIN) {
			this.nextState = nextState;
		}
	}
	public RoomState getNextState() {
		return this.nextState;
	}
	public RoomState getState() {
		return this.state;
	}
	
	private void updateState() {
		if (this.nextState != null) {
			this.state = this.nextState;
			this.nextState = null;
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
		return visited;
	}


	public void setVisited(boolean visited) {
		this.visited = visited;
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
		this.removeEntity(neutral);
		this.neutrals.remove(neutral);
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

	public void setRequestedDialog(String text, Vector2 pos) {
		Dialog d = new Dialog(text, pos, false);
		this.requestedDialog = d;
	}
	public void setRequestedDialog(String text, Vector2 pos, boolean force) {
		Dialog d = new Dialog(text, pos, force);
		this.requestedDialog = d;
	}

	public void clearRequestedDialog() {
		this.requestedDialog = null;
	}
	
}
