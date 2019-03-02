package com.dokkaebistudio.tacticaljourney.components.player;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.alterations.Alteration;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.rendering.HUDRenderer;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;

/**
 * Marker to indicate that this entity can receive blessings and curses.
 * @author Callil
 *
 */
public class AlterationReceiverComponent implements Component, Poolable {
	
	/** The list of blessings already received. */
	private List<Blessing> blessings = new ArrayList<>();
	
	/** The list of curses already received. */
	private List<Curse> curses = new ArrayList<>();
	
	
	private AlterationActionEnum currentAction;
	private Alteration currentAlteration;

	public enum AlterationActionEnum {
		RECEIVE_BLESSING,
		REMOVE_BLESSING,
		RECEIVE_CURSE,
		REMOVE_CURSE;
	}
	
	
	//*************
	// Events
    
	public void onRoomCleared(Entity entity, Room room) {
		for (Blessing b : blessings) {
			b.onRoomCleared(entity, room);
		}
		for (Curse c : curses) {
			c.onRoomCleared(entity, room);
		}
	}
	
	
	
	//*************
	// Animations
	
	private void setReceiveAnimation(AtlasRegion texture, Vector2 startGridPos, Stage fxStage) {
		final Image alterationImg = new Image(texture);
		Vector2 playerPixelPos = TileUtil.convertGridPosIntoPixelPos(startGridPos);
		alterationImg.setPosition(playerPixelPos.x, playerPixelPos.y + 60);
				
		Action removeImageAction = new Action(){
			  @Override
			  public boolean act(float delta){
				  alterationImg.remove();
				  return true;
			  }
		};
	
		alterationImg.setOrigin(Align.center);
		
		ScaleToAction init = Actions.scaleTo(0, 0);
		ScaleToAction appear = Actions.scaleTo(1, 1, 0.5f);
		RotateByAction rotate = Actions.rotateBy(3600, 1.5f, Interpolation.exp5Out);
		MoveToAction moveTo = Actions.moveTo(HUDRenderer.POS_PROFILE.x, HUDRenderer.POS_PROFILE.y, 1f, Interpolation.circle);
		ScaleToAction disappearByScaling = Actions.scaleTo(0, 0, 2f);
		
		ParallelAction appearance = Actions.parallel(appear, rotate);
		ParallelAction disappearance = Actions.parallel(moveTo, disappearByScaling);
		alterationImg.addAction(Actions.sequence(init, appearance, disappearance, removeImageAction));
		
		fxStage.addActor(alterationImg);
	}
	
	
	private void setRemoveAnimation(AtlasRegion texture, Vector2 startGridPos, Stage fxStage) {
		final Image alterationImg = new Image(texture);
		Vector2 playerPixelPos = TileUtil.convertGridPosIntoPixelPos(startGridPos);
		alterationImg.setPosition(playerPixelPos.x, playerPixelPos.y);
				
		Action removeImageAction = new Action(){
			  @Override
			  public boolean act(float delta){
				  alterationImg.remove();
				  return true;
			  }
		};
	
		alterationImg.setOrigin(Align.center);
		
		ScaleToAction init = Actions.scaleTo(0, 0);

		MoveToAction moveFromPlayer = Actions.moveTo(playerPixelPos.x, playerPixelPos.y + 60, 0.5f);
		ScaleToAction appear = Actions.scaleTo(1, 1, 0.5f);
		AlphaAction disappear = Actions.alpha(0, 1f);
		
		ParallelAction appearance = Actions.parallel(appear, moveFromPlayer);
		alterationImg.addAction(Actions.sequence(init, appearance, disappear, removeImageAction));
		
		fxStage.addActor(alterationImg);
	}
	
	
	
	@Override
	public void reset() {
		blessings.clear();
		curses.clear();
	}

	
	public void addBlessing(Entity entity, Blessing blessing, Stage fxStage) {
		blessing.onReceive(entity);
		blessings.add(blessing);
		
		if (fxStage != null) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(entity);
			this.setReceiveAnimation(blessing.texture(), gridPositionComponent.coord(), fxStage);
		}
	}
	
	public void addCurse(Entity entity, Curse curse, Stage fxStage) {
		curse.onReceive(entity);
		curses.add(curse);
		
		
		if (fxStage != null) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(entity);
			this.setReceiveAnimation(curse.texture(), gridPositionComponent.coord(), fxStage);
		}
	}
	
	public void removeBlessing(Entity entity, Blessing blessing, Stage fxStage) {
		blessing.onRemove(entity);
		blessings.remove(blessing);
		
		if (fxStage != null) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(entity);
			this.setRemoveAnimation(blessing.texture(), gridPositionComponent.coord(), fxStage);
		}
	}
	
	public void removeCurse(Entity entity, Curse curse, Stage fxStage) {
		curse.onRemove(entity);
		curses.remove(curse);
		
		if (fxStage != null) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(entity);
			this.setRemoveAnimation(curse.texture(), gridPositionComponent.coord(), fxStage);
		}
	}
	
	public void removeCurseByClass(Entity entity, Class curseClass, Stage fxStage) {
		for (Curse curse : curses) {
			if (curse.getClass() == curseClass) {
				removeCurse(entity, curse, fxStage);
				break;
			}
		}
	}
	
	
	
	
	//*****************
	// Requests
	
	public void requestAction(AlterationActionEnum action, Alteration alteration) {
		this.currentAction = action;
		this.currentAlteration = alteration;
	}
	
	public void requestRemoveBlessingByClass(Class alterationClass) {
		for (Blessing blessing : blessings) {
			if (blessing.getClass().equals(alterationClass)) {
				this.currentAlteration = blessing;
				this.currentAction = AlterationActionEnum.REMOVE_BLESSING;
				return;
			}
		}
	}
	
	public void requestRemoveCurseByClass(Class alterationClass) {
		for (Curse curse : curses) {
			if (curse.getClass().equals(alterationClass)) {
				this.currentAlteration = curse;
				this.currentAction = AlterationActionEnum.RECEIVE_CURSE;
				return;
			}
		}
	}
	
	public void clearCurrentAction() {
		this.currentAction = null;
		this.currentAlteration = null;
	}
	
	
	//***********************
	// Getters and Setters
	
	
	public List<Blessing> getBlessings() {
		return blessings;
	}

	public void setBlessings(List<Blessing> blessings) {
		this.blessings = blessings;
	}

	public List<Curse> getCurses() {
		return curses;
	}

	public void setCurses(List<Curse> curses) {
		this.curses = curses;
	}



	public AlterationActionEnum getCurrentAction() {
		return currentAction;
	}



	public void setCurrentAction(AlterationActionEnum currentAction) {
		this.currentAction = currentAction;
	}



	public Alteration getCurrentAlteration() {
		return currentAlteration;
	}



	public void setCurrentAlteration(Alteration currentAlteration) {
		this.currentAlteration = currentAlteration;
	}

	
	
}
