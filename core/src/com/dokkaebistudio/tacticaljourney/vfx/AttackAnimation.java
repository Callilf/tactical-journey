package com.dokkaebistudio.tacticaljourney.vfx;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.movements.AttackTypeEnum;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.singletons.AnimationSingleton;
import com.dokkaebistudio.tacticaljourney.util.ActionsUtil;
import com.dokkaebistudio.tacticaljourney.util.AnimatedImage;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.dokkaebistudio.tacticaljourney.wheel.Sector;
import com.dokkaebistudio.tacticaljourney.wheel.Sector.Hit;

public class AttackAnimation {
	private boolean hasAnim;

	private Integer attackAnimIndex = -1;
	private Integer criticalAttackAnimIndex = -1;
	
	private Animation<Sprite> staticAttackAnim;
	
	private AnimatedImage animatedImage;

	private boolean oriented;
	
	private boolean playing;
	
	
	
	private AnimatedImage attackImage;
	
	
	public AttackAnimation(Animation<Sprite> attackAnim, boolean oriented) {
		this(attackAnim, null, oriented);
	}
	

	public AttackAnimation(Animation<Sprite> attackAnim, Animation<Sprite> critAnim, boolean oriented) {
		this.attackAnimIndex = AnimationSingleton.getInstance().getIndex(attackAnim);
		this.criticalAttackAnimIndex = AnimationSingleton.getInstance().getIndex(critAnim);
		this.oriented = oriented;
		
		if (attackAnim != null) {
			this.hasAnim = true;
		}
		this.playing = false;
	}
	
	
	
	
	/**
	 * Set the attack animation to use.
	 * @param texture the texture to use
	 * @param startGridPos the start pos (the attacker pos)
	 * @param finishAttackAction the action to call after the movement is over
	 */
	public boolean setAttackImage(AttackTypeEnum attackType, Vector2 startGridPos, Tile targetedTile, Sector pointedSector, Stage fxStage, Action finishAttackAction) {
		if (!this.hasAnim()) return false;
		
		Animation<Sprite> anim = this.getAnim(pointedSector);
		
		if (attackType == AttackTypeEnum.MELEE) {
			attackImage = new AnimatedImage(anim, false, finishAttackAction);
			Vector2 playerPixelPos = TileUtil.convertGridPosIntoPixelPos(startGridPos);
			Vector2 targetPosInPixel = targetedTile.getAbsolutePos();
			attackImage.setPosition(targetPosInPixel.x, targetPosInPixel.y);
			
			if (this.isOriented()) {
				double degrees = Math.atan2(
						targetPosInPixel.y - playerPixelPos.y,
					    targetPosInPixel.x - playerPixelPos.x
					) * 180.0d / Math.PI;
				attackImage.setOrigin(Align.center);
				attackImage.setRotation((float) degrees);
			}
			
			

			
		} else {
			// Range
			attackImage = new AnimatedImage(anim, true, null);
			Vector2 playerPixelPos = TileUtil.convertGridPosIntoPixelPos(startGridPos);
			attackImage.setPosition(playerPixelPos.x, playerPixelPos.y);
			
			Vector2 targetPosInPixel = targetedTile.getAbsolutePos();
			attackImage.setOrigin(Align.center);

			if (this.isOriented()) {
				double degrees = Math.atan2(
						targetPosInPixel.y - playerPixelPos.y,
					    targetPosInPixel.x - playerPixelPos.x
					) * 180.0d / Math.PI;
				attackImage.setRotation((float) degrees);
			}
			
			
			double distance = Math.hypot(playerPixelPos.x-targetPosInPixel.x, playerPixelPos.y-targetPosInPixel.y);
			double nbTiles = Math.ceil(distance / GameScreen.GRID_SIZE);
			float duration = (float) (nbTiles * 0.1f);
			
			if (this.isOriented()) {
				ActionsUtil.move(attackImage, targetPosInPixel, duration, finishAttackAction);
			} else {
				float rotation = (float) (nbTiles * 90);
				ActionsUtil.moveAndRotate(attackImage, targetPosInPixel, rotation, duration, finishAttackAction);
			}

		}
		
		this.setPlaying(true);
		fxStage.addActor(attackImage);
		return true;
	}
	

	
	
	
	/**
	 * Return the animation to use.
	 * @param pointedSector the sector of the wheel
	 * @return the animation to use.
	 */
	public Animation<Sprite> getAnim(Sector pointedSector) {
		if (staticAttackAnim != null) return staticAttackAnim;
		
		if (pointedSector != null && pointedSector.hit == Hit.CRITICAL) {
			return AnimationSingleton.getInstance().getAnimation(this.criticalAttackAnimIndex);
		} else {
			return AnimationSingleton.getInstance().getAnimation(this.attackAnimIndex);
		}
	}
	
	
	public void clear() {
		this.setPlaying(false);
		if (this.attackImage != null) {
			this.attackImage.remove();
		}
	}
	
	//************************
	// Getters and setters
	
	public Integer getAttackAnim() {
		return attackAnimIndex;
	}
	
	public void setAttackAnim(Animation<Sprite> attackAnim) {
		this.attackAnimIndex = AnimationSingleton.getInstance().getIndex(attackAnim);
	}
	
	public void setAttackAnim(TextureRegion region) {
		Array<Sprite> array = new Array<Sprite>();
		array.add(new Sprite(region));
		this.staticAttackAnim = new Animation<>(0.1f, array);
		
		this.hasAnim = true;
	}

	public Integer getCriticalAttackAnim() {
		return criticalAttackAnimIndex;
	}

	public void setCriticalAttackAnim(Animation<Sprite> criticalAttackAnim) {
		this.criticalAttackAnimIndex = AnimationSingleton.getInstance().getIndex(criticalAttackAnim);
	}

	public AnimatedImage getAnimatedImage() {
		return animatedImage;
	}

	public void setAnimatedImage(AnimatedImage animatedImage) {
		this.animatedImage = animatedImage;
	}

	public boolean isOriented() {
		return oriented;
	}

	public void setOriented(boolean oriented) {
		this.oriented = oriented;
	}


	public boolean isPlaying() {
		return playing;
	}


	public void setPlaying(boolean playing) {
		this.playing = playing;
	}


	public boolean hasAnim() {
		return hasAnim;
	}


	public void setHasAnim(boolean hasAnim) {
		this.hasAnim = hasAnim;
	}
}
