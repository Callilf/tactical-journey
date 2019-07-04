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

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.dokkaebistudio.tacticaljourney.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class StatusSystem extends EntitySystem implements RoomSystem {	
	
	private Stage fxStage;
	private Room room;
	
    /** The entities with a status receiver component of the current room. */
    private List<Entity> allEntitiesOfCurrentRoom = new ArrayList<>();

		
	public StatusSystem(Entity player, Room r, Stage stage) {
		this.priority = 12;

		this.fxStage = stage;
		this.room = r;
	}
	
	
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }
    
	@Override
	public void update(float deltaTime) {
		fillEntitiesOfCurrentRoom();
		
		for (Entity entity : allEntitiesOfCurrentRoom) {
			StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(entity);
			
			// Add or remove statuses
			if (statusReceiverComponent.getCurrentAction() != null) {
				Status currentStatus = statusReceiverComponent.getCurrentStatus();
	
				switch (statusReceiverComponent.getCurrentAction()) {
				case RECEIVE_STATUS:
					statusReceiverComponent.addStatus(entity, currentStatus, room,fxStage);				
					break;
				case REMOVE_STATUS:
					statusReceiverComponent.removeStatus(entity, currentStatus, room);						
					break;
				
				}
				
				statusReceiverComponent.clearCurrentAction();
			}
	
			
			// Handle status on the player
			if (entity == GameScreen.player) {
				if (room.getState() == RoomState.PLAYER_TURN_INIT) {
					for (Status status : statusReceiverComponent.getStatuses()) {
						status.onStartTurn(entity, room);
					}
				}
				
				if (room.getState() == RoomState.PLAYER_END_TURN) {
					for (Status status : statusReceiverComponent.getStatuses()) {
						status.onEndTurn(entity, room);
						
						statusReceiverComponent.updateDuration(status, -1);
					}
				}
			}
			
			// Handle status on allies
			if (Mappers.allyComponent.has(entity) && entity != GameScreen.player) {
				if (room.getState() == RoomState.PLAYER_END_TURN) {
					for (Status status : statusReceiverComponent.getStatuses()) {
						status.onStartTurn(entity, room);
					}
				}
				
				if (room.getState() == RoomState.ALLY_END_TURN) {
					for (Status status : statusReceiverComponent.getStatuses()) {
						status.onEndTurn(entity, room);
						
						statusReceiverComponent.updateDuration(status, -1);
					}
				}
			}
			
			// Handle status on enemies
			if (Mappers.enemyComponent.has(entity)) {
				if (room.getState() == RoomState.ALLY_END_TURN) {
					for (Status status : statusReceiverComponent.getStatuses()) {
						status.onStartTurn(entity, room);
					}
				}
				
				if (room.getState() == RoomState.ENEMY_END_TURN) {
					for (Status status : statusReceiverComponent.getStatuses()) {
						status.onEndTurn(entity, room);
						
						statusReceiverComponent.updateDuration(status, -1);
					}
				}
			}

		}
	}

	
	
	private void fillEntitiesOfCurrentRoom() {
		allEntitiesOfCurrentRoom.clear();
		for (Entity e : room.getAllEntities()) {
			if (Mappers.statusReceiverComponent.has(e)) allEntitiesOfCurrentRoom.add(e);
		}
	}
}
