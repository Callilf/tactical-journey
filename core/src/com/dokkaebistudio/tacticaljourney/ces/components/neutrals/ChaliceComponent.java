package com.dokkaebistudio.tacticaljourney.ces.components.neutrals;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;
import com.dokkaebistudio.tacticaljourney.ces.components.HealthComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.StatusReceiverComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.interfaces.MarkerInterface;
import com.dokkaebistudio.tacticaljourney.ces.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.ces.components.player.AlterationReceiverComponent.AlterationActionEnum;
import com.dokkaebistudio.tacticaljourney.gamescreen.GameScreen;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.rendering.MapRenderer;
import com.dokkaebistudio.tacticaljourney.room.Room;
import com.dokkaebistudio.tacticaljourney.statuses.debuffs.StatusDebuffPoison;
import com.dokkaebistudio.tacticaljourney.util.AnimatedImage;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.dokkaebistudio.tacticaljourney.vfx.VFXUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is a chalice.
 * @author Callil
 *
 */
public class ChaliceComponent implements Component, Poolable, MarkerInterface {
	
	public enum ChaliceType {
		LIFT_CURSE,
		FULL_HEAL,
		VISION
	}
	
	/** The type of chalice. */
	private ChaliceType type;
	
	/** Whether this chalice is filled or empty. */
	private boolean filled;
	
	/** The holy aura the statue has when the blessing hasn't been received yet. */
	private AnimatedImage aura;
	

	
	@Override
	public void reset() {
		setType(ChaliceType.LIFT_CURSE);
		setAura(null);
		setFilled(true);
	}
	
	@Override
	public void showMarker(Entity e) {
		if (this.getAura() != null) {
			GameScreen.fxStage.addActor(this.getAura());
		}
	}
	
	@Override
	public void hideMarker() {
		if (this.getAura() != null) {
			this.getAura().remove();
		}
	}
	
	
	public void drink(Room room) {
		room.pauseState();
		this.setFilled(false);

		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(GameScreen.player);
		PoolableVector2 playerPixelPos = TileUtil.convertGridPosIntoPixelPos(gridPositionComponent.coord());
		
		this.getAura().setOrigin(Align.center);
		ScaleToAction scale = Actions.scaleTo(0f, 0f, 2f);
		MoveToAction move = Actions.moveTo(playerPixelPos.x, playerPixelPos.y, 2f, Interpolation.pow5);
		this.getAura().addAction(Actions.sequence(Actions.parallel(scale, move), new Action() {
			@Override
			public boolean act(float delta) {
				removeAura();
				
				switch (type) {
				case LIFT_CURSE:
					AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(GameScreen.player);
					if (alterationReceiverComponent.getCurses().isEmpty()) {
						Journal.addEntry("[YELLOW]A holy fluid runed through your body. Since you did not have any curse, it did nothing else than making you feel good.");
					} else {
						int curseIndex = RandomSingleton.getInstance().getUnseededRandom().nextInt(alterationReceiverComponent.getCurses().size());
						Curse curse = alterationReceiverComponent.getCurses().get(curseIndex);
						
						alterationReceiverComponent.requestAction(AlterationActionEnum.LIFT_CURSE, curse);
						Journal.addEntry("[YELLOW]A holy fluid runed through your body. You feel that a burden has been lifted.");
						
						VFXUtil.createStatsUpNotif("Burden lifted", gridPositionComponent.coord());
					}
					break;
					
				case FULL_HEAL:
					HealthComponent healthComponent = Mappers.healthComponent.get(GameScreen.player);
					healthComponent.restoreHealth(healthComponent.getMaxHp() - healthComponent.getHp());
					
					StatusReceiverComponent statusReceiverComponent = Mappers.statusReceiverComponent.get(GameScreen.player);
					statusReceiverComponent.removeStatus(GameScreen.player, StatusDebuffPoison.class, room);
					
					Journal.addEntry("[GREEN]A revigorating fluid runed through your body and healed all your wounds.");
					break;
					
				case VISION:
					room.floor.getRooms().forEach(r -> r.setDisplayedOnMap(true));
					MapRenderer.requireRefresh();
					Journal.addEntry("[GREEN]A vivid image of the layout of the floor formed into your mind.");
					
					VFXUtil.createStatsUpNotif("Vision", gridPositionComponent.coord());

					break;
					
					default:
				}
				
				room.unpauseState();
				return true;
			}
		}));
	}
	
	//*********************************
	// Getters and Setters

	public AnimatedImage getAura() {
		return aura;
	}

	public void setAura(AnimatedImage aura) {
		this.aura = aura;
	}
	
	public void removeAura() {
		if (this.aura != null) {
			this.aura.remove();
			this.aura = null;
		}
	}
	
	public boolean isFilled() {
		return filled;
	}


	public void setFilled(boolean filled) {
		this.filled = filled;
	}
	
	public ChaliceType getType() {
		return type;
	}

	public void setType(ChaliceType type) {
		this.type = type;
	}


	
	
	public static Serializer<ChaliceComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<ChaliceComponent>() {

			@Override
			public void write(Kryo kryo, Output output, ChaliceComponent object) {
				output.writeString(object.type.name());
				output.writeBoolean(object.filled);
				kryo.writeClassAndObject(output, object.aura);
			}

			@Override
			public ChaliceComponent read(Kryo kryo, Input input, Class<? extends ChaliceComponent> type) {
				ChaliceComponent compo = engine.createComponent(ChaliceComponent.class);
				compo.type = ChaliceType.valueOf(input.readString());
				compo.filled = input.readBoolean();
				compo.setAura((AnimatedImage) kryo.readClassAndObject(input));
				return compo;
			}
		
		};
	}


	


}
