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

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.dokkaebistudio.tacticaljourney.components.player.PlayerComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;

public class TurnSystem extends IteratingSystem implements RoomSystem {	
	
	private Room room;

	
	public TurnSystem(Room r) {
		super(Family.all(PlayerComponent.class).get());
		this.room = r;
	}
	
	
    @Override
    public void enterRoom(Room newRoom) {
    	this.room = newRoom;	
    }
    
    @Override
    protected void processEntity(Entity moverEntity, float deltaTime) {}

	@Override
	public void update(float deltaTime) {
		
		switch(room.getState()) {
		case PLAYER_END_TURN:
			room.setNextState(RoomState.ENEMY_TURN_INIT);
			break;
		case ENEMY_END_TURN:
			room.turnManager.startNewTurn();
		default:
		}

	}
}
