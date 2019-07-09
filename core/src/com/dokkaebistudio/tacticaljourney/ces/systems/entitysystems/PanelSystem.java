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

package com.dokkaebistudio.tacticaljourney.ces.systems.entitysystems;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.dokkaebistudio.tacticaljourney.ces.systems.NamedSystem;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.RoomState;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class PanelSystem extends NamedSystem {	
	
    private List<Entity> allPanels = new ArrayList<>();
	
	public PanelSystem(Room r) {
		this.priority = 23;
		this.room = r;
	}

	@Override
	public void performUpdate(float deltaTime) {
		if (room.getState() == RoomState.ENEMY_END_TURN || room.getVisited().justEntered()) {
			fillPanels();
			allPanels.forEach(e -> Mappers.panelComponent.get(e).updateText(room.floor.getTurns(), room.floor.getTurnThreshold()));
		}
	}

	
	
	private void fillPanels() {
		allPanels.clear();
		for (Entity e : room.getAllEntities()) {
			if (Mappers.panelComponent.has(e)) allPanels.add(e);
		}
	}
}
