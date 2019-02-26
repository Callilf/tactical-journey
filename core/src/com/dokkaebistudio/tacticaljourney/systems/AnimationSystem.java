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
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.dokkaebistudio.tacticaljourney.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class AnimationSystem extends EntitySystem implements RoomSystem {

	private Room room;

	
	public AnimationSystem(Room room) {
		this.priority = 3;

		this.room = room;
	}
	
	
	@Override
	public void enterRoom(Room newRoom) {
		this.room = newRoom;
	}
	
	@Override
	public void update(float deltaTime) {
		
		for(Entity entity : room.getAllEntities()) {
			if (!Mappers.animationComponent.has(entity))	continue;
			AnimationComponent anim = Mappers.animationComponent.get(entity);

			SpriteComponent spriteCompo = Mappers.spriteComponent.get(entity);
			StateComponent state = Mappers.stateComponent.get(entity);
			if (spriteCompo == null || state == null) continue;
			
			Animation<Sprite> animation = anim.animations.get(state.get());
			
			if (animation != null) {
				spriteCompo.setSprite(new Sprite(animation.getKeyFrame(state.time))); 
			}
			
			state.time += deltaTime;
		}

	}
}
