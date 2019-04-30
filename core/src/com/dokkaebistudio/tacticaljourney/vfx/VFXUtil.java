package com.dokkaebistudio.tacticaljourney.vfx;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.ColorAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.util.AnimatedImage;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class VFXUtil {
	
	
	public static void createDisappearanceEffect(Vector2 gridPos, Sprite sprite) {
		
		final Image image = new Image(sprite);
		Action removeImageAction = new Action(){
		  @Override
		  public boolean act(float delta){
			  image.remove();
		    return true;
		  }
		};
		PoolableVector2 pixelPos = TileUtil.convertGridPosIntoPixelPos(gridPos);
		image.setPosition(pixelPos.x, pixelPos.y);
		pixelPos.free();

		image.setOrigin(Align.center);
		
		ColorAction color = Actions.color(Color.GRAY, 0.5f);
		MoveByAction moveBy = Actions.moveBy(0, 50, 0.5f);
		AlphaAction fadeOut = Actions.fadeOut(0.5f);
		image.addAction(Actions.sequence(Actions.parallel(color, moveBy, fadeOut), removeImageAction));
		
		GameScreen.fxStage.addActor(image);		
	}
	
	public static void createDeathEffect(Entity entity) {
		SpriteComponent spriteComponent = Mappers.spriteComponent.get(entity);
		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(entity);
		
		if (spriteComponent.flipX && !spriteComponent.getSprite().isFlipX()) {
			spriteComponent.getSprite().setFlip(true, false); 
		} else if (!spriteComponent.flipX && spriteComponent.getSprite().isFlipX()) {
			spriteComponent.getSprite().setFlip(false, false); 
		}	
		
		final Image image = new Image(spriteComponent.getSprite());
		Action removeImageAction = new Action(){
		  @Override
		  public boolean act(float delta){
			  image.remove();
		    return true;
		  }
		};
		PoolableVector2 pixelPos = TileUtil.convertGridPosIntoPixelPos(gridPositionComponent.coord());
		image.setPosition(pixelPos.x, pixelPos.y);
		pixelPos.free();

		image.setOrigin(Align.center);
		
		ColorAction color = Actions.color(Color.GRAY, 0.5f);
		MoveByAction moveBy = Actions.moveBy(0, 500, 2f, Interpolation.pow3In);
		ScaleToAction scaleTo = Actions.scaleTo(0.5f, 3f, 1.5f, Interpolation.pow3In);
		AlphaAction disappearAlpha = Actions.alpha(0f, 2f);
		image.addAction(Actions.sequence(Actions.parallel(color, moveBy, scaleTo, disappearAlpha), removeImageAction));
		
		GameScreen.fxStage.addActor(image);		
	}
	

	/**
	 * Log effect when evading.
	 * @param gridPos the tile pos
	 */
	public static void createLogEffect(Vector2 gridPos) {
		final Image log = new Image(Assets.kawarimi_log.getRegion());
		Action removeImageAction = new Action(){
		  @Override
		  public boolean act(float delta){
			  log.remove();
		    return true;
		  }
		};
		PoolableVector2 pixelPos = TileUtil.convertGridPosIntoPixelPos(gridPos);
		log.setPosition(pixelPos.x, pixelPos.y);
		pixelPos.free();
		
		
		log.setOrigin(Align.center);
		ScaleToAction init = Actions.scaleTo(0, 0);
		DelayAction delayInit = Actions.delay( 0.1f);
		ScaleToAction appear = Actions.scaleTo(0.8f, 0.8f, 0.7f, Interpolation.elasticOut);
		AlphaAction disappearAlpha = Actions.alpha(0f, 0.7f);
		log.addAction(Actions.sequence(init, delayInit, appear, disappearAlpha, removeImageAction));
		
		GameScreen.fxStage.addActor(log);
	}
	
	/**
	 * Smoke effect when evading.
	 * @param gridPos the tile pos
	 */
	public static void createSmokeEffect(Vector2 gridPos) {
		final AnimatedImage smokeAnim = new AnimatedImage(AnimationSingleton.getInstance().smoke_bomb, false);
		Action smokeAnimFinishAction = new Action(){
		  @Override
		  public boolean act(float delta){
			smokeAnim.remove();
		    return true;
		  }
		};
		smokeAnim.setFinishAction(smokeAnimFinishAction);
		
		PoolableVector2 pixelPos = TileUtil.convertGridPosIntoPixelPos(gridPos);
		smokeAnim.setPosition(pixelPos.x + GameScreen.GRID_SIZE/2 - smokeAnim.getWidth()/2, pixelPos.y + GameScreen.GRID_SIZE/2 - smokeAnim.getHeight()/2);
		pixelPos.free();
		
		
		GameScreen.fxStage.addActor(smokeAnim);
	}
	
}
