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
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.TextComponent;
import com.dokkaebistudio.tacticaljourney.ces.systems.RoomSystem;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.rendering.interfaces.Renderer;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;

public class RoomRenderer implements Renderer, RoomSystem {

	private static Stage stage;
	private SpriteBatch batch;
	private Comparator<Entity> comparator;
	private OrthographicCamera cam;
	private Array<Entity> renderQueue;
	
	private static Image blackOverlayForPopins;
	private static Image fadeoutBlack;

	
	/** The current room. */
	private Room room;
	
	public RoomRenderer(Stage s, SpriteBatch batch, Room room, OrthographicCamera camera) {
		this.stage = s;
		this.comparator = new Comparator<Entity>() {
			@Override
			public int compare(Entity entityA, Entity entityB) {
				GridPositionComponent gpcA = Mappers.gridPositionComponent.get(entityA);
				GridPositionComponent gpcB = Mappers.gridPositionComponent.get(entityB);
				if (gpcA == null && gpcB == null) return 0;
				else if (gpcA == null) return -1;
				else if (gpcB == null) return 1;
				
				// This complex computation ensure that big sprites on lower tiles appear in front of sprites on upper tiles
				return (int) Math.signum(
						(gpcA.zIndex + (GameScreen.SCREEN_H - gpcA.coord().y)*5*gpcA.overlap) - (gpcB.zIndex + (GameScreen.SCREEN_H - gpcB.coord().y)*5*gpcB.overlap)
						);
			}
		};
		
		this.batch = batch;
		this.cam = camera;
		this.room = room;
		
		blackOverlayForPopins = new Image(Assets.menuBackground.getRegion());
		blackOverlayForPopins.setPosition(0, 0);
		blackOverlayForPopins.addAction(Actions.alpha(0.5f));
		
		fadeoutBlack = new Image(Assets.menuBackground.getRegion());
		fadeoutBlack.setPosition(0, 0);
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
					textCompo.getFont().getFont().draw(batch, textCompo.getText(), gridPosComponent.getAbsolutePos().x, gridPosComponent.getAbsolutePos().y);
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

					if (gridPosComponent.getOrbitSpeed() != 0) {
						handleOrbit(sprite, gridPosComponent);
					}

					if (!spriteCompo.hide) {
						sprite.draw(batch);
					}
				}
				if (textCompo != null && textCompo.getFont() != null) {
					textCompo.getFont().getFont().draw(batch, textCompo.getText(), realPos.x, realPos.y + textCompo.getHeight());
				}

			} 
		
		}
		
		batch.end();
		
		stage.act(deltaTime);
		stage.draw();

	}

	private void handleOrbit(Sprite sprite, GridPositionComponent gridPosComponent) {
		  gridPosComponent.setOrbitCurrentPercentage((gridPosComponent.getOrbitCurrentPercentage() + gridPosComponent.getOrbitSpeed()) % 1);
		  
		  float angle = (float)(Math.PI*2*gridPosComponent.getOrbitCurrentPercentage());
		  sprite.setPosition(sprite.getX() + gridPosComponent.getOrbitRadius()*(float)Math.cos(angle), 
				  sprite.getY() + gridPosComponent.getOrbitRadius()*(float)Math.sin(angle));
	}
	
	public static void showBlackFilter() {
		if (blackOverlayForPopins != null && stage != null) {
			stage.addActor(blackOverlayForPopins);
		}
	}
	
	public static void hideBlackFilter() {
		if (blackOverlayForPopins != null) {
			blackOverlayForPopins.remove();
		}
	}
	
	public static void showFadeoutBlack() {
		if (fadeoutBlack != null && stage != null) {
			stage.addActor(fadeoutBlack);
			fadeoutBlack.addAction(Actions.alpha(1f));
			fadeoutBlack.addAction(Actions.sequence(Actions.alpha(0f, 2f, Interpolation.pow4In), new Action() {
				public boolean act(float delta) {
					fadeoutBlack.remove();
					return true;
				}
			}));
		}
	}
	
	public OrthographicCamera getCamera() {
		return cam;
	}
}
