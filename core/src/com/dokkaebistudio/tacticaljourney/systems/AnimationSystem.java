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
import com.dokkaebistudio.tacticaljourney.components.ParentRoomComponent;
import com.dokkaebistudio.tacticaljourney.components.StateComponent;
import com.dokkaebistudio.tacticaljourney.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class AnimationSystem extends IteratingSystem implements RoomSystem {

	private Room room;
	
	public AnimationSystem(Room room) {
		super(Family.all(SpriteComponent.class,
							AnimationComponent.class,
							StateComponent.class).get());
	}
	
	
	@Override
	public void enterRoom(Room newRoom) {
		this.room = newRoom;
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		ParentRoomComponent parentRoomComponent = Mappers.parentRoomComponent.get(entity);
		if (parentRoomComponent != null && parentRoomComponent.getParentRoom() != this.room) {
			return;
		}
		
		SpriteComponent spriteCompo = Mappers.spriteComponent.get(entity);
//		AnimationComponent anim = am.get(entity);
//		StateComponent state = sm.get(entity);
//		
//		Animation<Sprite> animation = anim.animations.get(state.get());
//		
//		if (animation != null) {
//			spriteCompo.setSprite(new Sprite(animation.getKeyFrame(state.time))); 
//		}
//		
//		state.time += deltaTime;
	}
}
