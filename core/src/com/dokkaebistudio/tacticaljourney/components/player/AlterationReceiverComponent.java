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
import com.dokkaebistudio.tacticaljourney.wheel.AttackWheel;
import com.dokkaebistudio.tacticaljourney.wheel.Sector;

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
	
	
	private List<AlterationActionEnum> currentActions = new ArrayList<>();
	private List<Alteration> currentAlterations = new ArrayList<>();

	public enum AlterationActionEnum {
		RECEIVE_BLESSING,
		REMOVE_BLESSING,
		RECEIVE_CURSE,
		REMOVE_CURSE;
	}
	
	
	//*************
	// Events
	
	public void onAttack(Entity attacker, Entity target, Sector sector, Room room) {
		for (Blessing b : blessings) {
			b.onAttack(attacker, target, sector, room);
		}
		for (Curse c : curses) {
			c.onAttack(attacker, target, sector, room);
		}
	}
	
	public void onKill(Entity attacker, Entity target, Room room) {
		for (Blessing b : blessings) {
			b.onKill(attacker, target, room);
		}
		for (Curse c : curses) {
			c.onKill(attacker, target, room);
		}
	}
	
	public void onReceiveDamage(Entity user, Entity attacker, Room room) {
		for (Blessing b : blessings) {
			b.onReceiveDamage(user, attacker, room);
		}
		for (Curse c : curses) {
			c.onReceiveDamage(user, attacker, room);
		}
	}
	
	public void onDeath(Entity user, Entity attacker, Room room) {
		for (Blessing b : blessings) {
			b.onDeath(user, attacker, room);
		}
		for (Curse c : curses) {
			c.onDeath(user, attacker, room);
		}
	}
    
	public void onRoomVisited(Entity entity, Room room) {
		for (Blessing b : blessings) {
			b.onRoomVisited(entity, room);
		}
		for (Curse c : curses) {
			c.onRoomVisited(entity, room);
		}
	}
	
	public void onRoomCleared(Entity entity, Room room) {
		for (Blessing b : blessings) {
			b.onRoomCleared(entity, room);
		}
		for (Curse c : curses) {
			c.onRoomCleared(entity, room);
		}
	}
	
	
	public void onModifyWheelSectors(AttackWheel wheel, Entity entity, Room room) {
		for (Blessing b : blessings) {
			b.onModifyWheelSectors(wheel, entity, room);
		}
		for (Curse c : curses) {
			c.onModifyWheelSectors(wheel, entity, room);
		}
	}
	
	
	
	//*************
	// Animations
	
	private void setReceiveAnimation(AtlasRegion texture, Vector2 startGridPos, Stage fxStage, int offset) {
		final Image alterationImg = new Image(texture);
		Vector2 playerPixelPos = TileUtil.convertGridPosIntoPixelPos(startGridPos);
		alterationImg.setPosition(HUDRenderer.POS_PROFILE.x, HUDRenderer.POS_PROFILE.y + 60 + (60*offset));
				
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
		ScaleToAction disappearByScaling = Actions.scaleTo(0, 0, 1f);
		
		ParallelAction appearance = Actions.parallel(appear, rotate);
		ParallelAction disappearance = Actions.parallel(moveTo, disappearByScaling);
		alterationImg.addAction(Actions.sequence(init, appearance, disappearance, removeImageAction));
		
		fxStage.addActor(alterationImg);
	}
	
	
	private void setRemoveAnimation(AtlasRegion texture, Vector2 startGridPos, Stage fxStage, int offset) {
		final Image alterationImg = new Image(texture);
		Vector2 playerPixelPos = TileUtil.convertGridPosIntoPixelPos(startGridPos);
		alterationImg.setPosition(HUDRenderer.POS_PROFILE.x, HUDRenderer.POS_PROFILE.y);
				
		Action removeImageAction = new Action(){
			  @Override
			  public boolean act(float delta){
				  alterationImg.remove();
				  return true;
			  }
		};
	
		alterationImg.setOrigin(Align.center);
		
		ScaleToAction init = Actions.scaleTo(0, 0);

		MoveToAction moveFromPlayer = Actions.moveTo(HUDRenderer.POS_PROFILE.x, HUDRenderer.POS_PROFILE.y + 60 + (60*offset), 0.5f);
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

	
	public void addBlessing(Entity entity, Blessing blessing, Stage fxStage, int offset) {
		blessing.onReceive(entity);
		blessings.add(blessing);
		
		if (fxStage != null) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(entity);
			this.setReceiveAnimation(blessing.texture(), gridPositionComponent.coord(), fxStage, offset);
		}
	}
	
	public void addCurse(Entity entity, Curse curse, Stage fxStage, int offset) {
		curse.onReceive(entity);
		curses.add(curse);
		
		
		if (fxStage != null) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(entity);
			this.setReceiveAnimation(curse.texture(), gridPositionComponent.coord(), fxStage, offset);
		}
	}
	
	public void removeBlessing(Entity entity, Blessing blessing, Stage fxStage, int offset) {
		blessing.onRemove(entity);
		blessings.remove(blessing);
		
		if (fxStage != null) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(entity);
			this.setRemoveAnimation(blessing.texture(), gridPositionComponent.coord(), fxStage, offset);
		}
	}
	
	public void removeCurse(Entity entity, Curse curse, Stage fxStage, int offset) {
		curse.onRemove(entity);
		curses.remove(curse);
		
		if (fxStage != null) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(entity);
			this.setRemoveAnimation(curse.texture(), gridPositionComponent.coord(), fxStage, offset);
		}
	}
	
	public void removeCurseByClass(Entity entity, Class curseClass, Stage fxStage, int offset) {
		for (Curse curse : curses) {
			if (curse.getClass() == curseClass) {
				removeCurse(entity, curse, fxStage, offset);
				break;
			}
		}
	}
	
	
	
	
	//*****************
	// Requests
	
	public void requestAction(AlterationActionEnum action, Alteration alteration) {
		this.currentActions.add(action);
		this.currentAlterations.add(alteration);
	}
	
	public void requestRemoveBlessingByClass(Class alterationClass) {
		for (Blessing blessing : blessings) {
			if (blessing.getClass().equals(alterationClass)) {
				this.currentActions.add(AlterationActionEnum.REMOVE_BLESSING);
				this.currentAlterations.add(blessing);
				return;
			}
		}
	}
	
	public void requestRemoveCurseByClass(Class alterationClass) {
		for (Curse curse : curses) {
			if (curse.getClass().equals(alterationClass)) {
				this.currentActions.add(AlterationActionEnum.REMOVE_CURSE);
				this.currentAlterations.add(curse);
				return;
			}
		}
	}
	
	public void clearCurrentAction() {
		this.currentActions.clear();
		this.currentAlterations.clear();
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


	public List<AlterationActionEnum> getCurrentActions() {
		return this.currentActions;
	}
	public List<Alteration> getCurrentAlterations() {
		return this.currentAlterations;
	}
	
	
}
