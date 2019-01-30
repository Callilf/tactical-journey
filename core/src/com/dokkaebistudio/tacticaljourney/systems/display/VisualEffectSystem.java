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

package com.dokkaebistudio.tacticaljourney.systems.display;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.dokkaebistudio.tacticaljourney.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.components.display.VisualEffectComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class VisualEffectSystem extends EntitySystem implements RoomSystem {

	
	/** The current room. */
	private Room room;
	private List<Entity> entitiesToRemove;
	
	public VisualEffectSystem(Room room) {
		this.room = room;
		this.entitiesToRemove = new ArrayList<>();
	}
	
	@Override
	public void enterRoom(Room newRoom) {
		this.room = newRoom;
	}

	@Override
	public void update(float deltaTime) {
		entitiesToRemove.clear();

		for (Entity entity : room.getAllEntities()) {
		
			// Handle visual effects
			VisualEffectComponent visualEffectComponent = Mappers.visualEffectComponent.get(entity);
			if (visualEffectComponent != null) {
				AnimationComponent animationComponent = Mappers.animationComponent.get(entity);
				StateComponent stateComponent = Mappers.stateComponent.get(entity);
				if (animationComponent != null && stateComponent != null) {
					Animation<Sprite> animation = animationComponent.animations.get(stateComponent.get());
					boolean animationFinished = animation.isAnimationFinished(stateComponent.time);
					
					if (animationFinished) {
						// Remove the visual effect
						entitiesToRemove.add(entity);
					}
				}
			}
		
		}
		
		// Remove entities from the game
		for(Entity e : entitiesToRemove) {
			room.removeEntity(e);
		}
		
	}
	

}
