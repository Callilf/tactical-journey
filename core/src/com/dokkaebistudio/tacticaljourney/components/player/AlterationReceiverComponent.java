package com.dokkaebistudio.tacticaljourney.components.player;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.alterations.Alteration;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;
import com.dokkaebistudio.tacticaljourney.room.Room;
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
	private Image blessingImage;
	
	public Image setBlessingImage(AtlasRegion texture, Vector2 startGridPos) {
		final Image arrow = new Image(texture);
		Vector2 playerPixelPos = TileUtil.convertGridPosIntoPixelPos(startGridPos);
		arrow.setPosition(playerPixelPos.x, playerPixelPos.y + 60);
				
		Action removeImageAction = new Action(){
			  @Override
			  public boolean act(float delta){
				  arrow.remove();
				  return true;
			  }
		};
	
		arrow.setOrigin(Align.center);
		
		ScaleToAction init = Actions.scaleTo(0, 0);
		ScaleToAction appear = Actions.scaleTo(1, 1, 1f);
		RotateByAction rotate = Actions.rotateBy(3600, 2f, Interpolation.exp5Out);
		AlphaAction disappear = Actions.alpha(0, 2f);
		
		ParallelAction appearance = Actions.parallel(appear, rotate);
		ParallelAction disappearance = Actions.parallel(disappear, rotate);
		arrow.addAction(Actions.sequence(init, appearance, disappearance, removeImageAction));
			
		this.blessingImage = arrow;
		return this.blessingImage;
	}
	
	
	
	@Override
	public void reset() {
		blessings.clear();
		curses.clear();
	}

	
	public void addBlessing(Entity entity, Blessing blessing) {
		blessing.onReceive(entity);
		blessings.add(blessing);
	}
	
	public void addCurse(Entity entity, Curse curse) {
		curse.onReceive(entity);
		curses.add(curse);
	}
	
	public void removeBlessing(Entity entity, Blessing blessing) {
		blessing.onRemove(entity);
		blessings.remove(blessing);
	}
	
	public void removeCurse(Entity entity, Curse curse) {
		curse.onRemove(entity);
		curses.remove(curse);
	}
	
	public void removeCurseByClass(Entity entity, Class curseClass) {
		for (Curse curse : curses) {
			if (curse.getClass() == curseClass) {
				removeCurse(entity, curse);
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
