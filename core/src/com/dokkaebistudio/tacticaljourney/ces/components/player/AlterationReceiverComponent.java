package com.dokkaebistudio.tacticaljourney.ces.components.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
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
import com.dokkaebistudio.tacticaljourney.ces.components.attack.AttackComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.item.ItemComponent;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.items.infusableItems.AbstractInfusableItem;
import com.dokkaebistudio.tacticaljourney.rendering.HUDRenderer;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.room.Tile;
import com.dokkaebistudio.tacticaljourney.statuses.Status;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.dokkaebistudio.tacticaljourney.wheel.AttackWheel;
import com.dokkaebistudio.tacticaljourney.wheel.Sector;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity can receive blessings and curses.
 * @author Callil
 *
 */
public class AlterationReceiverComponent implements Component, Poolable {
	
	public Comparator<Alteration> comparator = new Comparator<Alteration>() {
		public int compare(Alteration o1, Alteration o2) {
			if (o1.isInfused() == o2.isInfused()) return 0;
			if (o1.isInfused() && !o2.isInfused()) return -1;
			if (!o1.isInfused() && o2.isInfused()) return 1;
			return 0;
		}
	};
	
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
		REMOVE_CURSE,
		LIFT_CURSE;
	}
	
	
	//*************
	// Events
	
	public void onAttack(Entity attacker, Entity target, Sector sector, AttackComponent attackCompo, Room room) {
		for (Blessing b : blessings) {
			b.onAttack(attacker, target, sector, attackCompo, room);
		}
		for (Curse c : curses) {
			c.onAttack(attacker, target, sector, attackCompo, room);
		}
	}
	
	public void onAttackEmptyTile(Entity attacker, Tile tile, AttackComponent attackCompo, Room room) {
		for (Blessing b : blessings) {
			b.onAttackEmptyTile(attacker, tile, attackCompo, room);
		}
		for (Curse c : curses) {
			c.onAttackEmptyTile(attacker, tile, attackCompo, room);
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
	
	public boolean onReceiveAttack(Entity user, Entity attacker, Room room) {
		boolean result = true;
		for (Blessing b : blessings) {
			result &= b.onReceiveAttack(user, attacker, room);
		}
		for (Curse c : curses) {
			result &= c.onReceiveAttack(user, attacker, room);
		}
		return result;
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
	
	public void onFloorVisited(Entity entity, Floor floor, Room room) {
		for (Blessing b : blessings) {
			b.onFloorVisited(entity, floor, room);
		}
		for (Curse c : curses) {
			c.onFloorVisited(entity, floor, room);
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
	
	
	public int onShopNumberOfItems(Entity entity, Entity shopkeeper, Room room) {
		int result = 0;
		for (Blessing b : blessings) {
			result += b.onShopNumberOfItems(entity, shopkeeper, room);
		}
		for (Curse c : curses) {
			result += c.onShopNumberOfItems(entity, shopkeeper, room);
		}
		return result;
	}
	
	public void onLevelUp(Entity entity, Room room) {
		for (Blessing b : blessings) {
			b.onLevelUp(entity, room);
		}
		for (Curse c : curses) {
			c.onLevelUp(entity, room);
		}
	}
	
	
	public void onPlayerTurnStarts(Entity entity, Room room) {
		for (Blessing b : blessings) {
			b.onPlayerTurnStarts(entity, room);
		}
		for (Curse c : curses) {
			c.onPlayerTurnStarts(entity, room);
		}
	}
	public void onPlayerTurnEnds(Entity entity, Room room) {
		for (Blessing b : blessings) {
			b.onPlayerTurnEnds(entity, room);
		}
		for (Curse c : curses) {
			c.onPlayerTurnEnds(entity, room);
		}
	}	
	
	public void onArriveOnTile(Vector2 gridPos, Entity mover, Room room) {
		for (Blessing b : blessings) {
			b.onArriveOnTile(gridPos, mover, room);
		}
		for (Curse c : curses) {
			c.onArriveOnTile(gridPos, mover, room);
		}
	}
	
	public boolean onReceiveStatusEffect(Entity entity, Status status, Room room) {
		boolean result = true;
		for (Blessing b : blessings) {
			result &= b.onReceiveStatusEffect(entity, status, room);
		}
		for (Curse c : curses) {
			result &= c.onReceiveStatusEffect(entity, status, room);
		}
		return result;
	}
	
	public void onRemoveStatusEffect(Entity entity, Status status, Room room) {
		for (Blessing b : blessings) {
			b.onRemoveStatusEffect(entity, status, room);
		}
		for (Curse c : curses) {
			c.onRemoveStatusEffect(entity, status, room);
		}
	}
	
	public int onPickupItem(Entity entity, Entity item, Room room) {
		int result = 0;
		for (Blessing b : blessings) {
			result += b.onPickupItem(entity, item, room);
		}
		for (Curse c : curses) {
			result += c.onPickupItem(entity, item, room);
		}
		return result;
	}

	public int onUseItem(Entity entity, Entity item, Room room) {
		int result = 0;
		for (Blessing b : blessings) {
			result += b.onUseItem(entity, item, room);
		}
		for (Curse c : curses) {
			result += c.onUseItem(entity, item, room);
		}
		return result;
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
			this.setReceiveAnimation(blessing.texture().getRegion(), gridPositionComponent.coord(), fxStage, offset);
		}
	}
	
	public void addCurse(Entity entity, Curse curse, Stage fxStage, int offset) {
		curse.onReceive(entity);
		curses.add(curse);
		
		
		if (fxStage != null) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(entity);
			this.setReceiveAnimation(curse.texture().getRegion(), gridPositionComponent.coord(), fxStage, offset);
		}
	}
	
	public void removeBlessing(Entity entity, Blessing blessing, Stage fxStage, int offset) {
		blessing.onRemove(entity);
		blessings.remove(blessing);
		
		if (fxStage != null) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(entity);
			this.setRemoveAnimation(blessing.texture().getRegion(), gridPositionComponent.coord(), fxStage, offset);
		}
	}
	
	public void removeCurse(Entity entity, Curse curse, Stage fxStage, int offset) {
		curse.onRemove(entity);		
		curses.remove(curse);
		
		if (fxStage != null) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(entity);
			this.setRemoveAnimation(curse.texture().getRegion(), gridPositionComponent.coord(), fxStage, offset);
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
	
	
	public void liftCurse(Entity entity, Curse curse, Stage fxStage, int offset) {
		curse.onRemove(entity);
		
		if (curse.getItemEntityId() != null) {
			Entity itemEntity = Mappers.inventoryComponent.get(GameScreen.player).findItemByEntityId(curse.getItemEntityId());
			
			ItemComponent itemComponent = Mappers.itemComponent.get(itemEntity);
			AbstractInfusableItem infusableItem = (AbstractInfusableItem) itemComponent.getItemType();
			infusableItem.removeCurse(curse);
		}
		
		curses.remove(curse);
		
		if (fxStage != null) {
			GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(entity);
			this.setRemoveAnimation(curse.texture().getRegion(), gridPositionComponent.coord(), fxStage, offset);
		}
	}
	
	/**
	 * Checks whether the entity has the given blessing.
	 * @param blessingClass the blessing class to test
	 * @return true if the entity has this blessing
	 */
	public Blessing getBlessing(Class blessingClass) {
		for (Blessing b : this.blessings) {
			if (b.getClass().equals(blessingClass)) {
				return b;
			}
		}
		return null;
	}
	
	/**
	 * Checks whether the entity has the given blessing.
	 * @param blessingClass the blessing class to test
	 * @return true if the entity has this blessing
	 */
	public boolean hasBlessing(Class blessingClass) {
		for (Blessing b : this.blessings) {
			if (b.getClass().equals(blessingClass)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks whether the entity has the given curse.
	 * @param curseClass the curse class to test
	 * @return true if the entity has this curse
	 */
	public boolean hasCurse(Class curseClass) {
		for (Curse c : this.curses) {
			if (c.getClass().equals(curseClass)) {
				return true;
			}
		}
		return false;
	}
	
	public void sort() {
		Collections.sort(blessings, comparator);
		Collections.sort(curses, comparator);
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
	
	
	
	
	public static Serializer<AlterationReceiverComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<AlterationReceiverComponent>() {

			@Override
			public void write(Kryo kryo, Output output, AlterationReceiverComponent object) {
				kryo.writeClassAndObject(output, object.blessings);
				kryo.writeClassAndObject(output, object.curses);
			}

			@Override
			public AlterationReceiverComponent read(Kryo kryo, Input input, Class<? extends AlterationReceiverComponent> type) {
				AlterationReceiverComponent compo = engine.createComponent(AlterationReceiverComponent.class);
				compo.blessings = (List<Blessing>) kryo.readClassAndObject(input);
				compo.curses = (List<Curse>) kryo.readClassAndObject(input);
				return compo;
			}
		
		};
	}
	
}
