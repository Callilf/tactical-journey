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

import java.util.Comparator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class RenderingSystem extends EntitySystem implements RoomSystem {

	private SpriteBatch batch;
	private Array<Entity> renderQueue;
	private Comparator<Entity> comparator;
	private OrthographicCamera cam;
	
	/** The current room. */
	private Room room;
	
	public RenderingSystem(SpriteBatch batch, Room room, OrthographicCamera camera) {
				
		renderQueue = new Array<Entity>();
		
		comparator = new Comparator<Entity>() {
			@Override
			public int compare(Entity entityA, Entity entityB) {
				GridPositionComponent gridPositionComponentA = Mappers.gridPositionComponent.get(entityA);
				GridPositionComponent gridPositionComponentB = Mappers.gridPositionComponent.get(entityB);
				if (gridPositionComponentA == null && gridPositionComponentB == null) return 0;
				else if (gridPositionComponentA == null) return -1;
				else if (gridPositionComponentB == null) return 1;
				
				return (int) Math.signum(gridPositionComponentA.zIndex - gridPositionComponentB.zIndex);
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
		
		for(Entity e : room.getAllEntities()) {
			renderQueue.addAll(e);
		}
		
		renderQueue.sort(comparator);
		
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		
		for (Entity entity : renderQueue) {
			SpriteComponent spriteCompo = Mappers.spriteComponent.get(entity);
			TextComponent textCompo = Mappers.textComponent.get(entity);
			
			GridPositionComponent gridPosComponent = Mappers.gridPositionComponent.get(entity);
			if (gridPosComponent == null) continue;
			
			if (gridPosComponent.hasAbsolutePos()) {		
				// use transform component for drawing position
				
				
				if (spriteCompo != null && spriteCompo.getSprite() != null) {
					float x = gridPosComponent.getAbsolutePos().x;
					float y = gridPosComponent.getAbsolutePos().y;
				
					spriteCompo.getSprite().setPosition(x, y);
					if (!spriteCompo.hide) {
						spriteCompo.getSprite().draw(batch);
					}
				}
				if (textCompo != null && textCompo.getFont() != null) {					
					textCompo.getFont().draw(batch, textCompo.getText(), gridPosComponent.getAbsolutePos().x, gridPosComponent.getAbsolutePos().y);
				}
			} else {
				// use grid position to render instead of real screen coordinates
				
				Vector2 realPos = TileUtil.convertGridPosIntoPixelPos(gridPosComponent.coord());
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
	
	
	public OrthographicCamera getCamera() {
		return cam;
	}
}
