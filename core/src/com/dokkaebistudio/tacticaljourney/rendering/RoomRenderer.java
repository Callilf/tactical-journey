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

package com.dokkaebistudio.tacticaljourney.rendering;

import java.util.Comparator;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class RoomRenderer implements Renderer, RoomSystem {

	private static Stage stage;
	private SpriteBatch batch;
	private Comparator<Entity> comparator;
	private OrthographicCamera cam;
	private Array<Entity> renderQueue;
	
	private static Image fullBackground;

	
	/** The current room. */
	private Room room;
	
	public RoomRenderer(Stage s, SpriteBatch batch, Room room, OrthographicCamera camera) {
		this.stage = s;
		this.comparator = new Comparator<Entity>() {
			@Override
			public int compare(Entity entityA, Entity entityB) {
				GridPositionComponent gridPositionComponentA = Mappers.gridPositionComponent.get(entityA);
				GridPositionComponent gridPositionComponentB = Mappers.gridPositionComponent.get(entityB);
				if (gridPositionComponentA == null && gridPositionComponentB == null) return 0;
				else if (gridPositionComponentA == null) return -1;
				else if (gridPositionComponentB == null) return 1;
				
				return (int) Math.signum(
						(gridPositionComponentA.zIndex + (GameScreen.SCREEN_H - gridPositionComponentA.coord().y)*5) - (gridPositionComponentB.zIndex + (GameScreen.SCREEN_H - gridPositionComponentB.coord().y)*5)
						);
			}
		};
		
		this.batch = batch;
		this.cam = camera;
		this.room = room;
		
		fullBackground = new Image(Assets.menuBackground.getRegion());
		fullBackground.setPosition(0, 0);
		fullBackground.addAction(Actions.alpha(0.5f));
	}
	
	@Override
	public void enterRoom(Room newRoom) {
		this.room = newRoom;
	}

	public void render(float deltaTime) {
		
		if (room.getState().updateNeeded()) {
			renderQueue = room.getAllEntities();
			renderQueue.sort(comparator);
		}
		
		
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		
		// draw the grid in the background
		this.room.floor.getGrid().draw(batch);
		
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
				
					Sprite sprite = spriteCompo.getSprite();
					if (sprite.getTexture() == null) continue;
					
					if (spriteCompo.flipX && !spriteCompo.getSprite().isFlipX()) {
						spriteCompo.getSprite().setFlip(true, false); 
					} else if (!spriteCompo.flipX && spriteCompo.getSprite().isFlipX()) {
						spriteCompo.getSprite().setFlip(false, false); 
					}	

					sprite.setPosition(x + GameScreen.GRID_SIZE/2 - sprite.getWidth()/2, y);
					
					if (!spriteCompo.hide) {
						spriteCompo.getSprite().draw(batch);
					}
				}
				if (textCompo != null && textCompo.getFont() != null) {					
					textCompo.getFont().draw(batch, textCompo.getText(), gridPosComponent.getAbsolutePos().x, gridPosComponent.getAbsolutePos().y);
				}
			} else {
				// use grid position to render instead of real screen coordinates
				
				Vector2 realPos = gridPosComponent.getWorldPos();
				if (spriteCompo != null && spriteCompo.getSprite() != null) {
					Sprite sprite = spriteCompo.getSprite();
					if (sprite.getTexture() == null) continue;
					
					if (spriteCompo.flipX && !spriteCompo.getSprite().isFlipX()) {
						sprite.setFlip(true, false); 
					} else if (!spriteCompo.flipX && spriteCompo.getSprite().isFlipX()) {
						sprite.setFlip(false, false); 
					}
					sprite.setPosition(realPos.x + GameScreen.GRID_SIZE/2 - sprite.getWidth()/2, realPos.y);


					if (!spriteCompo.hide) {
						sprite.draw(batch);
					}
				}
				if (textCompo != null && textCompo.getFont() != null) {
					textCompo.getFont().draw(batch, textCompo.getText(), realPos.x, realPos.y + textCompo.getHeight());
				}

			} 
		
		}
		
		batch.end();
		
		stage.act(deltaTime);
		stage.draw();

	}
	
	public static void showBlackFilter() {
		if (fullBackground != null && stage != null) {
			stage.addActor(fullBackground);
		}
	}
	
	public static void hideBlackFilter() {
		if (fullBackground != null) {
			fullBackground.remove();
		}
	}
	
	public OrthographicCamera getCamera() {
		return cam;
	}
}
