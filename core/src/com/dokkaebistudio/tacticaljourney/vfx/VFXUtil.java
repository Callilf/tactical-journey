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
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.display.SpriteComponent;
import com.dokkaebistudio.tacticaljourney.enums.HealthChangeEnum;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.rendering.service.PopinService;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.util.AnimatedImage;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class VFXUtil {
	
//	private static int damageDisplayerXOffset;
	
	
	public static void createStatsUpNotif(String text, Vector2 gridPos) {
		createStatsUpNotif(text, "BLACK", gridPos);
	}
	
	public static void createStatsUpNotif(String text, String color, Vector2 gridPos) {
		final Label image = new Label("[" + color + "]" + text, PopinService.hudStyle());
		final Container<Label> container = new Container<>(image);
	    container.setTransform(true);   // for enabling scaling and rotation
	    container.pack();
	    
		Action removeImageAction = new Action(){
		  @Override
		  public boolean act(float delta){
			container.remove();
		    return true;
		  }
		};
		
		PoolableVector2 pixelPos = TileUtil.convertGridPosIntoPixelPos(gridPos);
		container.setPosition(pixelPos.x + GameScreen.GRID_SIZE/2 - container.getWidth()/2, pixelPos.y + GameScreen.GRID_SIZE/2 - container.getHeight()/2);
		pixelPos.free();

		container.setOrigin(Align.center);
				
		ScaleToAction appear = Actions.scaleTo(1.5f, 1.5f, 1f, Interpolation.elasticOut);
		AlphaAction disappear = Actions.fadeOut(2f);
		
		container.addAction(Actions.sequence(Actions.scaleTo(0, 0),  appear, disappear, removeImageAction));
		
		GameScreen.fxStage.addActor(container);		
	}
	
	/**
	 * Create a damage displayer.
	 * @param damage the damage to display
	 * @param gridPos the grid position
	 * @param healthChange the type of health modif (impact the text color)
	 * @param offsetY the offset in case of many displayers being displayed at the same time
	 * @param room the room
	 * @return the damage displayer entity
	 */
	public static void createDamageDisplayer(String damage, Vector2 gridPos, HealthChangeEnum healthChange, float offsetY, Room room) {
		String color = "";
		switch(healthChange) {
		case HEALED:
			color = "[GREEN]";
			break;
		case HIT:
		case HIT_INTERRUPT:
			color = "[RED]";
			break;
		case ARMOR:
			color = "[BLUE]";
			break;
		case RESISTANT:
			color = "[BLACK]";
			break;
			default:
				color = "[WHITE]";

		}		
		

		final Label image = new Label(color + damage, PopinService.hudStyle());
		final Container<Label> container = new Container<>(image);
	    container.setTransform(true);   // for enabling scaling and rotation
	    container.pack();
	    
		Action removeImageAction = new Action(){
		  @Override
		  public boolean act(float delta){
			container.remove();
		    return true;
		  }
		};
		
		PoolableVector2 pixelPos = TileUtil.convertGridPosIntoPixelPos(gridPos);
		container.setPosition(pixelPos.x + GameScreen.GRID_SIZE - container.getHeight(), pixelPos.y + GameScreen.GRID_SIZE/2 - offsetY);
		pixelPos.free();

		container.setOrigin(Align.center);
				
		ScaleToAction appear = Actions.scaleTo(1.5f, 1.5f, 1f, Interpolation.elasticOut);
		ScaleToAction scale = Actions.scaleTo(0f, 0f, 2f);
		SequenceAction scalingActions = Actions.sequence(appear,scale);
		
		MoveByAction moveBy = Actions.moveBy(0, 150, 3f);
		container.addAction(Actions.sequence(Actions.scaleTo(0, 0), Actions.parallel(scalingActions, moveBy), removeImageAction));
		
		GameScreen.fxStage.addActor(container);		
	}
	
	
	public static void createExperienceDisplayer(int exp, Vector2 gridPos, float offsetY, Room room) {
		final Label image = new Label("[YELLOW]EXP+" + exp, PopinService.hudStyle());
		Action removeImageAction = new Action(){
		  @Override
		  public boolean act(float delta){
			  image.remove();
		    return true;
		  }
		};
		
		PoolableVector2 pixelPos = TileUtil.convertGridPosIntoPixelPos(gridPos);
		image.setPosition(pixelPos.x, pixelPos.y + GameScreen.GRID_SIZE - offsetY);
		pixelPos.free();

		image.setOrigin(Align.center);
		
		MoveByAction moveBy = Actions.moveBy(0, 100, 2f);
		image.addAction(Actions.sequence(moveBy, removeImageAction));
		
		GameScreen.fxStage.addActor(image);		
	}
	
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
		createSmokeEffect(gridPos, null);
	}
	
	/**
	 * Smoke effect when evading.
	 * @param gridPos the tile pos
	 */
	public static void createSmokeEffect(Vector2 gridPos, Action smokeAnimFinishAction) {
		final AnimatedImage smokeAnim = new AnimatedImage(AnimationSingleton.getInstance().smoke_bomb, false);
		smokeAnim.setFinishAction(smokeAnimFinishAction);
		
		PoolableVector2 pixelPos = TileUtil.convertGridPosIntoPixelPos(gridPos);
		smokeAnim.setPosition(pixelPos.x + GameScreen.GRID_SIZE/2 - smokeAnim.getWidth()/2, pixelPos.y + GameScreen.GRID_SIZE/2 - smokeAnim.getHeight()/2);
		pixelPos.free();
		
		
		GameScreen.fxStage.addActor(smokeAnim);
	}
	

	
}
