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

import static com.dokkaebistudio.tacticaljourney.GameScreen.SCREEN_H;
import static com.dokkaebistudio.tacticaljourney.GameScreen.SCREEN_W;

import java.util.Comparator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.TextComponent;
import com.dokkaebistudio.tacticaljourney.components.TransformComponent;

public class RenderingSystem extends IteratingSystem {

	private SpriteBatch batch;
	private Array<Entity> renderQueue;
	private Comparator<Entity> comparator;
	private OrthographicCamera cam;
	
	private ComponentMapper<SpriteComponent> spriteM;
	private ComponentMapper<TextComponent> textM;
	private ComponentMapper<TransformComponent> transformM;
	private final ComponentMapper<GridPositionComponent> gridPositionM;
	
	public RenderingSystem(SpriteBatch batch) {
		super(Family.one(SpriteComponent.class, TextComponent.class).get());
		
		spriteM = ComponentMapper.getFor(SpriteComponent.class);
		textM = ComponentMapper.getFor(TextComponent.class);
		transformM = ComponentMapper.getFor(TransformComponent.class);
		gridPositionM = ComponentMapper.getFor(GridPositionComponent.class);
		
		renderQueue = new Array<Entity>();
		
		comparator = new Comparator<Entity>() {
			@Override
			public int compare(Entity entityA, Entity entityB) {
				if (!transformM.has(entityA) && transformM.has(entityB)) {
					return 1;
				} else if (!transformM.has(entityB) && transformM.has(entityA)) {
					return -1;
				} else if (!transformM.has(entityA) && !transformM.has(entityB)) {
					return 0;
				}
				
				return (int)Math.signum(transformM.get(entityB).pos.z -
										transformM.get(entityA).pos.z);
			}
		};
		
		this.batch = batch;
		
		cam = new OrthographicCamera(SCREEN_W, SCREEN_H);
		cam.position.set(SCREEN_W/2, SCREEN_H/2, 0);
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		renderQueue.sort(comparator);
		
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		
		for (Entity entity : renderQueue) {
			SpriteComponent spriteCompo = spriteM.get(entity);
			TextComponent textCompo = textM.get(entity);
			
			if (spriteCompo == null && textCompo == null) {
				continue;
			}

			if (gridPositionM.has(entity)){
				// use grid position to render instead of real screen coordinates
				GridPositionComponent g = gridPositionM.get(entity);
				
				float x = g.coord.x * GameScreen.GRID_SIZE + GameScreen.LEFT_RIGHT_PADDING;
				float y = g.coord.y * GameScreen.GRID_SIZE + GameScreen.BOTTOM_MENU_HEIGHT;
				if (spriteCompo != null && spriteCompo.getSprite() != null) {
					spriteCompo.getSprite().setPosition(x, y);
					spriteCompo.getSprite().draw(batch);
				}
				if (textCompo != null && textCompo.getFont() != null) {
					textCompo.getFont().draw(batch, textCompo.getText(), x, y);
				}

			} else if (transformM.has(entity)) {
				//TODO This is not used at the moment, but will probably used later
				
				// use transform component for drawing position
				TransformComponent t = transformM.get(entity);
				
				
				if (spriteCompo != null && spriteCompo.getSprite() != null) {
					float width = spriteCompo.getSprite().getRegionWidth();
					float height = spriteCompo.getSprite().getRegionHeight();
					float originX = width * 0.5f;
					float originY = height * 0.5f;
					float x = t.pos.x - originX;
					float y = t.pos.y - originY;
				
					spriteCompo.getSprite().setPosition(x, y);
					spriteCompo.getSprite().draw(batch);
				}
				if (textCompo != null && textCompo.getFont() != null) {					
					textCompo.getFont().draw(batch, textCompo.getText(), t.pos.x, t.pos.y);
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
