/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
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

package com.dokkaebistudio.tacticaljourney.systems;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.dokkaebistudio.tacticaljourney.ai.movements.ExplosionTileSearchService;
import com.dokkaebistudio.tacticaljourney.components.DestructibleComponent;
import com.dokkaebistudio.tacticaljourney.components.ExplosiveComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.enums.DamageType;
import com.dokkaebistudio.tacticaljourney.enums.StatesEnum;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.LootUtil;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class ExplosionSystem extends EntitySystem implements RoomSystem {	
	
	private Room room;
	public Stage fxStage;

	private ExplosionTileSearchService explosionTileSearchService = new ExplosionTileSearchService();
	
    /** The explosives of the current room that need updating. */
    private List<Entity> allExplosivesOfCurrentRoom;

	
	public ExplosionSystem(Room r, Stage fxStage) {
		this.priority = 7;

		this.room = r;
		this.fxStage = fxStage;
		
		allExplosivesOfCurrentRoom = new ArrayList<>();
	}
	
	
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }
    
	@Override
	public void update(float deltaTime) {
		
		// Update state
		if (room.getState() == RoomState.PLAYER_COMPUTE_MOVABLE_TILES) {

			fillEntitiesOfCurrentRoom();
			for (Entity explosive : allExplosivesOfCurrentRoom) {
				updateExplosiveEntityState(explosive);
				
				// Compute the explosion tiles
				computeExplosionTilesToDisplayToPlayer(explosive);
			}
			
		}
		

		// Explosive objects explode at the end of a turn
		if (room.getState() == RoomState.PLAYER_END_TURN) {

			fillEntitiesOfCurrentRoom();

			for (Entity explosive : allExplosivesOfCurrentRoom) {

				// Initialize the explosion turn for newly created bombs
				ExplosiveComponent explosiveComponent = Mappers.explosiveComponent.get(explosive);
				if (explosiveComponent.getExplosionTurn() == null) {
					explosiveComponent.setExplosionTurn(room.turnManager.getTurn() + explosiveComponent.getTurnsToExplode());
					
					StateComponent stateComponent = Mappers.stateComponent.get(explosive);
					stateComponent.set(
							explosiveComponent.getExplosionTurn() == room.turnManager.getTurn() ? 
									StatesEnum.EXPLODING_THIS_TURN.getState() : StatesEnum.EXPLODING_IN_SEVERAL_TURNS.getState());
					
					// Compute the explosion tiles
					computeExplosionTilesToDisplayToPlayer(explosive);
				}

				
				// Check whether the explosion is triggered this turn
				if (explosiveComponent.getExplosionTurn() == room.turnManager.getTurn()) {

					// EXPLODES
					explode(explosive);
				}
			}
		}
	}


	private void updateExplosiveEntityState(Entity explosive) {
		ExplosiveComponent explosiveComponent = Mappers.explosiveComponent.get(explosive);
		if (explosiveComponent.getExplosionTurn() != null && explosiveComponent.getExplosionTurn() == room.turnManager.getTurn()) {

			StateComponent stateComponent = Mappers.stateComponent.get(explosive);
			stateComponent.set(
					explosiveComponent.getExplosionTurn() == room.turnManager.getTurn() ? 
							StatesEnum.EXPLODING_THIS_TURN.getState() : StatesEnum.EXPLODING_IN_SEVERAL_TURNS.getState());

		}
	}
	
	
	private void fillEntitiesOfCurrentRoom() {
		allExplosivesOfCurrentRoom.clear();
		for (Entity e : room.getAllEntities()) {
			if (Mappers.explosiveComponent.has(e)) allExplosivesOfCurrentRoom.add(e);
		}
	}

	
	
    /**
     * For each explosive entity, compute the explosion tiles.
     */
    private void computeExplosionTilesToDisplayToPlayer(Entity entity) {
    	ExplosiveComponent explosionCompo = Mappers.explosiveComponent.get(entity);
    	
		//clear the movable tile
    	explosionCompo.clearExplosionTiles();
    		
    	//Build the movable tiles list
		explosionTileSearchService.buildExplosionTilesSet(entity, room);
//		explosionCompo.hideAttackableTiles();
    }
    
    
    
    //*********************
    // EXPLOOOODDDEEEEE
    
    
	/**
	 * The explosive entity explodes and deals damages to anything caught in the
	 * blast.
	 * 
	 * @param explosive the explosive
	 */
	private void explode(Entity explosive) {
		ExplosiveComponent explosiveComponent = Mappers.explosiveComponent.get(explosive);
		for (Entity attackableTile : explosiveComponent.attackableTiles) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(attackableTile);
			Entity target = TileUtil.getAttackableEntityOnTile(gridPositionComponent.coord(), room);

			// Deal damage to any entity on the tile
			if (target != null && explosive != target) {
				room.attackManager.applyDamage(explosive, target, explosiveComponent.getDamage(), DamageType.EXPLOSION, null);
			}

			// Destroy destructible entities
			Set<Entity> destructibles = TileUtil.getEntitiesWithComponentOnTile(gridPositionComponent.coord(),
					DestructibleComponent.class, room);
			for (Entity d : destructibles) {
				LootUtil.destroy(d, room);
			}
			room.entityFactory.effectFactory.createExplosionEffect(room, gridPositionComponent.coord());
		}
		room.removeEntity(explosive);


	}

}
