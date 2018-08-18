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

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.dokkaebistudio.tacticaljourney.components.MovementComponent;
import com.dokkaebistudio.tacticaljourney.components.TransformComponent;

public class MovementSystem extends IteratingSystem {
	private Vector2 tmp = new Vector2();

	private ComponentMapper<TransformComponent> tm;
	private ComponentMapper<MovementComponent> mm;
	
	public MovementSystem() {
		super(Family.all(TransformComponent.class, MovementComponent.class).get());
		
		tm = ComponentMapper.getFor(TransformComponent.class);
		mm = ComponentMapper.getFor(MovementComponent.class);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		TransformComponent pos = tm.get(entity);
		MovementComponent mov = mm.get(entity);;
		
		tmp.set(mov.accel).scl(deltaTime);
		mov.velocity.add(tmp);
		
		tmp.set(mov.velocity).scl(deltaTime);
		pos.pos.add(tmp.x, tmp.y, 0.0f);
	}
}
