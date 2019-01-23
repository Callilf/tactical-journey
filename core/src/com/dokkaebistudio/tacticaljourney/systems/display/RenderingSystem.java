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

import static com.dokkaebistudio.tacticaljourney.GameScreen.SCREEN_H;
import static com.dokkaebistudio.tacticaljourney.GameScreen.SCREEN_W;

import java.util.Comparator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.dokkaebistudio.tacticaljourney.components.ParentRoomComponent;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TransformComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class RenderingSystem extends IteratingSystem implements RoomSystem {

	private SpriteBatch batch;
	private Array<Entity> renderQueue;
	private Comparator<Entity> comparator;
	private OrthographicCamera cam;
	
	/** The current room. */
	private Room room;
	
	public RenderingSystem(SpriteBatch batch, Room room, OrthographicCamera camera) {
		super(Family.one(SpriteComponent.class, TextComponent.class).get());
				
		renderQueue = new Array<Entity>();
		
		comparator = new Comparator<Entity>() {
			@Override
			public int compare(Entity entityA, Entity entityB) {
				if (!Mappers.transfoComponent.has(entityA) && Mappers.transfoComponent.has(entityB)) {
					return -1;
				} else if (!Mappers.transfoComponent.has(entityB) && Mappers.transfoComponent.has(entityA)) {
					return 1;
				} else if (!Mappers.transfoComponent.has(entityA) && !Mappers.transfoComponent.has(entityB)) {
					
					
					if (!Mappers.gridPositionComponent.has(entityA) && Mappers.gridPositionComponent.has(entityB)) {
						return -1;
					} else if (!Mappers.gridPositionComponent.has(entityB) && Mappers.gridPositionComponent.has(entityA)) {
						return 1;
					} else if (!Mappers.gridPositionComponent.has(entityA) && !Mappers.gridPositionComponent.has(entityB)) {
						return 0;
					}
					
					return (int)Math.signum(Mappers.gridPositionComponent.get(entityA).zIndex - Mappers.gridPositionComponent.get(entityB).zIndex);
				}
				
				return (int) Math.signum(Mappers.transfoComponent.get(entityA).pos.z - Mappers.transfoComponent.get(entityB).pos.z);
			}
		};
		
		this.batch = batch;
		
		cam = camera;
	}
	
	@Override
	public void enterRoom(Room newRoom) {
		this.room = newRoom;
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		renderQueue.sort(comparator);
		
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		
		for (Entity entity : renderQueue) {
			ParentRoomComponent parentRoomComponent = Mappers.parentRoomComponent.get(entity);
			if (parentRoomComponent != null && parentRoomComponent.getParentRoom() != this.room) {
				continue;
			}
			
			SpriteComponent spriteCompo = Mappers.spriteComponent.get(entity);
			TextComponent textCompo = Mappers.textComponent.get(entity);
			
			if (spriteCompo == null && textCompo == null) {
				continue;
			}

			if (Mappers.transfoComponent.has(entity)) {			
				// use transform component for drawing position
				TransformComponent t = Mappers.transfoComponent.get(entity);
				
				
				if (spriteCompo != null && spriteCompo.getSprite() != null) {
					float x = t.pos.x;
					float y = t.pos.y;
				
					spriteCompo.getSprite().setPosition(x, y);
					if (!spriteCompo.hide) {
						spriteCompo.getSprite().draw(batch);
					}
				}
				if (textCompo != null && textCompo.getFont() != null) {					
					textCompo.getFont().draw(batch, textCompo.getText(), t.pos.x, t.pos.y);
				}
			} else if (Mappers.gridPositionComponent.has(entity)){
				// use grid position to render instead of real screen coordinates
				GridPositionComponent g = Mappers.gridPositionComponent.get(entity);
				
				Vector2 realPos = TileUtil.convertGridPosIntoPixelPos(g.coord);
				if (spriteCompo != null && spriteCompo.getSprite() != null) {
					spriteCompo.getSprite().setPosition(realPos.x, realPos.y);
					if (!spriteCompo.hide) {
						spriteCompo.getSprite().draw(batch);
					}
				}
				if (textCompo != null && textCompo.getFont() != null) {
					textCompo.getFont().draw(batch, textCompo.getText(), realPos.x, realPos.y + textCompo.getHeight());
				}

			} 
		}
		
		batch.end();
		renderQueue.clear();
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		renderQueue.add(entity);
	}
	
	
	public OrthographicCamera getCamera() {
		return cam;
	}
}
