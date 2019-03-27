package com.dokkaebistudio.tacticaljourney.components.neutrals;

import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing;
import com.dokkaebistudio.tacticaljourney.alterations.Curse;
import com.dokkaebistudio.tacticaljourney.alterations.Blessing.BlessingsEnum;
import com.dokkaebistudio.tacticaljourney.alterations.Curse.CursesEnum;
import com.dokkaebistudio.tacticaljourney.alterations.pools.AlterationPool;
import com.dokkaebistudio.tacticaljourney.room.Floor;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Marker to indicate that this entity is a statue.
 * @author Callil
 *
 */
public class StatueComponent implements Component, Poolable {

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
	

	
	@Override
	public void reset() {
		this.setHasBlessing(true);
		this.setJustDestroyed(false);
		setBlessingToGive(null);
		setCurseToGive(null);
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

	
	
	public static Serializer<StatueComponent> getSerializer(final PooledEngine engine, final Floor floor) {
		return new Serializer<StatueComponent>() {

			@Override
			public void write(Kryo kryo, Output output, StatueComponent object) {
				output.writeBoolean(object.hasBlessing);
				kryo.writeClassAndObject(output, object.blessingToGive);
				kryo.writeClassAndObject(output, object.curseToGive);
				
				// TODO : private AlterationPool alterationPool;

			}

			@Override
			public StatueComponent read(Kryo kryo, Input input, Class<StatueComponent> type) {
				StatueComponent compo = engine.createComponent(StatueComponent.class);
				compo.hasBlessing = input.readBoolean();
				compo.blessingToGive = (Blessing) kryo.readClassAndObject(input);
				compo.curseToGive = (Curse) kryo.readClassAndObject(input);

				return compo;
			}
		
		};
	}
}
