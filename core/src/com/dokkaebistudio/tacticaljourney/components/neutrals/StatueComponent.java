package com.dokkaebistudio.tacticaljourney.components.neutrals;

import java.util.List;

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
import com.dokkaebistudio.tacticaljourney.GameScreen;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing.BlessingsEnum;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;
import com.dokkaebistudio.tacticaljourney.alterations.Curse.CursesEnum;
import com.dokkaebistudio.tacticaljourney.alterations.pools.AlterationPool;
import com.dokkaebistudio.tacticaljourney.components.display.GridPositionComponent;
import com.dokkaebistudio.tacticaljourney.components.interfaces.MarkerInterface;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent;
import com.dokkaebistudio.tacticaljourney.components.player.AlterationReceiverComponent.AlterationActionEnum;
import com.dokkaebistudio.tacticaljourney.util.AnimatedImage;
import com.dokkaebistudio.tacticaljourney.util.Mappers;
import com.dokkaebistudio.tacticaljourney.util.PoolableVector2;
import com.dokkaebistudio.tacticaljourney.util.TileUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is a statue.
 * @author Callil
 *
 */
public class StatueComponent implements Component, Poolable, MarkerInterface {

	/** Whether this statue still has a blessing to offer. */
	private boolean hasBlessing = true;
	
	/** Whether this statue still has a curse to deliver when destroyed. */
	private boolean justDestroyed = false;
	
	/** The blessing to give. */
	private Blessing blessingToGive;
	
	/** The curse to give. */
	private Curse curseToGive;
	
	/** The pool of alterations that can be given. */
	private AlterationPool alterationPool;
	
	/** The holy aura the statue has when the blessing hasn't been received yet. */
	private AnimatedImage holyAura;
	

	
	@Override
	public void reset() {
		this.setHasBlessing(true);
		this.setJustDestroyed(false);
		setBlessingToGive(null);
		setCurseToGive(null);
		setHolyAura(null);
	}
	
	@Override
	public void showMarker(Entity e) {
		if (this.getHolyAura() != null) {
			GameScreen.fxStage.addActor(this.getHolyAura());
		}
	}
	
	@Override
	public void hideMarker() {
		if (this.getHolyAura() != null) {
			this.getHolyAura().remove();
		}
	}
	
	
	public void pray() {
		this.setHasBlessing(false);

		GridPositionComponent gridPositionComponent = Mappers.gridPositionComponent.get(GameScreen.player);
		PoolableVector2 playerPixelPos = TileUtil.convertGridPosIntoPixelPos(gridPositionComponent.coord());
		
		this.getHolyAura().setOrigin(Align.center);
		ScaleToAction scale = Actions.scaleTo(0f, 0f, 2f);
		MoveToAction move = Actions.moveTo(playerPixelPos.x, playerPixelPos.y, 2f, Interpolation.pow5);
		this.getHolyAura().addAction(Actions.sequence(Actions.parallel(scale, move), new Action() {
			@Override
			public boolean act(float delta) {
				removeHolyAura();
				
				AlterationReceiverComponent alterationReceiverComponent = Mappers.alterationReceiverComponent.get(GameScreen.player);
				alterationReceiverComponent.requestAction(AlterationActionEnum.RECEIVE_BLESSING, getBlessingToGive());		

				return true;
			}
		}));
	}
	
	//*********************************
	// Getters and Setters
	
	public AlterationPool getAlterationPool() {
		return alterationPool;
	}

	public void setAlterationPool(AlterationPool alterationPool) {
		this.alterationPool = alterationPool;
		List<BlessingsEnum> blessingType = alterationPool.getBlessingTypes(1);
		blessingToGive = Blessing.createBlessing(blessingType.get(0));
		blessingToGive.setInfused(true);
		
		List<CursesEnum> curseType = alterationPool.getCurseTypes(1);
		curseToGive = Curse.createCurse(curseType.get(0));
		curseToGive.setInfused(true);
	}

	public boolean isHasBlessing() {
		return hasBlessing;
	}

	public void setHasBlessing(boolean hasBlessing) {
		this.hasBlessing = hasBlessing;
	}

	public boolean wasJustDestroyed() {
		return justDestroyed;
	}

	public void setJustDestroyed(boolean justDestroyed) {
		this.justDestroyed = justDestroyed;
	}

	public Blessing getBlessingToGive() {
		return blessingToGive;
	}

	public void setBlessingToGive(Blessing blessingToGive) {
		this.blessingToGive = blessingToGive;
	}

	public Curse getCurseToGive() {
		return curseToGive;
	}

	public void setCurseToGive(Curse curseToGive) {
		this.curseToGive = curseToGive;
	}

	public AnimatedImage getHolyAura() {
		return holyAura;
	}

	public void setHolyAura(AnimatedImage holyAura) {
		this.holyAura = holyAura;
	}
	
	public void removeHolyAura() {
		if (this.holyAura != null) {
			this.holyAura.remove();
			this.holyAura = null;
		}
	}
	
	
	public static Serializer<StatueComponent> getSerializer(final PooledEngine engine) {
		return new Serializer<StatueComponent>() {

			@Override
			public void write(Kryo kryo, Output output, StatueComponent object) {
				output.writeBoolean(object.hasBlessing);
				kryo.writeClassAndObject(output, object.blessingToGive);
				kryo.writeClassAndObject(output, object.curseToGive);
				kryo.writeClassAndObject(output, object.holyAura);
			}

			@Override
			public StatueComponent read(Kryo kryo, Input input, Class<? extends StatueComponent> type) {
				StatueComponent compo = engine.createComponent(StatueComponent.class);
				compo.hasBlessing = input.readBoolean();
				compo.blessingToGive = (Blessing) kryo.readClassAndObject(input);
				compo.curseToGive = (Curse) kryo.readClassAndObject(input);
				compo.setHolyAura((AnimatedImage) kryo.readClassAndObject(input));
				return compo;
			}
		
		};
	}


}
