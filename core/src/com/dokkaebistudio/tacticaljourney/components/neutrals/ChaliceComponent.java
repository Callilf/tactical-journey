package com.dokkaebistudio.tacticaljourney.components.neutrals;

import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.ai.random.RandomSingleton;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing.BlessingsEnum;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;
import com.dokkaebistudio.tacticaljourney.alterations.Curse.CursesEnum;
import com.dokkaebistudio.tacticaljourney.alterations.pools.AlterationPool;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent.AlterationActionEnum;
import com.dokkaebistudio.tacticaljourney.journal.Journal;
import com.dokkaebistudio.tacticaljourney.systems.AlterationSystem;
import com.dokkaebistudio.tacticaljourney.util.AnimatedImage;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is a chalice.
 * @author Callil
 *
 */
public class ChaliceComponent implements Component, Poolable {
	/** Whether this chalice is filled or empty. */
	private boolean filled;
	
	/** The holy aura the statue has when the blessing hasn't been received yet. */
	private AnimatedImage aura;
	

	
	@Override
	public void reset() {
		setAura(null);
		setFilled(true);
	}
	
	
	public void drink() {
		this.setFilled(false);

		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(GameScreen.player);
		PoolableVector2 playerPixelPos = TileUtil.convertGridPosIntoPixelPos(gridPositionComponent.coord());
		
		ScaleToAction scale = Actions.scaleTo(0f, 0f, 2f);
		MoveToAction move = Actions.moveTo(playerPixelPos.x + GameScreen.GRID_SIZE/2, playerPixelPos.y + GameScreen.GRID_SIZE/2, 2f, Interpolation.pow5);
		this.getAura().addAction(Actions.sequence(Actions.parallel(scale, move), new Action() {
			@Override
			public boolean act(float delta) {
				removeAura();
				
				AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(GameScreen.player);
				if (alterationReceiverComponent.getCurses().isEmpty()) {
					Journal.addEntry("[YELLOW]A holy fluid runs through your body. Since you did not have any curse, it does nothing else that making you feel good.");
				} else {
					int curseIndex = RandomSingleton.getInstance().getUnseededRandom().nextInt(alterationReceiverComponent.getCurses().size());
					Curse curse = alterationReceiverComponent.getCurses().get(curseIndex);
					
					alterationReceiverComponent.requestAction(AlterationActionEnum.REMOVE_CURSE, curse);
					Journal.addEntry("[YELLOW]A holy fluid runs through your body. You feel that a burden has been lifted.");
				}

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
		if (aura != null) {
			GameScreen.fxStage.addActor(aura);
		}
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
	
	
	public static Serializer<ChaliceComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<ChaliceComponent>() {

			@Override
			public void write(Kryo kryo, Output output, ChaliceComponent object) {
				output.writeBoolean(object.filled);
				kryo.writeClassAndObject(output, object.aura);
			}

			@Override
			public ChaliceComponent read(Kryo kryo, Input input, Class<ChaliceComponent> type) {
				ChaliceComponent compo = engine.createComponent(ChaliceComponent.class);
				compo.filled = input.readBoolean();
				compo.setAura((AnimatedImage) kryo.readClassAndObject(input));
				return compo;
			}
		
		};
	}


	


}
