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
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.dokkaebistudio.tacticaljourney.ces.components.display.AnimationComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.StateComponent;
import com.dokkaebistudio.tacticaljourney.ces.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class AnimationSystem extends EntitySystem implements RoomSystem {
	
	private Room room;

    /** The entities with an animation component of the current room. */
    private List<Entity> allEntitiesOfCurrentRoom = new ArrayList<>();

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
		fillEntitiesOfCurrentRoom();
		
		for (Entity entity : allEntitiesOfCurrentRoom) {
			AnimationComponent anim = Mappers.animationComponent.get(entity);
	
			SpriteComponent spriteCompo = Mappers.spriteComponent.get(entity);
			StateComponent state = Mappers.stateComponent.get(entity);
			if (spriteCompo == null || state == null) return;
			
			Animation<Sprite> animation = anim.getAnimation(state.get());
			
			if (animation != null) {
				Sprite keyFrame = animation.getKeyFrame(state.time);
				spriteCompo.getSprite().set(keyFrame);
			}
			
			state.time += deltaTime;
		}
	}
	
	
	private void fillEntitiesOfCurrentRoom() {
		allEntitiesOfCurrentRoom.clear();
		for (Entity e : room.getAllEntities()) {
			if (Mappers.animationComponent.has(e)) allEntitiesOfCurrentRoom.add(e);
		}
	}
}
