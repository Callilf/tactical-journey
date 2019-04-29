package com.dokkaebistudio.tacticaljourney.vfx;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.dokkaebistudio.tacticaljourney.Assets;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.util.AnimatedImage;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

public class VFXUtil {

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
