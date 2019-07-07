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

package com.dokkaebistudio.tacticaljourney.systems.entitysystems;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.NamedSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class StateSystem extends NamedSystem {	
	
    /** The entities with an state component of the current room. */
    private List<Entity> allEntitiesOfCurrentRoom = new ArrayList<>();

	
	public StateSystem(Room r) {
		this.priority = 2;
		this.room = r;
	}
	
	
	@Override
	public void performUpdate(float deltaTime) {
		fillEntitiesOfCurrentRoom();
		for (Entity entity : allEntitiesOfCurrentRoom) {
			Mappers.stateComponent.get(entity).time += deltaTime;
		}
	}
	
	
	private void fillEntitiesOfCurrentRoom() {
		allEntitiesOfCurrentRoom.clear();
		for (Entity e : room.getAllEntities()) {
			if (Mappers.stateComponent.has(e)) allEntitiesOfCurrentRoom.add(e);
		}
	}
}
